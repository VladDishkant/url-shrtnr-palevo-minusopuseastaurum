package edu.kpi.testcourse.storage;

import com.google.gson.reflect.TypeToken;
import edu.kpi.testcourse.entities.UrlAlias;
import edu.kpi.testcourse.entities.User;
import edu.kpi.testcourse.logic.UrlShortenerConfig;
import edu.kpi.testcourse.serialization.JsonTool;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.print.DocFlavor.URL;

public class UrlRepositoryFileImpl implements UrlRepository {
  
  private final Map<String, UrlAlias> alias;

  private final JsonTool jsonTool;
  private final UrlShortenerConfig appConfig;


  @Inject
  public UrlRepositoryFileImpl(JsonTool jsonTool, UrlShortenerConfig appConfig) {
    this.jsonTool = jsonTool;
    this.appConfig = appConfig;
    this.alias = readUrlFromJsonDatabaseFile(jsonTool, makeJsonFilePath(appConfig.storageRoot()));
  }

  /**
   * email or alias or destinationUrl
   */

  @Override
  public void createUrlAlias(UrlAlias urlAlias) throws AliasAlreadyExist {

  }

  @javax.annotation.Nullable
  @Override
  public UrlAlias findUrlAlias(String alias) {
    return null;
  }

  @Override
  public void deleteUrlAlias(String email, String alias) throws PermissionDenied {

  }

  @Override
  public List<UrlAlias> getAllAliasesForUser(String userEmail) {
    return null;
  }


  @Override
  public synchronized void createUrl(UrlAlias urlAlias) {
    if (alias.putIfAbsent(urlAlias.email(), urlAlias) != null) {
      throw new RuntimeException("Url already exists");
    }
    writeUsersToJsonDatabaseFile(jsonTool, alias, makeJsonFilePath(appConfig.storageRoot()));
  }

  @Override
  public synchronized @Nullable UrlAlias findUrl(String email) { return alias.get(email); }

  private static Path makeJsonFilePath(Path storageRoot) {
    return storageRoot.resolve("url-repository.json");
  }

  private static Map<String, UrlAlias> readUrlFromJsonDatabaseFile(
    JsonTool jsonTool, Path sourceFilePath
  ) {
    String json;
    try {
      json = Files.readString(sourceFilePath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Type type = new TypeToken<HashMap<String, UrlAlias>>(){}.getType();
    Map<String, UrlAlias> result = jsonTool.fromJson(json, type);
    if (result == null) {
      throw new RuntimeException("Could not deserialize the user repository");
    }
    return result;
  }

  private static void writeUsersToJsonDatabaseFile(
    JsonTool jsonTool, Map<String, UrlAlias> urls, Path destinationFilePath
  ) {
    String json = jsonTool.toJson(urls);
    try {
      Files.write(destinationFilePath, json.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
