/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.id.support;

/**
 * The incremental identifier configuration.
 * @author Kahle
 */
public class IncrementalIdConfig {
    private String  name;
    private String  prefix;
    private String  datePattern;
    private Integer sequenceLength;
    private Integer stepLength;
    private Long    offset;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getPrefix() {

        return prefix;
    }

    public void setPrefix(String prefix) {

        this.prefix = prefix;
    }

    public String getDatePattern() {

        return datePattern;
    }

    public void setDatePattern(String datePattern) {

        this.datePattern = datePattern;
    }

    public Integer getSequenceLength() {

        return sequenceLength;
    }

    public void setSequenceLength(Integer sequenceLength) {

        this.sequenceLength = sequenceLength;
    }

    public Integer getStepLength() {

        return stepLength;
    }

    public void setStepLength(Integer stepLength) {

        this.stepLength = stepLength;
    }

    public Long getOffset() {

        return offset;
    }

    public void setOffset(Long offset) {

        this.offset = offset;
    }

}
