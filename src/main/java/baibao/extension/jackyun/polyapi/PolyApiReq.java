/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.jackyun.polyapi;

import java.io.Serializable;

/**
 * 菠萝派平台的参数请求对象.
 * @author Kahle
 */
public class PolyApiReq implements Serializable {
    /**
     * 方法名称.
     * 示例值：Differ.JH.Business.GetOrder
     */
    private String method;
    /**
     * 应用编号.
     * 示例值：438b2f6ff103422a98a9349507293bb2
     */
    private String appKey;
    /**
     * 访问令牌.
     * 示例值：9415c33b04d24c7dae320b0185f42fb0
     */
    private String token;
    /**
     * 私有请求参数 Json 格式串.
     * 示例值：{}
     */
    private String bizContent;
    /**
     * 签名串.
     * 示例值：deerde15w5622s6w9552x2d25w5e8e55dd2d255essw
     */
    private String sign;
    /**
     * 计算的签名.
     */
    private String calcSign;

    public String getMethod() {

        return method;
    }

    public void setMethod(String method) {

        this.method = method;
    }

    public String getAppKey() {

        return appKey;
    }

    public void setAppKey(String appKey) {

        this.appKey = appKey;
    }

    public String getToken() {

        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public String getBizContent() {

        return bizContent;
    }

    public void setBizContent(String bizContent) {


        this.bizContent = bizContent;
    }

    public String getSign() {

        return sign;
    }

    public void setSign(String sign) {

        this.sign = sign;
    }

    public String getCalcSign() {

        return calcSign;
    }

    public void setCalcSign(String calcSign) {

        this.calcSign = calcSign;
    }

}
