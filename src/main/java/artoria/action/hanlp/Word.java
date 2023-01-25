package artoria.action.hanlp;

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
