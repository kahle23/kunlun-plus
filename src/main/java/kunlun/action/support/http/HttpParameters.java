/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.http;

import java.io.Serializable;

public class HttpParameters implements Serializable {
    private String charset;
    private String url;
    private Integer method;
    private Object body;

    public String getCharset() {

        return charset;
    }

    public void setCharset(String charset) {

        this.charset = charset;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {

        this.url = url;
    }

    public Integer getMethod() {

        return method;
    }

    public void setMethod(Integer method) {

        this.method = method;
    }

    public Object getBody() {

        return body;
    }

    public void setBody(Object body) {

        this.body = body;
    }

}
