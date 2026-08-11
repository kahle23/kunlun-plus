/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.vector.model.document;

import java.io.Serializable;
import java.util.List;

public class DocUpsertRequest implements Serializable {
    private String collection;
    private List<DocData> documents;
    private String configCode;

    public String getCollection() {

        return collection;
    }

    public void setCollection(String collection) {

        this.collection = collection;
    }

    public List<DocData> getDocuments() {

        return documents;
    }

    public void setDocuments(List<DocData> documents) {

        this.documents = documents;
    }

    public String getConfigCode() {

        return configCode;
    }

    public void setConfigCode(String configCode) {

        this.configCode = configCode;
    }

}
