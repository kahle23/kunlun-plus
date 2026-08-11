/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.vector.model.document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DocData implements Serializable {
    private Map<Object, Object> data;
    private List<Object> vector;
    private String id;

    public DocData(String id, List<Object> vector, Map<Object, Object> data) {
        this.vector = vector;
        this.data = data;
        this.id = id;
    }

    public DocData() {

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

    public Map<Object, Object> getData() {

        return data;
    }

    public void setData(Map<Object, Object> data) {

        this.data = data;
    }

}
