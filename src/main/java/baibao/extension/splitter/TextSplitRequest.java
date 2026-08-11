/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.splitter;

import java.io.Serializable;
import java.util.List;

public class TextSplitRequest implements Serializable {
    /**
     * The text separator, split in order until the chunkSize is met or List end.
     */
    private List<String> separators;
    /**
     * This is the overlap size of the chunks.
     */
    private Integer chunkOverlap;
    /**
     * This is the size of each chunk.
     */
    private Integer chunkSize;
    private String  text;

    public List<String> getSeparators() {

        return separators;
    }

    public void setSeparators(List<String> separators) {

        this.separators = separators;
    }

    public Integer getChunkOverlap() {

        return chunkOverlap;
    }

    public void setChunkOverlap(Integer chunkOverlap) {

        this.chunkOverlap = chunkOverlap;
    }

    public Integer getChunkSize() {

        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {

        this.chunkSize = chunkSize;
    }

    public String getText() {

        return text;
    }

    public void setText(String text) {

        this.text = text;
    }

}
