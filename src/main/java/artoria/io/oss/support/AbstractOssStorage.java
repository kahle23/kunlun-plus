package artoria.io.oss.support;

import artoria.common.Constants;
import artoria.data.tuple.KeyValue;
import artoria.exception.ExceptionUtils;
import artoria.io.oss.OssBase;
import artoria.io.oss.OssInfo;
import artoria.io.oss.OssObject;
import artoria.io.oss.OssStorage;
import artoria.io.storage.AbstractDataStorage;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * The abstract oss storage.
 * @author Kahle
 */
public abstract class AbstractOssStorage extends AbstractDataStorage implements OssStorage {
    private static final Logger log = LoggerFactory.getLogger(AbstractOssStorage.class);
    private final Map<String, String> objectUrlPrefixes;
    private final String defaultBucket;

    public AbstractOssStorage(Map<String, String> objectUrlPrefixes, String defaultBucket) {
        Assert.notNull(objectUrlPrefixes, "Parameter \"objectUrlPrefixes\" must not null. ");
        this.objectUrlPrefixes = objectUrlPrefixes;
        this.defaultBucket = defaultBucket;
    }

    public String getDefaultBucket() {
        Assert.notBlank(defaultBucket, "Parameter \"defaultBucket\" is not set. ");
        return defaultBucket;
    }

    protected Map<String, String> getObjectUrlPrefixes() {

        return objectUrlPrefixes;
    }

    protected OssBase getOssBase(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        if (key instanceof OssBase) {
            OssBase ossBase = (OssBase) key;
            Assert.notBlank(ossBase.getBucketName(), "Parameter \"bucketName\" must not blank. ");
            Assert.notNull(ossBase.getObjectKey(), "Parameter \"objectKey\" must not blank. ");
            return ossBase;
        }
        else if (key instanceof String) {
            OssBaseImpl ossBase = new OssBaseImpl();
            ossBase.setBucketName(getDefaultBucket());
            ossBase.setObjectKey(String.valueOf(key));
            return ossBase;
        }
        else {
            throw new IllegalArgumentException("Parameter \"key\" is not supported. ");
        }
    }

    protected OssObject convertToOssObject(Object data) {
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        OssObject ossObject;
        if (data instanceof OssObject) {
            ossObject = (OssObject) data;
        }
        else if (data instanceof KeyValue) {
            try {
                @SuppressWarnings("rawtypes")
                KeyValue keyValue = (KeyValue) data;
                Object value = keyValue.getValue();
                Object key = keyValue.getKey();
                OssBase ossBase = getOssBase(key);
                InputStream inputStream = convertToStream(value, Constants.UTF_8);
                ossObject = new OssObjectImpl(ossBase.getBucketName(), ossBase.getObjectKey());
                ((OssObjectImpl) ossObject).setObjectContent(inputStream);
            }
            catch (IOException e) {
                throw ExceptionUtils.wrap(e);
            }
        }
        else {
            throw new IllegalArgumentException("Parameter \"data\" is not supported. ");
        }
        return ossObject;
    }

    protected String mergePrefixAndKey(String urlPrefix, String objectKey) {
        if (!urlPrefix.endsWith(Constants.SLASH) &&
                !objectKey.startsWith(Constants.SLASH)) {
            return urlPrefix + Constants.SLASH + objectKey;
        }
        else { return urlPrefix + objectKey; }
    }

    protected String buildObjectUrl(String bucketName, String objectKey) {
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notBlank(objectKey, "Parameter \"objectKey\" must not blank. ");
        String urlPrefix = objectUrlPrefixes.get(bucketName);
        // if prefix is blank, do not build object url.
        if (StringUtils.isBlank(urlPrefix)) { return null; }
        return mergePrefixAndKey(urlPrefix, objectKey);
    }

    protected OssInfo buildOssInfo(String bucketName, String objectKey, String objectUrl, Object original) {
        OssInfoImpl ossInfo = new OssInfoImpl(bucketName, objectKey, objectUrl);
        ossInfo.setOriginal(original);
        String buildObjectUrl = buildObjectUrl(bucketName, objectKey);
        ossInfo.setObjectUrl(StringUtils.isNotBlank(buildObjectUrl) ? buildObjectUrl : objectUrl);
        return ossInfo;
    }

}
