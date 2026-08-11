/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.jackyun;

import java.io.Serializable;

/**
 * 吉客云开放平台的配置类.
 * @author Kahle
 */
public class JackYunConfig implements Serializable {
    /**
     * 吉客云开放平台调用地址.
     * 调用地址线上环境：https://open.jackyun.com/open/openapi/do
     */
    private String address;
    /**
     * 吉客云开放平台的应用编号.
     */
    private String appKey;
    /**
     * 吉客云开放平台的应用密钥.
     */
    private String appSecret;
    /**
     * 吉客云开放平台的接口版本号.
     */
    private String version;
    /**
     * 吉客云开放平台的返回格式(暂时只支持Json).
     */
    private String contentType;

    public JackYunConfig(String address, String appKey, String appSecret) {
        this.appSecret = appSecret;
        this.address = address;
        this.appKey = appKey;
    }

    public JackYunConfig(String appKey, String appSecret) {
        this.appSecret = appSecret;
        this.appKey = appKey;
    }

    public JackYunConfig() {

    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public String getAppKey() {

        return appKey;
    }

    public void setAppKey(String appKey) {

        this.appKey = appKey;
    }

    public String getAppSecret() {

        return appSecret;
    }

    public void setAppSecret(String appSecret) {

        this.appSecret = appSecret;
    }

    public String getVersion() {

        return version;
    }

    public void setVersion(String version) {

        this.version = version;
    }

    public String getContentType() {

        return contentType;
    }

    public void setContentType(String contentType) {

        this.contentType = contentType;
    }

}
