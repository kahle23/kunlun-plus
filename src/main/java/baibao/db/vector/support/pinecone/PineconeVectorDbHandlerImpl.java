/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.vector.support.pinecone;

import java.net.Proxy;

public class PineconeVectorDbHandlerImpl extends BasePineconeVectorDbHandler {
    private final Config config;

    public PineconeVectorDbHandlerImpl(String host, String apiKey, Proxy proxy) {

        this.config = new Config(host, apiKey, proxy);
    }

    public PineconeVectorDbHandlerImpl(String host, String apiKey) {

        this(host, apiKey, null);
    }

    @Override
    protected Config getConfig(Object input) {

        return config;
    }

}
