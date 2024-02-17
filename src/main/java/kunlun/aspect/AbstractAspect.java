/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.aspect;

import kunlun.util.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public abstract class AbstractAspect {

    protected List<Object> getArguments(JoinPoint joinPoint, Class<?>... ignoreTypes) {
        if (ignoreTypes == null) { ignoreTypes = new Class<?>[]{}; }
        if (joinPoint == null) { return null; }
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isEmpty(args)) {
            return Collections.emptyList();
        }
        List<Object> result = new ArrayList<Object>();
        for (Object arg : args) {
            boolean isIgnore = false;
            for (Class<?> ignoreType : ignoreTypes) {
                // if null, is not instance, no NPE.
                if (ignoreType.isInstance(arg)) {
                    isIgnore = true;
                    break;
                }
            }
            if (!isIgnore) { result.add(arg); }
        }
        return result;
    }

    protected Method getMethod(JoinPoint joinPoint) {
        if (joinPoint == null) { return null; }
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return null;
        }
        MethodSignature methodSign = (MethodSignature) signature;
        return methodSign.getMethod();
    }

    protected abstract void handle(JoinPoint joinPoint, Long timeSpent, Object result, Throwable th);

}
