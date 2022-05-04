package artoria.track;

/**
 * The track level.
 * @author Kahle
 */
public enum TrackLevel {
    /**
     * The trace level.
     */
    TRACE(1),
    /**
     * The debug level.
     */
    DEBUG(2),
    /**
     * The info level.
     */
    INFO(3),
    /**
     * The warn level.
     */
    WARN(4),
    /**
     * The error level.
     */
    ERROR(5),
    ;

    private final Integer value;

    TrackLevel(Integer value) {

        this.value = value;
    }

    public Integer getValue() {

        return value;
    }

    public static TrackLevel parse(Integer value) {
        if (value == null) { return null; }
        for (TrackLevel level : values()) {
            if (level.getValue().equals(value)) {
                return level;
            }
        }
        return null;
    }

}
