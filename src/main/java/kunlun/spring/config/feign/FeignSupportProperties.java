/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.config.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The feign support configuration.
 * @author Kahle
 */
@ConfigurationProperties("spring.extension.feign")
public class FeignSupportProperties {
    private Boolean requestTransfer;

    public Boolean getRequestTransfer() {

        return requestTransfer;
    }

    public void setRequestTransfer(Boolean requestTransfer) {

        this.requestTransfer = requestTransfer;
    }

}
