/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.renderer.support;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import kunlun.data.tuple.Pair;
import kunlun.exception.ExceptionUtils;
import kunlun.util.Assert;
import kunlun.util.CloseUtils;
import kunlun.util.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.io.Reader;
import java.io.Writer;

import static kunlun.common.constant.Charsets.STR_UTF_8;
import static kunlun.common.constant.Numbers.*;
import static kunlun.common.constant.Symbols.DOT;
import static kunlun.common.constant.Symbols.SLASH;
import static kunlun.util.ObjectUtils.cast;

/**
 * The freemarker text renderer.
 * @author Kahle
 */
public class FreemarkerTextRenderer extends AbstractTextRenderer {
    private final Configuration configuration;

    public FreemarkerTextRenderer() {
        try {
            Configuration configuration = new Configuration();
            TemplateLoader[] loaders = new TemplateLoader[TWO];
            loaders[ZERO] = new FileTemplateLoader(new File(DOT));
            loaders[ONE] = new ClassTemplateLoader(FreemarkerTextRenderer.class, SLASH);
            MultiTemplateLoader loader = new MultiTemplateLoader(loaders);
            configuration.setTemplateLoader(loader);
            this.configuration = configuration;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public FreemarkerTextRenderer(Configuration configuration) {

        this.configuration = configuration;
    }

    @Override
    public void render(Object template, String name, Object data, Object output) {
        Assert.isInstanceOf(Writer.class, output, "Parameter \"output\" must instance of Writer. ");
        if (template == null) { return; }
        Writer writer = (Writer) output;
        Closeable closeable = null;
        String templateStr;
        try {
            if (template instanceof String) {
                templateStr = (String) template;
                Template tp = new Template(name, templateStr, configuration);
                tp.process(data, writer);
            }
            else if (template instanceof Reader) {
                Reader reader = (Reader) template;
                closeable = (Reader) template;
                Template tp = new Template(name, reader, configuration);
                tp.process(data, writer);
            }
            else if (template instanceof Pair) {
                Pair<String, String> pair = cast(template);
                String encoding = pair.getRight();
                String path = pair.getLeft();
                if (StringUtils.isBlank(encoding)) { encoding = STR_UTF_8; }
                Template tp = configuration.getTemplate(path, encoding);
                tp.process(data, writer);
            }
            else {
                throw new IllegalArgumentException();
            }
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
        finally {
            CloseUtils.closeQuietly(closeable);
            CloseUtils.closeQuietly(writer);
        }
    }

}
