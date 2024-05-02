/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.polyglot.support;

import kunlun.cache.Cache;
import kunlun.cache.support.SimpleCache;
import kunlun.data.Dict;
import kunlun.util.Assert;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * The cached script engine provider.
 * @author Kahle
 */
public class CachedScriptEngineProvider extends ScriptEngineProvider {
    private final Cache cache;

    public CachedScriptEngineProvider(ScriptEngineManager scriptEngineManager, Cache cache) {
        super(scriptEngineManager);
        Assert.notNull(cache, "Parameter \"cache\" must not null. ");
        this.cache = cache;
    }

    public CachedScriptEngineProvider(Cache cache) {

        this(new ScriptEngineManager(), cache);
    }

    public CachedScriptEngineProvider() {

        this(new SimpleCache());
    }

    public Cache getCache() {

        return cache;
    }

    @Override
    public ScriptEngine getEngine(final String name, Object config) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        // Get config.
        boolean shared = false;
        if (config instanceof Map) {
            shared = Dict.of((Map<?, ?>) config).getBoolean("shared", false);
        }
        if (!shared) { super.getEngine(name, false); }
        // Get from cache.
        final boolean finalShared = shared;
        return getCache().get(name, new Callable<ScriptEngine>() {
            @Override
            public ScriptEngine call() throws Exception {
                return CachedScriptEngineProvider.super.getEngine(name, finalShared);
            }
        });
    }

}
