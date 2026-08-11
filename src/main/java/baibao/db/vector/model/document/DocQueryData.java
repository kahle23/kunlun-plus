/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.vector.model.document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DocQueryData extends DocData implements Serializable {
    private Object score;

    public DocQueryData(String id, List<Object> vector, Map<Object, Object> data) {

        super(id, vector, data);
    }

    public DocQueryData() {

    }

    public Object getScore() {

        return score;
    }

    public void setScore(Object score) {

        this.score = score;
    }

}
