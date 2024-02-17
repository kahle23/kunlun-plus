/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.nlp;

import java.util.List;

public class TextSimilarity {
    private List<String> words1;
    private List<String> words2;
    private String algorithm;
    private String text1;
    private String text2;

    public TextSimilarity(List<String> words1, List<String> words2, String algorithm) {
        this.algorithm = algorithm;
        this.words1 = words1;
        this.words2 = words2;
    }

    public TextSimilarity(List<String> words1, List<String> words2) {
        this.words1 = words1;
        this.words2 = words2;
    }

    public TextSimilarity(String text1, String text2, String algorithm) {
        this.algorithm = algorithm;
        this.text1 = text1;
        this.text2 = text2;
    }

    public TextSimilarity(String text1, String text2) {
        this.text1 = text1;
        this.text2 = text2;
    }

    public TextSimilarity() {

    }

    public String getAlgorithm() {

        return algorithm;
    }

    public void setAlgorithm(String algorithm) {

        this.algorithm = algorithm;
    }

    public String getText1() {

        return text1;
    }

    public void setText1(String text1) {

        this.text1 = text1;
    }

    public String getText2() {

        return text2;
    }

    public void setText2(String text2) {

        this.text2 = text2;
    }

    public List<String> getWords1() {

        return words1;
    }

    public void setWords1(List<String> words1) {

        this.words1 = words1;
    }

    public List<String> getWords2() {

        return words2;
    }

    public void setWords2(List<String> words2) {

        this.words2 = words2;
    }

}
