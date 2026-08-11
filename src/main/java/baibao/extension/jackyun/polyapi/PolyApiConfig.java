/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.jackyun.polyapi;

import java.io.Serializable;

/**
 * 菠萝派平台的配置类.
 * @author Kahle
 */
public class PolyApiConfig implements Serializable {
    /**
     * 菠萝派平台的推送接口地址.
     */
    private String address;
    /**
     * 用户在菠萝派平台申请应用时获得的应用编号.
     */
    private String appKey;
    /**
     * 用户在菠萝派平台申请应用时获得的应用密钥.
     */
    private String appSecret;
    /**
     * 用户店铺的访问令牌.
     */
    private String token;
    /**
     * 管家会员名 / 吉客号.
     * 文档中的字段为：uerName
     */
    private String username;
    /**
     * 推送方枚举.
     *
     * 推送地址枚举说明：
     * Poly_JACKYUNOPEN：吉客云开放平台
     * WDGJ_MESSAGE：管家消息系统（目前推送管家选择这个）
     * Poly_API：新API
     */
    private String targetType;

    public PolyApiConfig(String address, String appKey, String appSecret) {
        this.appSecret = appSecret;
        this.address = address;
        this.appKey = appKey;
    }

    public PolyApiConfig(String appKey, String appSecret) {
        this.appSecret = appSecret;
        this.appKey = appKey;
    }

    public PolyApiConfig() {

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

    public String getToken() {

        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getTargetType() {

        return targetType;
    }

    public void setTargetType(String targetType) {

        this.targetType = targetType;
    }

}
