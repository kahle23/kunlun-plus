package kunlun.action.support.data.fill;

import kunlun.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static kunlun.action.support.data.fill.DataFillHandler.FieldConfigImpl;

public class SingleFieldFillConfig implements DataFillHandler.Config {

    public static SingleFieldFillConfig of(Object data) {

        return new SingleFieldFillConfig().setData(data);
    }

    public static SingleFieldFillConfig of() {

        return new SingleFieldFillConfig();
    }


    private final List<FieldConfigImpl> fieldConfigs = new ArrayList<FieldConfigImpl>();
    private Object data;

    @Override
    public Object getData() {

        return data;
    }

    public SingleFieldFillConfig setData(Object data) {
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        this.data = data;
        return this;
    }

    @Override
    public List<FieldConfigImpl> getFieldConfigs() {

        return Collections.unmodifiableList(fieldConfigs);
    }

    public SingleFieldFillConfig addFieldConfig(String queryField, String fillField, String dataField) {
        this.fieldConfigs.add(new FieldConfigImpl(queryField, fillField, dataField));
        return this;
    }

    public SingleFieldFillConfig addFieldConfigs(List<FieldConfigImpl> fieldConfigs) {
        Assert.notEmpty(fieldConfigs, "Parameter \"fieldConfigs\" must not empty. ");
        this.fieldConfigs.addAll(fieldConfigs);
        return this;
    }

}
