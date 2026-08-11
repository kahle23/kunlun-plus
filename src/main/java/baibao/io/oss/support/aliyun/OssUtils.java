package baibao.io.oss.support.aliyun;

import com.aliyun.oss.OSS;
import kunlun.common.constant.Nil;
import kunlun.io.oss.OssInfo;
import kunlun.io.oss.OssObject;
import kunlun.io.oss.OssStorage;
import kunlun.io.storage.StorageUtil;
import kunlun.util.ObjUtil;

import java.util.Date;
import java.util.Map;

public class OssUtils {
    public static final String STORAGE_NAME = "aliyun-oss";

    public static OSS getNative() {

        return (OSS) ((OssStorage) StorageUtil.getStorage(STORAGE_NAME)).getNative();
    }

    public static OssStorage getStorage() {

        return (OssStorage) StorageUtil.getStorage(STORAGE_NAME);
    }

    public static Map<String, Object> createUploadSign(String bucketName, Map<String, Object> params) {

        return getStorage().createUploadSign(bucketName, params);
    }

    public static String createSignedUrl(String bucketName, String objectKey, Date expireTime, Map<String, Object> others) {

        return getStorage().createSignedUrl(bucketName, objectKey, expireTime, others);
    }

    public static String createSignedUrl(String bucketName, String objectKey, Date expireTime) {

        return getStorage().createSignedUrl(bucketName, objectKey, expireTime, Nil.g());
    }

    public static String createSignedUrl(String unsignedUrl, Date expireTime, Map<String, Object> others) {

        return getStorage().createSignedUrl(unsignedUrl, expireTime, others);
    }

    public static String createSignedUrl(String unsignedUrl, Date expireTime) {

        return getStorage().createSignedUrl(unsignedUrl, expireTime, Nil.g());
    }

    public static String createSignedUrl(String unsignedUrl) {

        return getStorage().createSignedUrl(unsignedUrl, Nil.g(), Nil.g());
    }

    public static String removeUrlSign(String objectUrl) {

        return getStorage().removeUrlSign(objectUrl);
    }

    public static String refreshUrlSign(String objectUrl, Date expireTime, Map<String, Object> others) {

        return getStorage().refreshUrlSign(objectUrl, expireTime, others);
    }

    public static String refreshUrlSign(String objectUrl, Date expireTime) {

        return getStorage().refreshUrlSign(objectUrl, expireTime, Nil.g());
    }

    public static String refreshUrlSign(String objectUrl) {

        return getStorage().refreshUrlSign(objectUrl, Nil.g(), Nil.g());
    }

    public static boolean exist(Object key) {

        return StorageUtil.exist(STORAGE_NAME, key);
    }

    public static OssObject get(Object key) {

        return ObjUtil.cast(StorageUtil.get(STORAGE_NAME, key));
    }

    public static OssInfo put(Object data) {

        return ObjUtil.cast(StorageUtil.put(STORAGE_NAME, data));
    }

    public static OssInfo put(Object key, Object value) {

        return ObjUtil.cast(StorageUtil.put(STORAGE_NAME, key, value));
    }

    public static Object delete(Object key) {

        return StorageUtil.delete(STORAGE_NAME, key);
    }

}
