package artoria.generator;

import artoria.template.Renderer;

import java.io.Writer;

/**
 * Generator interface.
 * @author Kahle
 */
public interface Generator<T> {

    /**
     * Set template renderer.
     * @param renderer Template renderer
     */
    void setRenderer(Renderer renderer);

    /**
     * Generate by data to writer.
     * @param data Necessary data
     * @param writer Generated will to writer
     * @throws GenerateException Generate error or other error
     */
    void generate(T data, Writer writer) throws GenerateException;

    /**
     * Generate by data.
     * @param data Necessary data
     * @throws GenerateException Generate error or other error
     */
    void generate(T data) throws GenerateException;

}
