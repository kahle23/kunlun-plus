package artoria.generator.java;

import artoria.jdbc.ColumnMeta;

/**
 * Database table's column necessary information and other information.
 * @author Kahle
 */
public class ColumnData extends ColumnMeta {
    private String fieldName;
    private String javaType;
    private String getterName;
    private String setterName;

    public String getFieldName() {

        return this.fieldName;
    }

    public void setFieldName(String fieldName) {

        this.fieldName = fieldName;
    }

    public String getJavaType() {

        return this.javaType;
    }

    public void setJavaType(String javaType) {

        this.javaType = javaType;
    }

    public String getGetterName() {

        return this.getterName;
    }

    public void setGetterName(String getterName) {

        this.getterName = getterName;
    }

    public String getSetterName() {

        return this.setterName;
    }

    public void setSetterName(String setterName) {

        this.setterName = setterName;
    }

}
