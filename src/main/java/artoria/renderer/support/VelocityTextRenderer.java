package artoria.renderer.support;

import artoria.data.tuple.Pair;
import artoria.util.Assert;
import artoria.util.CloseUtils;
import artoria.util.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeSingleton;

import java.io.Closeable;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import static artoria.common.constant.Charsets.STR_DEFAULT_CHARSET;
import static artoria.common.constant.Charsets.STR_UTF_8;
import static artoria.util.ObjectUtils.cast;
import static org.apache.velocity.app.Velocity.FILE_RESOURCE_LOADER_CACHE;
import static org.apache.velocity.app.Velocity.RESOURCE_LOADER;

/**
 * The velocity text renderer.
 * @author Kahle
 */
public class VelocityTextRenderer extends AbstractTextRenderer {
    private static final String FILE_LOADER_CLASS = "file.resource.loader.class";
    private static final String CLASS_LOADER_CLASS = "class.resource.loader.class";
    private static final String JAR_LOADER_CLASS = "jar.resource.loader.class";
    private static final String FILE_MODIFY_CHECK_INTERVAL = "file.resource.loader.modificationCheckInterval";
    private static final String OUTPUT_ENCODING = "output.encoding";
    private static final String INPUT_ENCODING = "input.encoding";
    private static VelocityEngine defaultEngine = new VelocityEngine();

    static {
        Properties properties = new Properties();
        properties.setProperty(INPUT_ENCODING, STR_DEFAULT_CHARSET);
        properties.setProperty(OUTPUT_ENCODING, STR_DEFAULT_CHARSET);
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

    public VelocityTextRenderer() {

        this.isInit = RuntimeSingleton.getRuntimeServices().isInitialized();
    }

    public VelocityTextRenderer(VelocityEngine engine) {
        Assert.notNull(engine, "Parameter \"engine\" must not null. ");
        this.engine = engine;
    }

    private Context handle(Object data) {
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
    public void render(Object template, String name, Object data, Object output) {
        Assert.isInstanceOf(Writer.class, output, "Parameter \"output\" must instance of Writer. ");
        if (template == null) { return; }
        Context context = handle(data);
        Writer writer = (Writer) output;
        Closeable closeable = null;
        String templateStr;
        try {
            if (template instanceof String) {
                templateStr = (String) template;
                if (isInit) {
                    Velocity.evaluate(context, writer, name, templateStr);
                }
                else if (engine != null) {
                    engine.evaluate(context, writer, name, templateStr);
                }
                else {
                    defaultEngine.evaluate(context, writer, name, templateStr);
                }
            }
            else if (template instanceof Reader) {
                Reader reader = (Reader) template;
                closeable = (Reader) template;
                if (isInit) {
                    Velocity.evaluate(context, writer, name, reader);
                }
                else if (engine != null) {
                    engine.evaluate(context, writer, name, reader);
                }
                else {
                    defaultEngine.evaluate(context, writer, name, reader);
                }
            }
            else if (template instanceof Pair) {
                Pair<String, String> pair = cast(template);
                String encoding = pair.getRight();
                if (StringUtils.isBlank(encoding)) { encoding = STR_UTF_8; }
                String path = pair.getLeft();
                if (isInit) {
                    Velocity.mergeTemplate(path, encoding, context, writer);
                }
                else if (engine != null) {
                    engine.mergeTemplate(path, encoding, context, writer);
                }
                else {
                    defaultEngine.mergeTemplate(path, encoding, context, writer);
                }
            }
            else {
                throw new IllegalArgumentException();
            }
        }
        finally {
            CloseUtils.closeQuietly(closeable);
            CloseUtils.closeQuietly(writer);
        }
    }

}
