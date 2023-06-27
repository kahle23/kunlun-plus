package artoria.data.comparison.support.difference;

import artoria.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The field difference comparator.
 * @author Kahle
 */
public class FieldDifferenceComparator extends AbstractFieldComparator {
    private final boolean ignoreRightNullValue;

    public FieldDifferenceComparator(Boolean ignoreRightNullValue) {
        Assert.notNull(ignoreRightNullValue, "Parameter \"ignoreRightNullValue\" must not null. ");
        this.ignoreRightNullValue = ignoreRightNullValue;
    }

    public FieldDifferenceComparator() {

        this(Boolean.FALSE);
    }

    @Override
    public List<FieldCompareResult> compare(Object left, Object right, Object... arguments) {
        Assert.notNull(left, "Parameter \"right\" must not null. ");
        Assert.notNull(left, "Parameter \"left\" must not null. ");
        // The right is new value.
        List<FieldEntity> rightList = convert(right);
        // The left is old value.
        List<FieldEntity> leftList = convert(left);
        // Convert leftList to map.
        Map<String, FieldEntity> leftMap = new LinkedHashMap<String, FieldEntity>();
        for (FieldEntity entity : leftList) {
            leftMap.put(entity.getName(), entity);
        }
        // Do compare (Take the data on the right as the baseline).
        List<FieldCompareResult> list = new ArrayList<FieldCompareResult>();
        for (FieldEntity rightEntity : rightList) {
            // Get right name and right value.
            Object rightValue = rightEntity.getValue();
            String name = rightEntity.getName();
            // Handle case where the value is null.
            if (ignoreRightNullValue && rightValue == null) { continue; }
            // Get left entity and left value.
            FieldEntity leftEntity = leftMap.get(name);
            if (leftEntity == null) { continue; }
            Object leftValue = leftEntity.getValue();
            // Compare left value and right value.
            if (equals(leftValue, rightValue)) { continue; }
            // Build result.
            FieldCompareResult result = new FieldCompareResult();
            result.setName(name);
            result.setDescription(rightEntity.getDescription());
            result.setLeftValue(leftValue);
            result.setRightValue(rightValue);
            list.add(result);
        }
        return list;
    }

}
