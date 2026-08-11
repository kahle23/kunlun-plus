/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.vector.model.document;

import java.io.Serializable;
import java.util.List;

public class DocDeleteRequest implements Serializable {
    private String collection;
    private List<String> ids;
    private Boolean deleteAll;
    private Object  filter;
    private String  configCode;

    public String getCollection() {

        return collection;
    }

    public void setCollection(String collection) {

        this.collection = collection;
    }

    public List<String> getIds() {

        return ids;
    }

    public void setIds(List<String> ids) {

        this.ids = ids;
    }

    public Boolean getDeleteAll() {

        return deleteAll;
    }

    public void setDeleteAll(Boolean deleteAll) {

        this.deleteAll = deleteAll;
    }

    public Object getFilter() {

        return filter;
    }

    public void setFilter(Object filter) {

        this.filter = filter;
    }

    public String getConfigCode() {

        return configCode;
    }

    public void setConfigCode(String configCode) {

        this.configCode = configCode;
    }

}
