/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.cache.support.config;

import kunlun.data.ReferenceType;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class SimpleCacheConfig implements Serializable {
    private String name;
    private ReferenceType referenceType;
    private Long capacity;
    private Long timeToLive;
    private TimeUnit timeToLiveUnit;
    private Long timeToIdle;
    private TimeUnit timeToIdleUnit;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public ReferenceType getReferenceType() {

        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {

        this.referenceType = referenceType;
    }

    public Long getCapacity() {

        return capacity;
    }

    public void setCapacity(Long capacity) {

        this.capacity = capacity;
    }

    public Long getTimeToLive() {

        return timeToLive;
    }

    public void setTimeToLive(Long timeToLive) {

        this.timeToLive = timeToLive;
    }

    public TimeUnit getTimeToLiveUnit() {

        return timeToLiveUnit;
    }

    public void setTimeToLiveUnit(TimeUnit timeToLiveUnit) {

        this.timeToLiveUnit = timeToLiveUnit;
    }

    public Long getTimeToIdle() {

        return timeToIdle;
    }

    public void setTimeToIdle(Long timeToIdle) {

        this.timeToIdle = timeToIdle;
    }

    public TimeUnit getTimeToIdleUnit() {

        return timeToIdleUnit;
    }

    public void setTimeToIdleUnit(TimeUnit timeToIdleUnit) {

        this.timeToIdleUnit = timeToIdleUnit;
    }

}
