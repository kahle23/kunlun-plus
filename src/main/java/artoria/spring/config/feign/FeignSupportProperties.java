package artoria.spring.config.feign;

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
