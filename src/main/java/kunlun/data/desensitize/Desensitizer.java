/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.desensitize;

/**
 * Provide a high-level abstract of the desensitize tools.
 * @author Kahle
 */
public interface Desensitizer {

    /**
     * Data desensitize.
     * @param data Data to be desensitized
     * @return Desensitized results
     */
    String desensitize(CharSequence data);

}
