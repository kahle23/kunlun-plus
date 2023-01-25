package artoria.action.hanlp.support;

import artoria.action.handler.AbstractClassicActionHandler;
import artoria.action.hanlp.SegmentResult;
import artoria.action.hanlp.TextSegment;
import artoria.action.hanlp.Word;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.List;

import static artoria.util.ObjectUtils.cast;

/**
 * HanLP Segment.
 * @see <a href="https://github.com/hankcs/HanLP">HanLP</a>
 * @see <a href="https://hanlp.hankcs.com">HanLP</a>
 * @author Kahle
 */
public class HankcsSegmentHandler extends AbstractClassicActionHandler {

    @Override
    public <T> T execute(Object input, Class<T> clazz) {
        isSupport(new Class[]{SegmentResult.class}, clazz);
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        TextSegment textSegment = (TextSegment) input;
        String algorithm = textSegment.getAlgorithm();
        String text = textSegment.getText();
        Assert.notBlank(text, "Parameter \"TextSegment.text\" must not blank. ");
        List<Term> termList = HanLP.segment(text);
        if (CollectionUtils.isEmpty(termList)) { return null; }
        List<Word> list = new ArrayList<Word>();
        for (Term term : termList) {
            if (term == null) { continue; }
            list.add(new Word(term.word, String.valueOf(term.nature)));
        }
        return cast(new SegmentResult(list, algorithm));
    }

}
