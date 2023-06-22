package artoria.polyglot;

import artoria.data.Dict;
import artoria.data.bean.BeanUtils;
import artoria.util.Assert;
import artoria.util.MapUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.Map;
import java.util.function.Predicate;

import static artoria.util.ObjectUtils.cast;

/**
 * The polyglot execution provider base on graalvm-js.
 * @see <a href="https://www.graalvm.org/latest/reference-manual/js/ScriptEngine/">ScriptEngine Implementation</a>
 * @see <a href="https://www.graalvm.org/latest/reference-manual/js/NashornMigrationGuide/">Migration Guide from Nashorn to GraalVM JavaScript</a>
 * @author Kahle
 */
public class GraalPolyglotProvider implements PolyglotProvider {

    /**
     * Build graalvm context.
     * @param name The language name
     * @param config The config
     * @return The graalvm context
     */
    protected Context buildContext(String name, Object config) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Dict configDict = Dict.of(BeanUtils.beanToMap(config));
        // Context builder.
        Context.Builder builder = Context.newBuilder();
        // Allow host access.
        builder.allowHostAccess(configDict.get("allowHostAccess", HostAccess.class, HostAccess.ALL));
        // Allows access to all Java classes.
        //builder.allowHostClassLookup(className -> true)
        Predicate<String> predicate = cast(configDict.get("allowHostClassLookup", Predicate.class));
        builder.allowHostClassLookup(predicate != null ? predicate : new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return true;
            }
        });
        // Options.
        Map<String, String> options = cast(configDict.get("options", Map.class));
        if (MapUtils.isNotEmpty(options)) {
            builder.options(options);
        }
        // Build.
        return builder.build();
    }

    @Override
    public Object eval(String name, Object source, Object config, Object data) {
        Assert.notNull(source, "Parameter \"source\" must not null. ");
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Context context = null;
        try  {
            // Build context.
            if (data instanceof Context) { context = (Context) data; }
            else {
                context = buildContext(name, config);
                Value bindings = context.getBindings(name);
                Dict dict = Dict.of(BeanUtils.beanToMap(data));
                for (Map.Entry<Object, Object> entry : dict.entrySet()) {
                    Object value = entry.getValue();
                    Object key = entry.getKey();
                    if (key == null) { continue; }
                    bindings.putMember(String.valueOf(key), value);
                }
            }
            // Eval.
            Value eval = context.eval(Source.create(name, (CharSequence) source));
            // Result.
            if (eval == null || eval.isNull()) { return null; }
            return eval.as(Object.class);
        }
        finally {
            if (context != null) {
                try {
                    context.close();
                }
                catch (Exception e) {
                    // ignored
                }
            }
        }
    }

    @Override
    public Object invoke(String name, Object source, Object config, String function, Object... arguments) {
        Assert.notBlank(function, "Parameter \"function\" must not blank. ");
        Assert.notNull(source, "Parameter \"source\" must not null. ");
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Context context = null;
        try {
            // Build context.
            context = buildContext(name, config);
            // Eval.
            context.eval(name, (CharSequence) source);
            // Get member and execute.
            Value member = context.getBindings(name).getMember(function);
            Value execute = member.execute(arguments);
            // Result.
            return execute.isNull() ? null : execute.as(Object.class);
        }
        finally {
            if (context != null) {
                try {
                    context.close();
                }
                catch (Exception e) {
                    // ignored
                }
            }
        }
    }

}
