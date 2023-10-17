package artoria.db.support;

import artoria.db.DbUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * The database handler auto configuration.
 * @author Kahle
 */
@Configuration
public class DbHandlerAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DbHandlerAutoConfiguration.class);

    public DbHandlerAutoConfiguration(ApplicationContext appContext) {
        // If not have beans, handlerMap is empty map, not is null.
        Map<String, AutoDbHandler> handlerMap = appContext.getBeansOfType(AutoDbHandler.class);
        for (AutoDbHandler dbHandler : handlerMap.values()) {
            if (dbHandler == null) { continue; }
            String handlerName = dbHandler.getName();
            if (StringUtils.isBlank(handlerName)) {
                log.warn("The database handler \"{}\"'s name is blank, it will be ignored. "
                        , dbHandler.getClass());
                continue;
            }
            DbUtils.registerHandler(handlerName, dbHandler);
        }
    }

}
