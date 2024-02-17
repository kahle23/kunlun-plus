/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.io.oss.support;

import kunlun.io.oss.OssInfo;

public class OssInfoImpl implements OssInfo {
    private String bucketName;
    private String objectKey;
    private String objectUrl;
    private Object original;

    public OssInfoImpl(String bucketName, String objectKey, String objectUrl) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.objectUrl = objectUrl;
    }

    public OssInfoImpl(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public OssInfoImpl() {

    }

    @Override
    public String getBucketName() {

        return bucketName;
    }

    public void setBucketName(String bucketName) {

        this.bucketName = bucketName;
    }

    @Override
    public String getObjectKey() {

        return objectKey;
    }

    public void setObjectKey(String objectKey) {

        this.objectKey = objectKey;
    }

    @Override
    public String getObjectUrl() {

        return objectUrl;
    }

    public void setObjectUrl(String objectUrl) {

        this.objectUrl = objectUrl;
    }

    @Override
    public Object getOriginal() {

        return original;
    }

    public void setOriginal(Object original) {

        this.original = original;
    }

}
