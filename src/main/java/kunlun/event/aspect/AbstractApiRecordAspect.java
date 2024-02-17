/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.event.aspect;

import kunlun.aspect.AbstractAspect;
import kunlun.data.json.JsonUtils;
import kunlun.event.annotation.ApiRecord;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

public abstract class AbstractApiRecordAspect extends AbstractAspect {
    private static Class<?>[] ignoreTypes = new Class[]{
            HttpServletRequest.class, HttpServletResponse.class, MultipartFile.class };
    private static Logger log = LoggerFactory.getLogger(AbstractApiRecordAspect.class);

    @Override
    protected void handle(JoinPoint joinPoint, Long timeSpent, Object result, Throwable th) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSign = (MethodSignature) signature;
        Method method = methodSign.getMethod();

        ApiRecord apiRecord = method.getAnnotation(ApiRecord.class);
        String apiName = apiRecord.name();
        boolean input = apiRecord.input();
        boolean output = apiRecord.output();
        boolean print = apiRecord.print();
        boolean record = apiRecord.record();

        List<Object> args = getArguments(joinPoint, ignoreTypes);
        if (input&&print) {
            log.info("The api named \"{}\"'s input parameters is: {}", apiName, JsonUtils.toJsonString(args));
        }
        if (output&&print) {
            log.info("The api named \"{}\"'s output values is: {}", apiName, JsonUtils.toJsonString(result));
        }

    }

}
