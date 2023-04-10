package org.jorgerojasdev.avro.maven.plugin.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;

public abstract class AbstractAutoImportMojo extends AbstractMojo {


    @Parameter(defaultValue = "1.11.1")
    protected String version;

    @Parameter(defaultValue = "${basedir}/src/main/avro")
    protected String sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/avro")
    protected String outputDirectory;

    @Parameter(defaultValue = "sourceDirectory")
    protected String testSourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/avro")
    protected String testOutputDirectory;

    @Parameter(defaultValue = "PRIVATE")
    protected String fieldVisibility;

    @Parameter
    protected String[] includes = new String[0];

    @Parameter
    protected String[] testIncludes = new String[0];

    @Parameter
    protected String[] excludes = new String[0];

    @Parameter
    protected String[] testExcludes = new String[0];

    @Parameter(defaultValue = "CharSequence")
    protected String stringType;

    @Parameter(defaultValue = "/org/apache/avro/compiler/specific/templates/java/classic/")
    protected String templateDirectory;

    @Parameter
    protected String[] velocityToolsClassesNames = new String[0];

    @Parameter
    protected boolean createOptionalGetters = false;

    @Parameter
    protected boolean gettersReturnOptional = false;

    @Parameter
    protected boolean optionalGettersForNullableFieldsOnly = false;

    @Parameter
    protected boolean createSetters = true;

    @Parameter
    protected String[] customConversions = new String[0];

    @Parameter
    protected String[] customLogicalTypeFactories = new String[0];

    @Parameter
    protected boolean enableDecimalLogicalType;

    public List<Element> asElementList() {
        List<Element> elementList = new ArrayList<>();

        elementList.add(element("sourceDirectory", sourceDirectory));
        elementList.add(element("outputDirectory", outputDirectory));
        elementList.add(element("testSourceDirectory", testSourceDirectory));
        elementList.add(element("testOutputDirectory", testOutputDirectory));
        elementList.add(element("fieldVisibility", fieldVisibility));
        elementList.add(element("includes", elementsFromStringValues("include", includes)));
        elementList.add(element("testIncludes", elementsFromStringValues("testInclude", testIncludes)));
        elementList.add(element("excludes", elementsFromStringValues("exclude", excludes)));
        elementList.add(element("testExcludes", elementsFromStringValues("testExclude", testExcludes)));
        elementList.add(element("stringType", stringType));
        elementList.add(element("templateDirectory", templateDirectory));
        elementList.add(element("velocityToolsClassesNames", elementsFromStringValues("velocityToolsClassesName", velocityToolsClassesNames)));
        elementList.add(element("createOptionalGetters", String.valueOf(createOptionalGetters)));
        elementList.add(element("gettersReturnOptional", String.valueOf(gettersReturnOptional)));
        elementList.add(element("optionalGettersForNullableFieldsOnly", String.valueOf(optionalGettersForNullableFieldsOnly)));
        elementList.add(element("createSetters", String.valueOf(createSetters)));
        elementList.add(element("customConversions", elementsFromStringValues("customConversion", customConversions)));
        elementList.add(element("customLogicalTypeFactories", elementsFromStringValues("customLogicalTypeFactory", customLogicalTypeFactories)));
        elementList.add(element("enableDecimalLogicalType", String.valueOf(enableDecimalLogicalType)));
        elementList.add(element("stringType", stringType));

        return elementList;
    }

    private Element[] elementsFromStringValues(String subElementTag, String[] values) {
        return Arrays.stream(values).map(value -> element(subElementTag, value)).toArray(Element[]::new);
    }
}
