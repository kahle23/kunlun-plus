package artoria.zookeeper;

import artoria.util.Assert;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Zookeeper simple connection state listener.
 * @author Kahle
 */
public class ZkConnectionStateListener implements ConnectionStateListener {
    public static Logger log = LoggerFactory.getLogger(ZkConnectionStateListener.class);
    private String listenerName;

    public ZkConnectionStateListener(String listenerName) {
        Assert.notBlank(listenerName, "Parameter \"listenerName\" must not blank. ");
        this.listenerName = listenerName;
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState state) {
        if (state == ConnectionState.LOST) {
            log.debug("{} : A client lost session with zookeeper. ", this.listenerName);
        }
        else if (state == ConnectionState.CONNECTED) {
            log.debug("{} : A client connected with zookeeper. ", this.listenerName);
        }
        else if (state == ConnectionState.RECONNECTED) {
            log.debug("{} : A client reconnected with zookeeper. ", this.listenerName);
        }
    }

}
