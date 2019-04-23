package artoria.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Logger properties.
 * @author Kahle
 */
@ConfigurationProperties(prefix = "artoria.logging")
public class LoggingProperties {
    private Boolean printControllerLog = false;

    public Boolean getPrintControllerLog() {

        return this.printControllerLog;
    }

    public void setPrintControllerLog(Boolean printControllerLog) {
        if (printControllerLog == null) { return; }
        this.printControllerLog = printControllerLog;
    }

}
