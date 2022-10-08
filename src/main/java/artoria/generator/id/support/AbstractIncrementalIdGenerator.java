package artoria.generator.id.support;

import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;

import static artoria.common.Constants.ZERO;
import static artoria.common.Constants.ZERO_STR;

/**
 * The abstract incremental identifier generator.
 * @author Kahle
 */
public abstract class AbstractIncrementalIdGenerator
        extends AbstractIdGenerator implements StringIdGenerator {
    private final IncrementalIdConfig config;

    public AbstractIncrementalIdGenerator(IncrementalIdConfig config) {
        Assert.notNull(config, "Parameter \"config\" must not null. ");
        Assert.notBlank(config.getName()
                , "Parameter \"config.name\" must not blank. ");
        this.config = config;
        if (config.getSequenceLength() == null) { config.setSequenceLength(8); }
        if (config.getStepLength() == null) { config.setStepLength(1); }
        if (config.getOffset() == null) { config.setOffset(0L); }
        if (StringUtils.isBlank(config.getDatePattern())) {
            config.setDatePattern("yyyyMMdd");
        }
    }

    public IncrementalIdConfig getConfig() {

        return config;
    }

    /**
     * Increment and get the stored value.
     * @return The value that is incremented and taken out
     */
    protected abstract Long incrementAndGet();

    @Override
    public String next(Object... arguments) {
        Assert.notBlank(config.getName()
                , "Parameter \"config.name\" must not blank. ");
        // Increment value.
        Long increment = incrementAndGet();
        Assert.notNull(increment
                , "Failed to invoke \"incrementAndGet\". ");
        // Add offset.
        Long offset = config.getOffset();
        if (offset != null && offset > ZERO) { increment += offset; }
        // Create identifier builder.
        StringBuilder identifier = new StringBuilder();
        identifier.append(increment);
        // Handle number length.
        Integer seqLength = config.getSequenceLength();
        boolean valid = seqLength != null && seqLength > ZERO;
        int count;
        if (valid && (count = seqLength - identifier.length()) > ZERO) {
            for (; count > ZERO; count--) {
                identifier.insert(ZERO, ZERO_STR);
            }
        }
        // Handle date string.
        String datePattern = config.getDatePattern();
        if (StringUtils.isNotBlank(datePattern)) {
            String format = DateUtils.format(datePattern);
            identifier.insert(ZERO, format);
        }
        // Handle prefix.
        String prefix = config.getPrefix();
        if (StringUtils.isNotBlank(prefix)) {
            identifier.insert(ZERO, prefix);
        }
        // Return result.
        return identifier.toString();
    }

}
