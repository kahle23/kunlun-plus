package artoria.template;

import artoria.util.Assert;
import artoria.util.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeSingleton;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import static artoria.common.Constants.DEFAULT_CHARSET_NAME;
import static org.apache.velocity.app.Velocity.*;

/**
 * Velocity template renderer.
 * @author Kahle
 */
public class VelocityRenderer implements Renderer {
    private static final String FILE_LOADER_CLASS = "file.resource.loader.class";
    private static final String CLASS_LOADER_CLASS = "class.resource.loader.class";
    private static final String JAR_LOADER_CLASS = "jar.resource.loader.class";
    private static final String FILE_MODIFY_CHECK_INTERVAL = "file.resource.loader.modificationCheckInterval";
    private static VelocityEngine defaultEngine = new VelocityEngine();

    static {
        Properties properties = new Properties();
        properties.setProperty(INPUT_ENCODING, DEFAULT_CHARSET_NAME);
        properties.setProperty(OUTPUT_ENCODING, DEFAULT_CHARSET_NAME);
        properties.setProperty(RESOURCE_LOADER, "file, class, jar");
        properties.setProperty(FILE_LOADER_CLASS, "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        properties.setProperty(CLASS_LOADER_CLASS, "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.setProperty(JAR_LOADER_CLASS, "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
        properties.setProperty(FILE_RESOURCE_LOADER_CACHE, "true");
        properties.setProperty(FILE_MODIFY_CHECK_INTERVAL, "86400");
        defaultEngine.init(properties);
    }

    private Boolean isInit = false;
    private VelocityEngine engine;

    public VelocityRenderer() {

        this.isInit = RuntimeSingleton.getRuntimeServices().isInitialized();
    }

    public VelocityRenderer(VelocityEngine engine) {
        Assert.notNull(engine, "Parameter \"engine\" must not null. ");
        this.engine = engine;
    }

    private Context handleContext(Object data) {
        Context context;
        if (data instanceof Context) {
            context = (Context) data;
        }
        else if (data instanceof Map) {
            Map model = (Map) data;
            context = new VelocityContext();
            for (Object key : model.keySet()) {
                Object val = model.get(key);
                context.put((String) key, val);
            }
        }
        else {
            throw new VelocityException("Parameter \"data\" cannot handle. ");
        }
        return context;
    }

    @Override
    public void render(Object data, Object output, String name, Object input, String charsetName) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Assert.state((output instanceof Writer)
                , "Parameter \"output\" must instance of \"Writer\". ");
        if ((input instanceof Reader) || (input instanceof String)) {
            Reader reader = input instanceof Reader
                    ? (Reader) input : new StringReader((String) input);
            Context context = this.handleContext(data);
            if (this.isInit) {
                Velocity.evaluate(context, (Writer) output, name, reader);
            }
            else if (this.engine != null) {
                this.engine.evaluate(context, (Writer) output, name, reader);
            }
            else {
                defaultEngine.evaluate(context, (Writer) output, name, reader);
            }
        }
        else {
            charsetName = StringUtils.isNotBlank(charsetName)
                    ? charsetName : DEFAULT_CHARSET_NAME;
            Context context = this.handleContext(data);
            if (this.isInit) {
                Velocity.mergeTemplate(name, charsetName, context, (Writer) output);
            }
            else if (this.engine != null) {
                this.engine.mergeTemplate(name, charsetName, context, (Writer) output);
            }
            else {
                defaultEngine.mergeTemplate(name, charsetName, context, (Writer) output);
            }
        }
    }

}
