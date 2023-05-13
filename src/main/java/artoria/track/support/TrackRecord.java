package artoria.track.support;

import artoria.core.Builder;
import artoria.data.Dict;
import artoria.track.TrackLevel;
import artoria.util.Assert;

/**
 * The track record.
 * @author Kahle
 */
public class TrackRecord implements Builder {
    private final StringBuilder content = new StringBuilder();
    private final StringBuilder message = new StringBuilder();
    private final String code;
    private TrackLevel level = TrackLevel.INFO;

    public static TrackRecord of(String code) {

        return new TrackRecord(code);
    }

    protected TrackRecord(String code) {
        Assert.notBlank(code, "Parameter \"code\" must not blank. ");
        this.code = code;
    }

    public String getCode() {

        return code;
    }

    public TrackLevel getLevel() {

        return level;
    }

    public TrackRecord setLevel(TrackLevel level) {
        Assert.notNull(level, "Parameter \"level\" must not null. ");
        this.level = level;
        return this;
    }

    public TrackRecord appendMessage(Object message) {
        this.message.append(message);
        return this;
    }

    public TrackRecord appendMessage(String format, Object... args) {
        this.message.append(String.format(format, args));
        return this;
    }

    public TrackRecord appendContent(Object content) {
        this.content.append(content);
        return this;
    }

    public TrackRecord appendContent(String format, Object... args) {
        this.content.append(String.format(format, args));
        return this;
    }

    @Override
    public Dict build() {
        return Dict.of("code", code)
                .set("level", level.getValue())
                .set("message", message.toString())
                .set("content", content.toString())
        ;
    }

}
