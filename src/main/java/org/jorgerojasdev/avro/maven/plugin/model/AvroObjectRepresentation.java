package org.jorgerojasdev.avro.maven.plugin.model;

import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@ToString
public class AvroObjectRepresentation {
    private String path;
    private String filename;
    private String namespace;
    private String name;
    private List<AvroFieldRepresentation> fields;
    private int dependencyLevel;
    private Set<String> dependencies = new HashSet<>();
}
