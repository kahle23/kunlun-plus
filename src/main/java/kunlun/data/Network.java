/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data;

@Deprecated
public class Network {
    /**
     * The address of the network.
     * @see <a href="https://en.wikipedia.org/wiki/Network_address">Network address</a>
     */
    private String address;
    /**
     * The access mode of the network.
     * @see <a href="https://en.wikipedia.org/wiki/Internet_access">Internet access</a>
     */
    private String accessMode;
    /**
     * The access provider of the network.
     * @see <a href="https://en.wikipedia.org/wiki/Internet_service_provider#Access_providers">Access providers</a>
     */
    private String accessProvider;

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public String getAccessMode() {

        return accessMode;
    }

    public void setAccessMode(String accessMode) {

        this.accessMode = accessMode;
    }

    public String getAccessProvider() {

        return accessProvider;
    }

    public void setAccessProvider(String accessProvider) {

        this.accessProvider = accessProvider;
    }

}
