package org.jorgerojasdev.avro.maven.plugin.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.maven.plugin.logging.Log;
import org.jorgerojasdev.avro.maven.plugin.model.AvroFieldRepresentation;
import org.jorgerojasdev.avro.maven.plugin.model.AvroObjectRepresentation;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;

@RequiredArgsConstructor
public class AvroImportService {

    private final Gson gson = new Gson();
    private final Log log;
    private final boolean debug;

    public List<Element> getImportsFromSourceDirectory(String sourceDirectory) {
        List<AvroObjectRepresentation> avroObjectRepresentations = getAvroRepresentations(sourceDirectory);
        if (debug) {
            log.info("");
        }
        log.info(String.format("Found avro objects: %s", avroObjectRepresentations.size()));

        Map<String, AvroObjectRepresentation> avroObjectMap = avroObjectRepresentations.stream().collect(Collectors.toMap(
                avroObjectRepresentation -> String.format("%s.%s", avroObjectRepresentation.getNamespace(), avroObjectRepresentation.getName()),
                avroObjectRepresentation -> avroObjectRepresentation
        ));

        avroObjectMap.forEach((key, value) -> {
            deepFirstSearchDependencyLevel(value, avroObjectMap, 0);
            flatMapDependencies(value, avroObjectMap);
        });

        List<AvroObjectRepresentation> avroSortedObjectRepresentationList = toSortedList(avroObjectMap).stream().filter(avroObjectRepresentation -> avroObjectRepresentation.getDependencyLevel() > 0).collect(Collectors.toList());

        return avroSortedObjectRepresentationList.stream().map(avroObjectRepresentation -> element("import", avroObjectRepresentation.getPath())).collect(Collectors.toList());
    }

    private List<AvroObjectRepresentation> toSortedList(Map<String, AvroObjectRepresentation> avroObjectRepresentationMap) {
        return avroObjectRepresentationMap.entrySet().stream().sorted((a, b) -> {
            if (a.getValue().getDependencyLevel() > b.getValue().getDependencyLevel()) {
                return -1;
            }
            if (a.getValue().getDependencyLevel() < b.getValue().getDependencyLevel()) {
                return 1;
            }

            if (a.getValue().getDependencies().contains(a.getKey())) {
                return -1;
            }

            return 1;
        }).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    private void deepFirstSearchDependencyLevel(AvroObjectRepresentation avroObjectRepresentation, Map<String, AvroObjectRepresentation> avroObjectRepresentationMap, int level) {
        if (level > avroObjectRepresentation.getDependencyLevel()) {
            avroObjectRepresentation.setDependencyLevel(level);
        }

        for (AvroFieldRepresentation field : avroObjectRepresentation.getFields()) {
            String fieldType = field.getType();

            if (avroObjectRepresentationMap.containsKey(fieldType)) {
                AvroObjectRepresentation referencedObj = avroObjectRepresentationMap.get(fieldType);
                avroObjectRepresentation.getDependencies().add(String.format("%s.%s", referencedObj.getNamespace(), referencedObj.getName()));
                deepFirstSearchDependencyLevel(referencedObj, avroObjectRepresentationMap, level + 1);
            }
        }
    }

    private void flatMapDependencies(AvroObjectRepresentation avroObjectRepresentation, Map<String, AvroObjectRepresentation> avroObjectRepresentationMap) {
        Set<String> dependencies = deepFirstAddDependencies(avroObjectRepresentation.getDependencies(), avroObjectRepresentationMap);
        avroObjectRepresentation.setDependencies(dependencies);
    }

    private Set<String> deepFirstAddDependencies(Set<String> dependencies, Map<String, AvroObjectRepresentation> avroObjectRepresentationMap) {
        Set<String> recursiveDependencies = new HashSet<>(dependencies);
        recursiveDependencies.addAll(dependencies.stream().flatMap(dependency -> avroObjectRepresentationMap.get(dependency).getDependencies().stream()).collect(Collectors.toSet()));
        return recursiveDependencies;
    }

    private List<AvroObjectRepresentation> getAvroRepresentations(String sourceDirectory) {
        File folder = new File(sourceDirectory);
        List<File> avroFiles = getFilesFromFolder(folder).collect(Collectors.toList());

        return avroFiles.stream().flatMap(avroFile -> {
            try {
                AvroObjectRepresentation avroObjectRepresentation =
                        gson.fromJson(new FileReader(avroFile), AvroObjectRepresentation.class);

                avroObjectRepresentation.setPath(avroFile.getPath());
                avroObjectRepresentation.setFilename(avroFile.getName());

                debug(String.format("Avro file found on path: %s", avroFile.getPath()));

                return Stream.of(avroObjectRepresentation);
            } catch (Exception e) {
                log.error(e);
                return Stream.empty();
            }
        }).collect(Collectors.toList());
    }

    private Stream<File> getFilesFromFolder(File folder) {
        if (folder == null || !folder.isDirectory()) {
            return Stream.empty();
        }
        File[] subFolders = folder.listFiles(File::isDirectory);
        File[] avroFiles = folder.listFiles((dir, name) -> name.endsWith(".avsc"));

        List<File> subFolderList = Arrays.asList(Optional.ofNullable(subFolders).orElse(new File[0]));
        List<File> avroFileList = Arrays.asList(Optional.ofNullable(avroFiles).orElse(new File[0]));

        Stream<File> subAvroFiles = subFolderList.stream().flatMap(this::getFilesFromFolder);

        return Stream.concat(avroFileList.stream(), subAvroFiles);
    }

    private void debug(CharSequence charSequence) {
        if (debug) {
            log.info(String.format("[DEBUG] - [%s] -> %s", this.getClass().getSimpleName(), charSequence));
        }
    }
}
