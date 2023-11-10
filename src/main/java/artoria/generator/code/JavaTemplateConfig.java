package artoria.generator.code;

@Deprecated
public class JavaTemplateConfig {
    private String templateName;
    private String businessPackageName;
    private Boolean skipExisted;

    public JavaTemplateConfig(String templateName, String businessPackageName, Boolean skipExisted) {
        this.businessPackageName = businessPackageName;
        this.templateName = templateName;
        this.skipExisted = skipExisted;
    }

    public JavaTemplateConfig() {
    }

    public String getTemplateName() {

        return templateName;
    }

    public void setTemplateName(String templateName) {

        this.templateName = templateName;
    }

    public String getBusinessPackageName() {

        return businessPackageName;
    }

    public void setBusinessPackageName(String businessPackageName) {

        this.businessPackageName = businessPackageName;
    }

    public Boolean getSkipExisted() {

        return skipExisted;
    }

    public void setSkipExisted(Boolean skipExisted) {

        this.skipExisted = skipExisted;
    }

}
