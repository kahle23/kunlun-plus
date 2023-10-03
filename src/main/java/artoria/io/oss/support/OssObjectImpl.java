package artoria.io.oss.support;

import artoria.io.oss.OssObject;

import java.io.InputStream;

public class OssObjectImpl implements OssObject {
    private String bucketName;
    private String objectKey;
    private Object metadata;
    private InputStream objectContent;

    public OssObjectImpl(String bucketName, String objectKey, Object metadata, InputStream objectContent) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.metadata = metadata;
        this.objectContent = objectContent;
    }

    public OssObjectImpl(String bucketName, String objectKey, InputStream objectContent) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.objectContent = objectContent;
    }

    public OssObjectImpl(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public OssObjectImpl() {

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
    public Object getMetadata() {

        return metadata;
    }

    public void setMetadata(Object metadata) {

        this.metadata = metadata;
    }

    @Override
    public InputStream getObjectContent() {

        return objectContent;
    }

    public void setObjectContent(InputStream objectContent) {

        this.objectContent = objectContent;
    }

}
