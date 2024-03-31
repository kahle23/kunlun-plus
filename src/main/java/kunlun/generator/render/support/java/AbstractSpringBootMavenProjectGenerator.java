/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.render.support.java;

public abstract class AbstractSpringBootMavenProjectGenerator extends AbstractJavaMavenProjectGenerator {

    @Override
    protected void buildMainJavaPath(ProjectContextImpl projectContext, ModuleInfo moduleInfo) {
        super.buildMainJavaPath(projectContext, moduleInfo);
        String mainPackageChildPath = moduleInfo.getMainJavaBasePackageChildPath();
        String mainChildPath = moduleInfo.getMainJavaChildPath();
        // render
        render(projectContext, moduleInfo
                , mainChildPath + "\\Application.java"
                , mainPackageChildPath + "\\Application.java");
    }

    @Override
    public void buildMainResourcesPath(ProjectContextImpl projectContext, ModuleInfo moduleInfo) {
        super.buildMainResourcesPath(projectContext, moduleInfo);
        String mainResChildPath = moduleInfo.getMainResourcesChildPath();
        render(projectContext, moduleInfo, mainResChildPath + "\\application.yml");
        render(projectContext, moduleInfo, mainResChildPath + "\\application-dev.yml");
        render(projectContext, moduleInfo, mainResChildPath + "\\bootstrap.yml");
        render(projectContext, moduleInfo, mainResChildPath + "\\logback-spring.xml");
    }

    @Override
    public void buildTestJavaPath(ProjectContextImpl projectContext, ModuleInfo moduleInfo) {
        super.buildTestJavaPath(projectContext, moduleInfo);
        String testPackageChildPath = moduleInfo.getTestJavaBasePackageChildPath();
        String testChildPath = moduleInfo.getTestJavaChildPath();
        // render
        render(projectContext, moduleInfo
                , testChildPath + "\\ApplicationTest.java"
                , testPackageChildPath + "\\ApplicationTest.java");
    }

    @Override
    public void buildTestResourcesPath(ProjectContextImpl projectContext, ModuleInfo moduleInfo) {
        super.buildTestResourcesPath(projectContext, moduleInfo);
        String testResChildPath = moduleInfo.getTestResourcesChildPath();
        render(projectContext, moduleInfo, testResChildPath + "\\logback-test.xml");
    }

}
