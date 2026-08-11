/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.io.oss.support.minio;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.ErrorResponse;
import io.minio.messages.Item;
import kunlun.exception.ExceptionUtil;
import kunlun.io.oss.OssBase;
import kunlun.io.oss.OssInfo;
import kunlun.io.oss.OssObject;
import kunlun.io.oss.support.AbstractOssStorage;
import kunlun.io.oss.support.OssObjectImpl;
import kunlun.io.util.IoUtil;
import kunlun.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

public class MinioStorage extends AbstractOssStorage {
    private static final Logger log = LoggerFactory.getLogger(MinioStorage.class);
    private final MinioClient minioClient;
    private final Long partSize;

    public MinioStorage(MinioClient minioClient, Map<String, String> objectUrlPrefixes,
                        Long partSize, String defaultBucket) {
        super(objectUrlPrefixes, defaultBucket);
        Assert.notNull(minioClient, "Parameter \"minioClient\" must not null. ");
        if (partSize == null || partSize <= 0) { partSize = 5L * 1024 * 1024; }
        this.minioClient = minioClient;
        this.partSize = partSize;
    }

    public MinioStorage(MinioClient minioClient, Map<String, String> objectUrlPrefixes,
                        Long partSize) {

        this(minioClient, objectUrlPrefixes, partSize, null);
    }

    public MinioStorage(MinioClient minioClient, Map<String, String> objectUrlPrefixes) {

        this(minioClient, objectUrlPrefixes, null, null);
    }

    @Override
    public MinioClient getNative() {

        return minioClient;
    }

    @Override
    public boolean exist(Object key) {
        OssBase ossBase = getOssBase(key);
        String bucketName = ossBase.getBucketName();
        String objectKey = ossBase.getObjectKey();
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName).object(objectKey).build());
            return true;
        }
        catch (ErrorResponseException e) {
            ErrorResponse errorResponse = e.errorResponse();
            String code = errorResponse.code();
            if ("NoSuchKey".equals(code)) { return false; }
            else { throw ExceptionUtil.wrap(e); }
        }
        catch (Exception e) {
            throw ExceptionUtil.wrap(e);
        }
    }

    @Override
    public OssObject get(Object key) {
        OssBase ossBase = getOssBase(key);
        String bucketName = ossBase.getBucketName();
        String objectKey = ossBase.getObjectKey();
        GetObjectResponse objectResponse;
        try {
            objectResponse = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());
        }
        catch (Exception e) {
            throw ExceptionUtil.wrap(e);
        }
        if (objectResponse == null) { return null; }
        OssObjectImpl ossObject = new OssObjectImpl();
        ossObject.setBucketName(objectResponse.bucket());
        ossObject.setObjectKey(objectResponse.object());
        ossObject.setMetadata(objectResponse.headers());
        ossObject.setObjectContent(objectResponse);
        return ossObject;
    }

    @Override
    public OssInfo put(Object data) {
        OssObject ossObject = convertToOssObject(data);
        InputStream inputStream = null;
        try {
            inputStream = ossObject.getObjectContent();
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(ossObject.getBucketName())
                    .object(ossObject.getObjectKey())
                    .stream(inputStream, -1, partSize)
                    .build());
            String bucketName = objectWriteResponse.bucket();
            String objectKey = objectWriteResponse.object();
            return buildOssInfo(bucketName, objectKey, null, objectWriteResponse);
        }
        catch (Exception e) {
            throw ExceptionUtil.wrap(e);
        }
        finally {
            IoUtil.closeQuietly(inputStream);
        }
    }

    @Override
    public Object delete(Object key) {
        try {
            OssBase ossBase = getOssBase(key);
            String bucketName = ossBase.getBucketName();
            String objectKey = ossBase.getObjectKey();
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());
            return null;
        }
        catch (Exception e) {
            throw ExceptionUtil.wrap(e);
        }
    }

    @Override
    public Object list(Object conditions) {
        // todo minioClient keys
        ListObjectsArgs args = null;
        Iterable<Result<Item>> results = minioClient.listObjects(args);
        return results;
    }

}
