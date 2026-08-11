/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.splitter;

import java.io.Serializable;
import java.util.List;

public class TextSplitResponse implements Serializable {
    private List<String> splitTexts;

    public TextSplitResponse(List<String> splitTexts) {

        this.splitTexts = splitTexts;
    }

    public TextSplitResponse() {

    }

    public List<String> getSplitTexts() {

        return splitTexts;
    }

    public void setSplitTexts(List<String> splitTexts) {

        this.splitTexts = splitTexts;
    }

}
