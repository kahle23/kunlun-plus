/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.tool.weather;

import java.io.Serializable;

/**
 * The weather query object.
 * @author Kahle
 */
public class WeatherQuery implements Serializable {
    private String locationCode;
    private String location;

    public WeatherQuery(String location) {

        this.location = location;
    }

    public WeatherQuery() {

    }

    public String getLocation() {

        return location;
    }

    public void setLocation(String location) {

        this.location = location;
    }

    public String getLocationCode() {

        return locationCode;
    }

    public void setLocationCode(String locationCode) {

        this.locationCode = locationCode;
    }
}
