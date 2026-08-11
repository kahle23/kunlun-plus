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
import kunlun.security.UserGroup;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static kunlun.util.Assert.notNull;

/**
 * 基于 UserService 的 UserGroup 的数据提供者.
 * @author Kahle
 */
public class UserGroupSupplier implements DataSupplier {

    public static UserGroupSupplier of(Object groupType, Function<UserGroup, Object> keyMapper) {

        return new UserGroupSupplier(groupType, keyMapper);
    }

    public static UserGroupSupplier of(Function<UserGroup, Object> keyMapper) {

        return new UserGroupSupplier(keyMapper);
    }

    protected final Function<UserGroup, Object> keyMapper;
    protected final Object groupType;

    public UserGroupSupplier(Object groupType, Function<UserGroup, Object> keyMapper) {
        this.keyMapper = notNull(keyMapper);
        this.groupType = groupType;
    }

    public UserGroupSupplier(Function<UserGroup, Object> keyMapper) {

        this(Nil.OBJ, keyMapper);
    }

    @Override
    public Map<String, Map<String, Object>> acquire(Collection<?> coll) {
        Map<String, Map<String, Object>> resultMap = new LinkedHashMap<String, Map<String, Object>>();
        Collection<UserGroup> userGroups =
                SecurityUtil.getUserService().getUserGroups(coll, groupType);
        for (UserGroup userGroup : userGroups) {
            if (userGroup == null) { continue; }
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            /*if (MapUtil.isNotEmpty(userDetail.getProperties())) {
                map = new LinkedHashMap<String, Object>(userDetail.getProperties());
            } else { map = new LinkedHashMap<String, Object>(); }*/
            map.putAll(BeanUtil.beanToMap(userGroup));
            resultMap.put(String.valueOf(keyMapper.apply(userGroup)), map);
        }
        return resultMap;
    }
}
