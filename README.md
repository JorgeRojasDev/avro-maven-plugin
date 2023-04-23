# Avro Maven Plugin

This Maven plugin is a wrapper for the `org.apache.avro.avro-maven-plugin`, designed to simplify the process of
importing `.avsc` files for developers. By incorporating a Depth-First Search (DFS) algorithm, the plugin automatically
establishes the necessary hierarchies for importing `.avsc` files, making the process transparent for the developer.

## Features

- Automatic handling of `.avsc` file imports, eliminating the need to manually import them
- DFS algorithm for establishing the required hierarchies for `.avsc` files
- Compatible with the original `org.apache.avro.avro-maven-plugin`

## Properties

In addition to its own custom configuration properties, this plugin supports all configuration properties that are
compatible with the `org.apache.avro.avro-maven-plugin`.

Custom configuration properties:

1. **debug** (default: `false`): When set to `true`, the plugin will emit more detailed logs during execution.
2. **version** (default: `"1.11.1"`): Specifies the version of the original `avro-maven-plugin` to use.

## Usage

Add the following to your project's pom.xml file:

```
<build>
  <plugins>
    <plugin>
      <groupId>io.github.jorgerojasdev</groupId>
      <artifactId>avro-maven-plugin</artifactId>
      <version>1.0.0</version>
      <configuration>
        <debug>true</debug>
        <version>${avro.version}</version>
        <sourceDirectory>${project.basedir}/src/main/avro</sourceDirectory>
        <outputDirectory>${project.build.directory}/generated-sources/avro</outputDirectory>
        <!-- Add any other configuration properties for org.apache.avro.avro-maven-plugin here -->
      </configuration>
    </plugin>
  </plugins>
</build>
```

Replace the `version` and `debug` values as necessary, and add any additional configuration properties needed for the
org.apache.avro.avro-maven-plugin.