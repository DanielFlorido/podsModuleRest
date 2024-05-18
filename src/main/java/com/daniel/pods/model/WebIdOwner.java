package com.daniel.pods.model;

import com.inrupt.client.webid.WebIdProfile;
import com.inrupt.rdf.wrapping.commons.ValueMappings;
import com.inrupt.rdf.wrapping.commons.WrapperIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;

import java.net.URI;

public class WebIdOwner extends WebIdProfile {
    private final IRI vcardName;
    private String token;

    public WebIdOwner(final URI identifier, final Dataset dataset) {
        super(identifier, dataset);
        this.vcardName = rdf.createIRI(Vocabulary.FN);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    private String getVCARDName() {
        return new Node(rdf.createIRI(getIdentifier().toString()), getGraph()).getVCARDName();
    }

    public String geUserName() {
        return getVCARDName() != null ? getVCARDName() : getWebid();
    }

    private String getWebid() {
        return this.getIdentifier().toString();
    }

    class Node extends WrapperIRI{
        Node(final RDFTerm original, final Graph graph){
            super(original,graph);
        }
        String getVCARDName(){
            return anyOrNull(vcardName, ValueMappings::literalAsString);
        }
    }
}
