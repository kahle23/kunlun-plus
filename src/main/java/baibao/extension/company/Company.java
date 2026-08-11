/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.company;

import java.io.Serializable;
import java.util.Date;

/**
 * The company information.
 * @author Kahle
 */
public class Company implements Serializable {
    private String id;
    private String name;
    private String type;
    /**
     * Unify the social credit code.
     */
    private String code;
    private String logo;
    private String telephone;
    private String email;
    private String website;
    private Date   establishTime;
    private String registeredAddress;
    private String intro;

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

    public String getCode() {

        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    public String getLogo() {

        return logo;
    }

    public void setLogo(String logo) {

        this.logo = logo;
    }

    public String getTelephone() {

        return telephone;
    }

    public void setTelephone(String telephone) {

        this.telephone = telephone;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getWebsite() {

        return website;
    }

    public void setWebsite(String website) {

        this.website = website;
    }

    public Date getEstablishTime() {

        return establishTime;
    }

    public void setEstablishTime(Date establishTime) {

        this.establishTime = establishTime;
    }

    public String getRegisteredAddress() {

        return registeredAddress;
    }

    public void setRegisteredAddress(String registeredAddress) {

        this.registeredAddress = registeredAddress;
    }

    public String getIntro() {

        return intro;
    }

    public void setIntro(String intro) {

        this.intro = intro;
    }

}
