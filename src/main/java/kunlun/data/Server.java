/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data;

import java.io.Serializable;

/**
 * Server.
 * @see <a href="https://en.wikipedia.org/wiki/Server_(computing)">Server (computing)</a>
 * @author Kahle
 */
@Deprecated
public class Server implements Serializable {
    private String id;
    private String name;
    private String type;
    private String description;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

}
