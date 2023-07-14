package artoria.renderer.support;

import artoria.data.Pair;
import artoria.data.bean.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.io.IOUtils;
import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.CloseUtils;
import org.beetl.core.*;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.exception.ErrorInfo;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import static artoria.util.ObjectUtils.cast;

/**
 * The beetl text renderer.
 * @author Kahle
 *
 * https://www.kancloud.cn/xiandafu/beetl3_guide/1992542
 */
public class BeetlTextRenderer extends AbstractTextRenderer {
    private final GroupTemplate groupTemplate;
    private ClasspathResourceLoader classpathLoader = new ClasspathResourceLoader();
    private FileResourceLoader fileLoader = new FileResourceLoader();

    public BeetlTextRenderer(GroupTemplate groupTemplate) {
        Assert.notNull(groupTemplate, "Parameter \"groupTemplate\" must not null. ");
        this.groupTemplate = groupTemplate;
    }

    public BeetlTextRenderer() {
        try {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            groupTemplate = new GroupTemplate(resourceLoader, cfg);
            groupTemplate.setErrorHandler(new ConsoleErrorHandler(){
                @Override
                public void processException(BeetlException ex, GroupTemplate groupTemplate, Writer writer) {
//                    super.processException(ex, groupTemplate, writer);

                    ErrorInfo error = new ErrorInfo(ex);
                    int line = error.errorTokenLine;

                    StringBuilder builder = new StringBuilder("\n>> ")
                            .append(DateUtils.format())
                            .append(": ").append(error.type)
                            .append(": ").append(error.errorTokenText)
                            .append("\n位于").append(line != 0 ? line + "行" : "").append(" 资源:")
//                            .append(getResourceName(ex.resource.getId()))
                            .append("\n")
                            ;

                    try {
                        @SuppressWarnings("rawtypes")
                        Resource res = ex.resource;
                        //显示前后三行的内容
                        int[] range = this.getRange(line);
                        String content = res.getContent(range[0], range[1]);
                        if (content != null) {
                            String[] strs = content.split(ex.cr);
                            int lineNumber = range[0];
                            for (String str : strs) {
                                builder.append(lineNumber);
                                builder.append("|");
                                builder.append(str).append("\n");
                                lineNumber++;
                            }
                        }
                    } catch (IOException e) {
                        // ingore
                    }

                    if (error.errorCode.equals(BeetlException.TEMPLATE_LOAD_ERROR)) {
                        if (error.msg != null) { builder.append(error.msg); }
                        builder.append("\n").append(groupTemplate.getResourceLoader().getInfo()).append("\n");
                    }

                    if (error.hasCallStack()) {
                        builder.append("  ========================").append("\n");
                        builder.append("  调用栈:").append("\n");
                        for (int i = 0; i < error.resourceCallStack.size(); i++) {
                            builder.append("  ")
                                    .append(error.resourceCallStack.get(i))
                                    .append(" 行：")
                                    .append(error.tokenCallStack.get(i).line)
                                    .append("\n");
                        }
                    }

                    throw new IllegalStateException(builder.toString(), ex);
                }
            });
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public GroupTemplate getGroupTemplate() {

        return groupTemplate;
    }

    public ClasspathResourceLoader getClasspathLoader() {

        return classpathLoader;
    }

    public void setClasspathLoader(ClasspathResourceLoader classpathLoader) {

        this.classpathLoader = classpathLoader;
    }

    public FileResourceLoader getFileLoader() {

        return fileLoader;
    }

    public void setFileLoader(FileResourceLoader fileLoader) {

        this.fileLoader = fileLoader;
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
            }
            else if (template instanceof Reader) {
                closeable = (Reader) template;
                templateStr = IOUtils.toString((Reader) template);
            }
            else if (template instanceof Pair) {
                Pair<String, String> pair = cast(template);
                String encoding = pair.getRight();
                String path = pair.getLeft();

                String classpathName = "classpath://";
                if (path != null && path.startsWith(classpathName)) {
                    path = path.substring(classpathName.length());
                    Template tp = groupTemplate.getTemplate(path, getClasspathLoader());
                    tp.binding(BeanUtils.beanToMap(data));
                    tp.renderTo(writer);
                    return;
                }
                else {
                    Template tp = groupTemplate.getTemplate(path, getFileLoader());
                    tp.binding(BeanUtils.beanToMap(data));
                    tp.renderTo(writer);
                    return;
                }
            }
            else {
                throw new IllegalArgumentException();
            }
            Template tp = groupTemplate.getTemplate(templateStr);
            tp.binding(BeanUtils.beanToMap(data));
            tp.renderTo(writer);
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
