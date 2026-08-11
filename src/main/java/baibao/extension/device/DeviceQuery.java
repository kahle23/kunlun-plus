/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.device;

import java.io.Serializable;

public class DeviceQuery implements Serializable {
    private String model;
    private String type;
    private String language;

    public DeviceQuery(String model) {

        this.model = model;
    }

    public DeviceQuery() {

    }

    public String getModel() {

        return model;
    }

    public void setModel(String model) {

        this.model = model;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getLanguage() {

        return language;
    }

    public void setLanguage(String language) {

        this.language = language;
    }

}
