package org.jorgerojasdev.avro.maven.plugin.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvroFieldRepresentation {
    private String name;
    private String type;
}
