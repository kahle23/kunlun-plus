/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.core.annotation;

import java.lang.annotation.*;

/**
 * The method (or API) permission control and interception.
 *
 * What is a permission code?
 * The permission code is a coded abstraction of a specific resource.
 * For example, "order_add" is a mapping of the "order add" operation.
 *
 * So the function of this annotation is to determine whether the current
 *      logged-in person has an access permission code, or restrict the
 *      data according to the rules corresponding to the logged-in person
 *      and data permission code.
 *
 * @see kunlun.core.DataController
 * @see kunlun.core.AccessController
 * @author Kahle
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    /**
     * Quickly fill in the access permission code and data permission code.
     * @return The permission code
     */
    String value() default "";

    /**
     * The access permission's permission code.
     * @return The permission code
     */
    String access() default "";

    /**
     * The data permission's permission code.
     * @return The permission code // todo 数据权限 因为是类似于改造 SQL 等，不太适合注解，目前碰到的是 注解一加，因为SQL查询多，可能部分SQL也挂上数据权限了，导致查询出问题了，类似于 pageHelper 的思路，再考虑考虑有没有别的方案
     */
    String data() default "";

    /**
     * The information about the configuration of access permission or data permission.
     * @return The configuration
     */
    String config() default "";

}
