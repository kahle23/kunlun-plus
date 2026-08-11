/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.vector.model.document;

import java.io.Serializable;
import java.util.List;

public class DocQueryRequest implements Serializable {
    private String  collection;
    private String  id;
    private List<Object> vector;
    private Object  filter;
    private Integer topK;
    private Boolean includeVector;
    private String  configCode;

    public String getCollection() {

        return collection;
    }

    public void setCollection(String collection) {

        this.collection = collection;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public List<Object> getVector() {

        return vector;
    }

    public void setVector(List<Object> vector) {

        this.vector = vector;
    }

    public Object getFilter() {

        return filter;
    }

    public void setFilter(Object filter) {

        this.filter = filter;
    }

    public Integer getTopK() {

        return topK;
    }

    public void setTopK(Integer topK) {

        this.topK = topK;
    }

    public Boolean getIncludeVector() {

        return includeVector;
    }

    public void setIncludeVector(Boolean includeVector) {

        this.includeVector = includeVector;
    }

    public String getConfigCode() {

        return configCode;
    }

    public void setConfigCode(String configCode) {

        this.configCode = configCode;
    }

}
