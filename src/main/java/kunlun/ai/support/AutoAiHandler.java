/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.ai.support;

import kunlun.core.ArtificialIntelligence;

public interface AutoAIHandler extends ArtificialIntelligence {

    /**
     * Get the AI handler name.
     * @return The AI handler name
     */
    String getName();

}
