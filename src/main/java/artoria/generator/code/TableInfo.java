package artoria.generator.code;

import artoria.jdbc.TableMeta;

import java.util.List;
import java.util.Set;

/**
 * Database table necessary information and other information.
 * @author Kahle
 */
public class TableInfo extends TableMeta {
    private String controllerPackageName;
    private String controllerClassName;
    private String voPackageName;
    private String voClassName;
    private String voObjectName;
    private String servicePackageName;
    private String serviceClassName;
    private String serviceObjectName;
    private String serviceImplPackageName;
    private String serviceImplClassName;
    private String dtoPackageName;
    private String dtoClassName;
    private String dtoObjectName;
    private String mapperPackageName;
    private String mapperClassName;
    private String mapperObjectName;
    private String entityPackageName;
    private String entityClassName;
    private String entityObjectName;
    private Set<String> entityImports;
    private List<ColumnInfo> columnInfoList;

    public String getControllerPackageName() {

        return this.controllerPackageName;
    }

    public void setControllerPackageName(String controllerPackageName) {

        this.controllerPackageName = controllerPackageName;
    }

    public String getControllerClassName() {

        return this.controllerClassName;
    }

    public void setControllerClassName(String controllerClassName) {

        this.controllerClassName = controllerClassName;
    }

    public String getVoPackageName() {

        return this.voPackageName;
    }

    public void setVoPackageName(String voPackageName) {

        this.voPackageName = voPackageName;
    }

    public String getVoClassName() {

        return this.voClassName;
    }

    public void setVoClassName(String voClassName) {

        this.voClassName = voClassName;
    }

    public String getVoObjectName() {

        return this.voObjectName;
    }

    public void setVoObjectName(String voObjectName) {

        this.voObjectName = voObjectName;
    }

    public String getServicePackageName() {

        return this.servicePackageName;
    }

    public void setServicePackageName(String servicePackageName) {

        this.servicePackageName = servicePackageName;
    }

    public String getServiceClassName() {

        return this.serviceClassName;
    }

    public void setServiceClassName(String serviceClassName) {

        this.serviceClassName = serviceClassName;
    }

    public String getServiceObjectName() {

        return this.serviceObjectName;
    }

    public void setServiceObjectName(String serviceObjectName) {

        this.serviceObjectName = serviceObjectName;
    }

    public String getServiceImplPackageName() {

        return this.serviceImplPackageName;
    }

    public void setServiceImplPackageName(String serviceImplPackageName) {

        this.serviceImplPackageName = serviceImplPackageName;
    }

    public String getServiceImplClassName() {

        return this.serviceImplClassName;
    }

    public void setServiceImplClassName(String serviceImplClassName) {

        this.serviceImplClassName = serviceImplClassName;
    }

    public String getDtoPackageName() {

        return this.dtoPackageName;
    }

    public void setDtoPackageName(String dtoPackageName) {

        this.dtoPackageName = dtoPackageName;
    }

    public String getDtoClassName() {

        return this.dtoClassName;
    }

    public void setDtoClassName(String dtoClassName) {

        this.dtoClassName = dtoClassName;
    }

    public String getDtoObjectName() {

        return this.dtoObjectName;
    }

    public void setDtoObjectName(String dtoObjectName) {

        this.dtoObjectName = dtoObjectName;
    }

    public String getMapperPackageName() {

        return this.mapperPackageName;
    }

    public void setMapperPackageName(String mapperPackageName) {

        this.mapperPackageName = mapperPackageName;
    }

    public String getMapperClassName() {

        return this.mapperClassName;
    }

    public void setMapperClassName(String mapperClassName) {

        this.mapperClassName = mapperClassName;
    }

    public String getMapperObjectName() {

        return this.mapperObjectName;
    }

    public void setMapperObjectName(String mapperObjectName) {

        this.mapperObjectName = mapperObjectName;
    }

    public String getEntityPackageName() {

        return this.entityPackageName;
    }

    public void setEntityPackageName(String entityPackageName) {

        this.entityPackageName = entityPackageName;
    }

    public String getEntityClassName() {

        return this.entityClassName;
    }

    public void setEntityClassName(String entityClassName) {

        this.entityClassName = entityClassName;
    }

    public String getEntityObjectName() {

        return this.entityObjectName;
    }

    public void setEntityObjectName(String entityObjectName) {

        this.entityObjectName = entityObjectName;
    }

    public Set<String> getEntityImports() {

        return this.entityImports;
    }

    public void setEntityImports(Set<String> entityImports) {

        this.entityImports = entityImports;
    }

    public List<ColumnInfo> getColumnInfoList() {

        return this.columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {

        this.columnInfoList = columnInfoList;
    }

}
