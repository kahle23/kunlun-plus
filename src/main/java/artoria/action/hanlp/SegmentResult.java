package artoria.action.hanlp;

import java.util.List;

public class SegmentResult {
    private List<Word> words;
    private String algorithm;

    public SegmentResult(List<Word> words, String algorithm) {
        this.words = words;
        this.algorithm = algorithm;
    }

    public SegmentResult(List<Word> words) {

        this.words = words;
    }

    public SegmentResult() {

    }

    public String getAlgorithm() {

        return algorithm;
    }

    public void setAlgorithm(String algorithm) {

        this.algorithm = algorithm;
    }

    public List<Word> getWords() {

        return words;
    }

    public void setWords(List<Word> words) {

        this.words = words;
    }

}
