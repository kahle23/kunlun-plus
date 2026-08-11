/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.security.support.fill;

import kunlun.common.constant.Nil;
import kunlun.core.function.Function;
import kunlun.data.bean.BeanUtil;
import kunlun.data.fill.DataSupplier;
import kunlun.security.SecurityUtil;
import kunlun.security.UserDetail;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static kunlun.util.Assert.notNull;

/**
 * 基于 UserService 的 UserDetail 的数据提供者.
 * @author Kahle
 */
public class UserDetailSupplier implements DataSupplier {

    public static UserDetailSupplier of(Object userType, Function<UserDetail, Object> keyMapper) {

        return new UserDetailSupplier(userType, keyMapper);
    }

    public static UserDetailSupplier of(Function<UserDetail, Object> keyMapper) {

        return new UserDetailSupplier(keyMapper);
    }

    protected final Function<UserDetail, Object> keyMapper;
    protected final Object userType;

    public UserDetailSupplier(Object userType, Function<UserDetail, Object> keyMapper) {
        this.keyMapper = notNull(keyMapper);
        this.userType = userType;
    }

    public UserDetailSupplier(Function<UserDetail, Object> keyMapper) {

        this(Nil.OBJ, keyMapper);
    }

    @Override
    public Map<String, Map<String, Object>> acquire(Collection<?> coll) {
        Map<String, Map<String, Object>> resultMap = new LinkedHashMap<String, Map<String, Object>>();
        Collection<UserDetail> userDetails =
                SecurityUtil.getUserService().getUserDetails(coll, userType);
        for (UserDetail userDetail : userDetails) {
            if (userDetail == null) { continue; }
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            /*if (MapUtil.isNotEmpty(userDetail.getProperties())) {
                map = new LinkedHashMap<String, Object>(userDetail.getProperties());
            } else { map = new LinkedHashMap<String, Object>(); }*/
            map.putAll(BeanUtil.beanToMap(userDetail));
            resultMap.put(String.valueOf(keyMapper.apply(userDetail)), map);
        }
        return resultMap;
    }
}
