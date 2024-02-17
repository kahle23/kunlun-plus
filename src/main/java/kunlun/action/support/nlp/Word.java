/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.nlp;

public class Word {
    private String content;
    private String nature;

    public Word(String content, String nature) {
        this.content = content;
        this.nature = nature;
    }

    public Word(String content) {

        this.content = content;
    }

    public Word() {

    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public String getNature() {

        return nature;
    }

    public void setNature(String nature) {

        this.nature = nature;
    }

}
