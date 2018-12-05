package artoria.rss;

import org.junit.Test;

import java.util.Date;

public class RomeRssTest {

    public static RomeRss createRss(FeedType feedType) {
        RomeRss rss = RomeRss.on(feedType.toString(), "hello", "https://github.com", "Hello, World! ");
        rss.addItem("Post1", "https://github.com", new Date(), "Post1's XXXX ddddd ssss");
        rss.addItem("Post2", "https://github.com", new Date(), "Post2's XXXX ddddd ssss");
        rss.addItem("Post3", "https://github.com", new Date(), "Post3's XXXX ddddd ssss");
        rss.addItem("Post4", "https://github.com", new Date(), "Post4's XXXX ddddd ssss");
        rss.addItem("Post5", "https://github.com", new Date(), "Post5's XXXX ddddd ssss");
        rss.setImage("image", "https://github.com");
        return rss;
    }

    @Test
    public void test1() throws Exception {
        FeedType feedType = FeedType.RSS_2_0;
        RomeRss rss = createRss(feedType);

        String outputString = rss.outputString();
        // System.out.println(outputString);

        RomeRss on = RomeRss.on(outputString, "utf-8");
        System.out.println(on.outputString());
    }

    @Test
    public void test2() throws Exception {
        FeedType feedType = FeedType.RSS_2_0;
        RomeRssGenerator generator = new RomeRssGenerator(true, "yyyy-MM-dd HH:mm:ss");
        RomeRss rss = createRss(feedType).setFeedGenerator(generator);

        System.out.println(rss.outputString());
    }

}
