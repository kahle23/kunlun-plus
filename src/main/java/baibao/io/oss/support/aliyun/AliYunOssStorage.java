package baibao.io.oss.support.aliyun;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import kunlun.action.ActionUtil;
import kunlun.common.constant.Charsets;
import kunlun.data.Dict;
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static kunlun.common.constant.Charsets.UTF_8;
import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Symbols.*;

public class AliYunOssStorage extends AbstractOssStorage {
    private static final Logger log = LoggerFactory.getLogger(AliYunOssStorage.class);
    private final OSS ossClient;
    private final String endpoint;
    private final String accessKeyId;

    public AliYunOssStorage(OSS ossClient, String endpoint, String accessKeyId,
                            Map<String, String> objectUrlPrefixes, String defaultBucket) {
        super(objectUrlPrefixes, defaultBucket);
        this.ossClient = Assert.notNull(ossClient);
        this.endpoint = Assert.notBlank(endpoint);
        this.accessKeyId = Assert.notBlank(accessKeyId);
        initializeHeaders();
    }

    private static final List<String> OSS_HEADER_LIST = new ArrayList<String>();

    private void initializeHeaders() {
        try {
            Field[] fields = OSSHeaders.class.getFields();
            for (Field field : fields) {
                Object obj = field.get(null);
                String value = String.valueOf(obj);
                value = value.trim().toLowerCase();
                OSS_HEADER_LIST.add(value);
            }
        }
        catch (Exception e) {
            throw ExceptionUtil.wrap(e);
        }
    }

    private ObjectMetadata convert(Map<String, Object> metadata) {
        if (metadata == null) { return null; }
        ObjectMetadata result = new ObjectMetadata();
        if (MapUtil.isEmpty(metadata)) { return result; }
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (StrUtil.isBlank(key)) { continue; }
            if (value == null) { continue; }
            String tmpKey = key.trim().toLowerCase();
            if (OSS_HEADER_LIST.contains(tmpKey)) {
                result.setHeader(key, value);
            }
            else {
                result.addUserMetadata(key, String.valueOf(value));
            }
        }
        return result;
    }

    @Override
    public OSS getNative() {

        return ossClient;
    }

    /**
     * 判断该 MIME 类型是否通常应内联显示。
     */
    private static boolean shouldInline(String mimeType) {
        // 未知类型默认为附件下载
        if (mimeType == null) { return false; }
        // 常见的可内联类型：图片、PDF、文本、HTML、JSON、XML等
        return mimeType.startsWith("image/") ||
                mimeType.startsWith("text/") ||
                "application/pdf".equals(mimeType) ||
                "application/json".equals(mimeType) ||
                mimeType.startsWith("video/") ||
                mimeType.contains("xml");
    }

    /**
     * 根据浏览器类型对文件名进行编码，以兼容不同浏览器（尤其是中文名）。
     */
    private static String encodeFilename(String filename/*, HttpServletRequest request*/) {
        String encodedFilename;
        try {
            // Chrome、Safari、Edge 等现代浏览器，使用 URL 编码
            encodedFilename = URLEncoder.encode(filename, Charsets.STR_UTF_8);
            // 如果没有 User-Agent 信息，默认使用 URL 编码
//            encodedFilename = URLEncoder.encode(filename, Charsets.STR_UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw ExceptionUtil.wrap(e);
        }
        return encodedFilename;
    }

    /**
     * 根据 MIME 类型和文件名，生成合适的 Content-Disposition 头值。
     *
     * @param mimeType 文件的 MIME 类型，例如 "image/png", "application/pdf", "text/plain"
     * @param filename 原始文件名
//     * @param request HttpServletRequest 对象，用于判断浏览器类型
     * @return 组装好的 Content-Disposition 字符串
     */
    public static String buildDisposition(String mimeType, String filename/*, HttpServletRequest request*/) {
        // 1. 判断是内联 (inline) 还是附件 (attachment)
        String dispositionType = shouldInline(mimeType) ? "inline" : "attachment";

        // 2. 对文件名进行编码，解决中文或特殊字符问题
        String encodedFilename = encodeFilename(filename/*, request*/);

        // 3. 组装最终的 Content-Disposition 头值
        return dispositionType + "; filename=\"" + encodedFilename + "\"";
    }

    @Override
    public Map<String, Object> createUploadSign(String bucketName, Map<String, Object> params) {
        // https://help.aliyun.com/zh/oss/use-cases/uploading-objects-to-oss-directly-from-clients/#36c322a437r3k
        try {
            // Process parameters.
            bucketName = Assert.notBlank(StrUtil.isNotBlank(bucketName) ? bucketName : getDefaultBucketName());
            long expireTime = Convert.toLong(params.get("expireTime"), 3600L);
            Date expiration = new Date(System.currentTimeMillis() + expireTime * 1000);
            String hostPrefix = Convert.toStr(params.get("hostPrefix"), "https://");
            String host = hostPrefix + bucketName + DOT + endpoint;
            // 原始文件名，尽管 dir 中已经含有 objectKey 了，但是希望下载的时候，可以自动命名成原始文件名
            // TODO 这个方案是不行的
//            String originalFilename = Convert.toStr(params.get("originalFilename"));
            // Equivalent to more than half the object key
            String dir = Assert.notBlank(Convert.toStr(params.get("dir")));
            // Build policy.
            PolicyConditions conditions = new PolicyConditions();
            conditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            conditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            // 增加 Content-Disposition 逻辑  // TODO 这个方案是不行的
//            String disposition = null;
//            if (StrUtil.isNotBlank(originalFilename)) {
//                String mimeType = ActionUtil.execute("media-type", originalFilename);
//                disposition = buildDisposition(mimeType, originalFilename);
//                if (StrUtil.isNotBlank(disposition)) {
//                    disposition = disposition.replaceAll("\"", "\\\\\"");
////                    conditions.addConditionItem(PolicyConditions.COND_CONTENT_DISPOSITION, disposition);
//                }
//            }
            String postPolicy = ossClient.generatePostPolicy(expiration, conditions);
            // Encode policy.
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            // Calculate signature.
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            // Get url prefix.
            String urlPrefix = getObjectUrlPrefixes().get(bucketName);
            if (StrUtil.isBlank(urlPrefix)) { urlPrefix = host; }
            // Build result.
            Dict result = Dict.of();
            result.put("ossAccessKeyId", accessKeyId);
            result.put("policy",    encodedPolicy);
            result.put("signature", postSignature);
            result.put("urlPrefix", urlPrefix);
            result.put("host", host);
            result.put("dir",  dir);
            return result;
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

    @Override
    public String createSignedUrl(String bucketName, String objectKey, Date expireTime, Map<String, Object> others) {
        // https://help.aliyun.com/zh/oss/developer-reference/download-using-a-presigned-url
        if (expireTime == null) { expireTime = new Date(System.currentTimeMillis() + 600000L); }
        if (StrUtil.isBlank(bucketName)) { bucketName = Assert.notBlank(getDefaultBucketName()); }
        if (Assert.notBlank(objectKey).startsWith(SLASH)) { objectKey = objectKey.substring(ONE); }
        if (others == null) { others = Collections.emptyMap(); }
        Dict othersDict = Dict.of(others);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey);
        request.setExpiration(expireTime);
        // 图片缩放 https://help.aliyun.com/zh/oss/resize-images-4
        String process = othersDict.getString("process");
        if (StrUtil.isNotBlank(process)) { request.setProcess(process); }

        // 原始文件名处理 ： https://help.aliyun.com/zh/oss/set-the-file-name-for-downloading-an-oss-file
        String originalFilename = othersDict.getString("originalFilename");
        if (StrUtil.isNotBlank(originalFilename)) {
            String mimeType = ActionUtil.execute("media-type", originalFilename);
            String disposition = buildDisposition(mimeType, originalFilename);
            if (StrUtil.isNotBlank(disposition)) {
//                disposition = disposition.replaceAll("\"", "\\\\\"");
                request.getResponseHeaders().setContentDisposition(disposition);
            }
        }

        URL url = ossClient.generatePresignedUrl(request);
        return String.valueOf(Assert.notNull(url));
    }

    @Override
    public String createSignedUrl(String unsignedUrl, Date expireTime, Map<String, Object> others) {
        if (StrUtil.isBlank(unsignedUrl)) { return unsignedUrl; }
        try {
            URL url = new URL(unsignedUrl);
            String urlPrefix = url.getProtocol() + "://" + url.getAuthority();
            String objectKey = url.getPath();
            // Get bucket name.
            String bucketName = null;
            for (Map.Entry<String, String> entry : getObjectUrlPrefixes().entrySet()) {
                if (urlPrefix.equals(entry.getValue())) {
                    bucketName = entry.getKey();
                    break;
                }
            }
            // Sign url.
            String signedUrl = createSignedUrl(bucketName, objectKey, expireTime, others);
            String signedQuery = new URL(signedUrl).getQuery();
            if (unsignedUrl.contains(QUESTION_MARK)) {
                return unsignedUrl + AMPERSAND + signedQuery;
            } else { return unsignedUrl + QUESTION_MARK + signedQuery; }
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

    @Override
    public String removeUrlSign(String objectUrl) {
        if (StrUtil.isBlank(objectUrl)) { return objectUrl; }
        try {
            URL url = new URL(objectUrl);
            String urlPrefix = url.getProtocol() + "://" + url.getAuthority();
            Dict urlQueryMap = Dict.of(UrlQuery.of(url.getQuery(), UTF_8).getQueryMap());
            urlQueryMap.delete("Expires", "OSSAccessKeyId", "Signature", "x-oss-process", "response-content-disposition");
            String urlQueryStr = UrlQuery.of(urlQueryMap).build(UTF_8);
            urlQueryStr = StrUtil.isNotBlank(urlQueryStr) ? QUESTION_MARK + urlQueryStr : EMPTY_STRING;
            return urlPrefix + url.getPath() + urlQueryStr;
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

    @Override
    public boolean exist(Object key) {

        return super.exist(key);
    }

    @Override
    public OssObject get(Object key) {
        OssBase ossBase = getOssBase(key);
        String bucketName = ossBase.getBucketName();
        String objectKey = ossBase.getObjectKey();
        OSSObject ossObject = ossClient.getObject(bucketName, objectKey);
        if (ossObject == null) { return null; }
        OssObjectImpl result = new OssObjectImpl();
        result.setBucketName(ossObject.getBucketName());
        result.setObjectKey(ossObject.getKey());
        result.setMetadata(ossObject.getObjectMetadata().getRawMetadata());
        result.setObjectContent(ossObject.getObjectContent());
        return result;
    }

    @Override
    public OssInfo put(Object data) {
        OssObject ossObject = convertToOssObject(data);
        InputStream inputStream = null;
        try {
            String bucketName = ossObject.getBucketName();
            String objectKey = ossObject.getObjectKey();
            inputStream = ossObject.getObjectContent();
//            Object metadata = ossObject.getMetadata();
            Dict metadata = Dict.of((Map<?, ?>) ossObject.getMetadata());
            String originalFilename;
            if (StrUtil.isNotBlank(originalFilename = metadata.getString("originalFilename"))) {
                String mimeType = ActionUtil.execute("media-type", originalFilename);
                String disposition = buildDisposition(mimeType, originalFilename);
                if (StrUtil.isNotBlank(disposition)) {
                    metadata.put(OSSHeaders.CONTENT_DISPOSITION, disposition);
                }
                metadata.remove("originalFilename");
            }
            PutObjectResult putObjectResult = ossClient
                    .putObject(bucketName, objectKey, inputStream, convert((Map<String, Object>) metadata));
            return buildOssInfo(bucketName, objectKey, null, putObjectResult);
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
        OssBase ossBase = getOssBase(key);
        String bucketName = ossBase.getBucketName();
        String objectKey = ossBase.getObjectKey();
        ossClient.deleteObject(bucketName, objectKey);
        return null;
    }

    @Override
    public Object list(Object conditions) {
        // todo ossClient listObjects
        return super.list(conditions);
    }

}
