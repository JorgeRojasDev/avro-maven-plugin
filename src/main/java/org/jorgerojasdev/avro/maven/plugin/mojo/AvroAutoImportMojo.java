package org.jorgerojasdev.avro.maven.plugin.mojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jorgerojasdev.avro.maven.plugin.constants.PromptConstants;
import org.jorgerojasdev.avro.maven.plugin.service.AvroImportService;

import java.util.Arrays;
import java.util.List;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


@Mojo(name = "schema", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class AvroAutoImportMojo extends AbstractAutoImportMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession mavenSession;

    @Parameter
    private boolean debug = false;

    @Component
    private BuildPluginManager pluginManager;

    @Override
    public void execute() throws MojoFailureException {
        getLog().info("");
        printDebugAllParameters();
        AvroImportService avroImportService = new AvroImportService(getLog(), debug);

        try {
            getLog().info(PromptConstants.PROMPT_DIVIDER);
            getLog().info("Resolving imports from avro...");
            getLog().info("");

            List<Element> elementList = asElementList();
            List<Element> importList = avroImportService.getImportsFromSourceDirectory(sourceDirectory);

            elementList.add(element("imports", importList.toArray(new Element[0])));
            getLog().info("");
            getLog().info(PromptConstants.PROMPT_DIVIDER);
            getLog().info("Launching avro-maven-plugin...");
            getLog().info("");
            executeAvroPlugin(elementList.toArray(new Element[0]));
            getLog().info("");
            getLog().info(PromptConstants.PROMPT_DIVIDER);
        } catch (Exception e) {
            getLog().error(e);
        }

    }

    private void printDebugAllParameters() {
        debug(PromptConstants.PROMPT_DIVIDER);
        debug("Printing all configs:");
        debug("");
        debug(String.format("   version: %s", version));
        debug(String.format("   sourceDirectory: %s", sourceDirectory));
        debug(String.format("   outputDirectory: %s", outputDirectory));
        debug(String.format("   testSourceDirectory: %s", testSourceDirectory));
        debug(String.format("   testOutputDirectory: %s", testOutputDirectory));
        debug(String.format("   fieldVisibility: %s", fieldVisibility));
        debug(String.format("   includes: %s", Arrays.asList(includes)));
        debug(String.format("   testIncludes: %s", Arrays.asList(testIncludes)));
        debug(String.format("   excludes: %s", Arrays.asList(excludes)));
        debug(String.format("   testExcludes: %s", Arrays.asList(testExcludes)));
        debug(String.format("   stringType: %s", stringType));
        debug(String.format("   templateDirectory: %s", templateDirectory));
        debug(String.format("   velocityToolsClassesNames: %s", Arrays.asList(velocityToolsClassesNames)));
        debug(String.format("   createOptionalGetters: %s", createOptionalGetters));
        debug(String.format("   gettersReturnOptional: %s", gettersReturnOptional));
        debug(String.format("   optionalGettersForNullableFieldsOnly: %s", optionalGettersForNullableFieldsOnly));
        debug(String.format("   createSetters: %s", createSetters));
        debug(String.format("   customConversions: %s", Arrays.asList(customConversions)));
        debug(String.format("   customLogicalTypeFactories: %s", Arrays.asList(customLogicalTypeFactories)));
        debug(String.format("   enableDecimalLogicalType: %s", enableDecimalLogicalType));
        debug("");
    }

    private void debug(CharSequence charSequence) {
        if (debug) {
            getLog().info(String.format("[DEBUG] -> %s", charSequence));
        }
    }

    private void executeAvroPlugin(Element... elements) throws MojoFailureException {
        try {
            executeMojo(
                    plugin(
                            groupId("org.apache.avro"),
                            artifactId("avro-maven-plugin"),
                            version(version)
                    ),
                    goal("schema"),
                    configuration(
                            elements
                    ),
                    executionEnvironment(
                            project,
                            mavenSession,
                            pluginManager
                    )
            );
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoFailureException(e.getCause().getMessage(), e);
        }
    }
}