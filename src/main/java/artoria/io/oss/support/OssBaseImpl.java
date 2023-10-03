package artoria.io.oss.support;

import artoria.io.oss.OssBase;

public class OssBaseImpl implements OssBase {
    private String bucketName;
    private String objectKey;

    public OssBaseImpl(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public OssBaseImpl() {

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

}
