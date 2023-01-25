package artoria.action.hanlp;

public class TextSegment {
    private String algorithm;
    private String text;

    public TextSegment(String text, String algorithm) {
        this.algorithm = algorithm;
        this.text = text;
    }

    public TextSegment(String text) {

        this.text = text;
    }

    public TextSegment() {

    }

    public String getText() {

        return text;
    }

    public void setText(String text) {

        this.text = text;
    }

    public String getAlgorithm() {

        return algorithm;
    }

    public void setAlgorithm(String algorithm) {

        this.algorithm = algorithm;
    }

}
