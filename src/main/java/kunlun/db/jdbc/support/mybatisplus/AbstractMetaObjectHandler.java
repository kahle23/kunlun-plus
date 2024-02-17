/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.db.jdbc.support.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import kunlun.exception.ExceptionUtils;
import kunlun.util.Assert;
import kunlun.util.ObjectUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static kunlun.convert.ConversionUtils.convert;

/**
 * The abstract meta object field fill handler.
 * @author Kahle
 */
public abstract class AbstractMetaObjectHandler implements MetaObjectHandler {
    private static final Logger log = LoggerFactory.getLogger(AbstractMetaObjectHandler.class);
    private final Map<String, Callable<Object>> commands = new ConcurrentHashMap<String, Callable<Object>>();
    private final Map<String, String> configs = new ConcurrentHashMap<String, String>();

    protected Map<String, Callable<Object>> getCommands() { return commands; }

    protected Map<String, String> getConfigs() { return configs; }

    /**
     * Register a command.
     * @param command The name of the command that is registered
     * @param callable The command logic that is registered
     */
    protected void registerCommand(String command, Callable<Object> callable) {
        Assert.notBlank(command, "Parameter \"command\" must not blank. ");
        Assert.notNull(callable, "Parameter \"callable\" must not null. ");
        this.commands.put(command, callable);
    }

    /**
     * Register a configuration.
     * @param fieldName The name of the field to be filled
     * @param command The command name corresponding to the field name
     */
    protected void registerConfig(String fieldName, String command) {
        Assert.notBlank(fieldName, "Parameter \"fieldName\" must not blank. ");
        Assert.notBlank(command, "Parameter \"command\" must not blank. ");
        this.configs.put(fieldName, command);
    }

    /**
     * Invoke a command.
     * @param command The name of the command to be invoked
     * @return The command invoke result
     */
    protected Object invokeCommand(String command) {
        Assert.notBlank(command, "Parameter \"command\" must not blank. ");
        Callable<Object> callable = commands.get(command);
        Assert.notNull(callable, "The command \"" + command + "\" is not supported. ");
        try {
            return callable.call();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    /**
     * Initialize the entire object.
     * It will be called at the constructor.
     */
    protected void init() {
        // creatorId、creatorName、modifierId、modifierName、
        registerCommand("nowDate", new Callable<Object>() {
            @Override
            public Object call() { return new Date(); }
        });
        registerCommand("int1", new Callable<Object>() {
            @Override
            public Object call() { return 1; }
        });
        registerCommand("int0", new Callable<Object>() {
            @Override
            public Object call() { return 0; }
        });
    }

    public AbstractMetaObjectHandler() { init(); }

    @Override
    public void insertFill(MetaObject metaObject) {
        for (Map.Entry<String, String> entry : getConfigs().entrySet()) {
            // Invoke command.
            Object fieldVal = invokeCommand(entry.getValue());
            if (ObjectUtils.isNull(fieldVal)) { continue; }
            // Whether the setter exists?
            if (!metaObject.hasSetter(entry.getKey())) { continue; }
            // Convert type and fill.
            @SuppressWarnings("rawtypes")
            Class clazz = metaObject.getSetterType(entry.getKey());
            strictInsertFill(metaObject, entry.getKey(), clazz, convert(fieldVal, clazz));
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        for (Map.Entry<String, String> entry : getConfigs().entrySet()) {
            // Invoke command.
            Object fieldVal = invokeCommand(entry.getValue());
            if (ObjectUtils.isNull(fieldVal)) { continue; }
            // Whether the setter exists?
            if (!metaObject.hasSetter(entry.getKey())) { continue; }
            // Convert type and fill.
            @SuppressWarnings("rawtypes")
            Class clazz = metaObject.getSetterType(entry.getKey());
            strictUpdateFill(metaObject, entry.getKey(), clazz, convert(fieldVal, clazz));
        }
    }

}
