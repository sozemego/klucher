package com.soze.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class FileUtils {

  private final ResourceLoader resourceLoader;
  
  @Autowired
  public FileUtils(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  public String readFromFile(String path) throws IOException {
    List<String> lines = readLinesFromFile(path);
    return concatenateLines(lines);
  }

  public List<String> readLinesFromFile(String path) throws IOException {
    Resource resource = resourceLoader.getResource("file:" + path);
    return readFromResource(resource);
  }

  private String concatenateLines(List<String> lines) {
    StringBuilder sb = new StringBuilder();
    for (String line : lines) {
      sb.append(line);
    }
    return sb.toString();
  }
  
  public List<String> readLinesFromClasspathFile(String path) throws IOException {
    Resource resource = resourceLoader.getResource("classpath:" + path);
    return readFromResource(resource);
  }
  
  private List<String> readFromResource(Resource resource) throws IOException {
    InputStream stream = resource.getInputStream();
    List<String> lines = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(stream, StandardCharsets.UTF_8))) {
      lines.addAll(br.lines().collect(Collectors.toList()));
    }
    return lines;
  }

}
