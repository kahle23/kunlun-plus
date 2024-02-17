/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.config.zookeeper;

import kunlun.util.Assert;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The zookeeper simple connection state listener.
 * @author Kahle
 */
public class SimpleConnectionStateListener implements ConnectionStateListener {
    public static final Logger log = LoggerFactory.getLogger(SimpleConnectionStateListener.class);
    private final String listenerName;

    public SimpleConnectionStateListener(String listenerName) {
        Assert.notBlank(listenerName, "Parameter \"listenerName\" must not blank. ");
        this.listenerName = listenerName;
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState state) {
        if (state == ConnectionState.LOST) {
            log.debug(this.listenerName + " : A client lost session with zookeeper. ");
        }
        else if (state == ConnectionState.CONNECTED) {
            log.debug(this.listenerName + " : A client connected with zookeeper. ");
        }
        else if (state == ConnectionState.RECONNECTED) {
            log.debug(this.listenerName + " : A client reconnected with zookeeper. ");
        }
    }

}
