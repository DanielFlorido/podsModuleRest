package com.daniel.pods.controllers;

import com.daniel.pods.manager.SessionManager;
import com.daniel.pods.model.Vocabulary;
import com.daniel.pods.service.UserService;
import com.daniel.pods.starter.Expense;
import com.inrupt.client.accessgrant.AccessGrantClient;
import com.inrupt.client.auth.Session;
import com.inrupt.client.openid.OpenIdAuthenticationProvider;
import com.inrupt.client.openid.OpenIdProvider;
import com.inrupt.client.openid.OpenIdSession;
import com.inrupt.client.solid.SolidSyncClient;
import com.inrupt.client.webid.WebIdProfile;
import com.inrupt.client.solid.PreconditionFailedException;
import com.inrupt.client.solid.ForbiddenException;
import com.inrupt.client.solid.NotFoundException;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.rdf.api.RDFSyntax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Set;

@RequestMapping("/api")
@RestController
public class ExpenseController {    
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private UserService userService;
    /**
     * Note 1: Authenticated Session
     * Using the client credentials, create an authenticated session.
     */
    //final Session session = OpenIdSession.ofIdToken(userService.getCurrentUser().getToken());
    /**
     * Note 2: SolidSyncClient
     * Instantiates a synchronous client for the authenticated session.
     * The client has methods to perform CRUD operations.
     */
   // final SolidSyncClient client = userService.getClient().session(session);
    private final PrintWriter printWriter = new PrintWriter(System.out, true);

    /**
     * Note 3: SolidSyncClient.read()
     * Using the SolidSyncClient client.read() method, reads the user's WebID Profile document and returns the Pod URI(s).
     */
    @GetMapping("/pods")
    public Set<URI> getPods() {
        printWriter.println("ExpenseController:: getPods");
        try (final var profile = userService.getClient().read(URI.create(userService.getCurrentUser().geUserName()), WebIdProfile.class)) {
            return profile.getStorages();
        }
    }

    /**
     * Note 4: SolidSyncClient.create()
     * Using the SolidSyncClient client.create() method,
     * - Saves the Expense as an RDF resource to the location specified in the Expense.identifier field.
     */
    @PostMapping(path = "/expenses/create")
    public Expense createExpense(@RequestBody Expense newExpense) {
        printWriter.println("ExpenseController:: createExpense");
        //final AccessGrantClient accessGrantClient = new AccessGrantClient(Vocabulary.PS_ACCESS_GRANT_URI).session(sessionManager.getSession());

        try (var createdExpense = userService.getClient().create(newExpense)) {
            printExpenseAsTurtle(createdExpense);
            return createdExpense;
        } catch(PreconditionFailedException e1) {
            // Errors if the resource already exists
            printWriter.println(String.format("[%s] com.inrupt.client.solid.PreconditionFailedException:: %s", e1.getStatusCode(), e1.getMessage()));
        } catch(ForbiddenException e2) {
            // Errors if user does not have access to create
            printWriter.println(String.format("[%s] com.inrupt.client.solid.ForbiddenException:: %s", e2.getStatusCode(), e2.getMessage()));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Note 5: SolidSyncClient.read()
     * Using the SolidSyncClient client.read() method,
     * - Reads the RDF resource into the Expense class.
     */
    @GetMapping("/expenses/get")
    public Expense getExpense(@RequestParam(value = "resourceURL", defaultValue = "") String resourceURL) {
        printWriter.println("ExpenseController:: getExpense");
        try (var resource = userService.getClient().read(URI.create(resourceURL), Expense.class)) {
            return resource;
        } catch (NotFoundException e1) {
            // Errors if resource is not found
            printWriter.println(String.format("[%s] com.inrupt.client.solid.NotFoundException:: %s", e1.getStatusCode(), e1.getMessage()));
        } catch(ForbiddenException e2) {
            // Errors if user does not have access to read
            printWriter.println(String.format("[%s] com.inrupt.client.solid.ForbiddenException:: %s", e2.getStatusCode(), e2.getMessage()));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Note 6: SolidSyncClient.update()
     * Using the SolidSyncClient client.update() method,
     * - Updates the Expense resource.
     */
    @PutMapping("/expenses/update")
    public Expense updateExpense(@RequestBody Expense expense) {
        printWriter.println("ExpenseController:: updateExpense");

        try(var updatedExpense = userService.getClient().update(expense)) {
            printExpenseAsTurtle(updatedExpense);
            return updatedExpense;
        } catch (NotFoundException e1) {
            // Errors if resource is not found
            printWriter.println(String.format("[%s] com.inrupt.client.solid.NotFoundException:: %s", e1.getStatusCode(), e1.getMessage()));
        } catch(ForbiddenException e2) {
            // Errors if user does not have access to read
            printWriter.println(String.format("[%s] com.inrupt.client.solid.ForbiddenException:: %s", e2.getStatusCode(), e2.getMessage()));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Note 7: SolidSyncClient.delete()
     * Using the SolidSyncClient client.delete() method,
     * - Deletes the resource located at the resourceURL.
     */

    @DeleteMapping("/expenses/delete")
    public void deleteExpense(@RequestParam(value = "resourceURL") String resourceURL) {
        printWriter.println("ExpenseController:: deleteExpense");
        try {
            userService.getClient().delete(URI.create(resourceURL));

            // Alternatively, you can specify an Expense object to the delete method.
            // The delete method deletes  the Expense recorde located in the Expense.identifier field.
            // For example: client.delete(new Expense(URI.create(resourceURL)));
        } catch (NotFoundException e1) {
            // Errors if resource is not found
            printWriter.println(String.format("[%s] com.inrupt.client.solid.NotFoundException:: %s", e1.getStatusCode(), e1.getMessage()));
        } catch(ForbiddenException e2) {
            // Errors if user does not have access to read
            printWriter.println(String.format("[%s] com.inrupt.client.solid.ForbiddenException:: %s", e2.getStatusCode(), e2.getMessage()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Note 8: Prints the expense resource in Turtle.
     */

    private void printExpenseAsTurtle(Expense expense) {
        printWriter.println("ExpenseController:: printExpenseAsTurtle");
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        try  {
            expense.serialize(RDFSyntax.TURTLE, content);
            printWriter.println(content.toString("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
