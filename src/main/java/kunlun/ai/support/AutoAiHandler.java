/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.ai.support;

import kunlun.core.ArtificialIntelligence;

public interface AutoAiHandler extends ArtificialIntelligence {

    /**
     * Get the ai handler name.
     * @return The ai handler name
     */
    String getName();

}
