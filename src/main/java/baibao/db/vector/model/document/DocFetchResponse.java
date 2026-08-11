/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.vector.model.document;

import java.io.Serializable;
import java.util.Map;

public class DocFetchResponse implements Serializable {
    private Map<String, DocData> documents;
    private String collection;

    public DocFetchResponse(String collection, Map<String, DocData> documents) {
        this.collection = collection;
        this.documents = documents;
    }

    public DocFetchResponse() {

    }

    public String getCollection() {

        return collection;
    }

    public void setCollection(String collection) {

        this.collection = collection;
    }

    public Map<String, DocData> getDocuments() {

        return documents;
    }

    public void setDocuments(Map<String, DocData> documents) {

        this.documents = documents;
    }

}
