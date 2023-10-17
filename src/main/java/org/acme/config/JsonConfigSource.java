package org.acme.config;

import io.smallrye.config.ConfigValue;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.spi.ConfigSource;

@Slf4j
public class JsonConfigSource implements ConfigSource {

  private final Map<String, ConfigValue> existingValues;

  private JsonObject root;

  public JsonConfigSource(final Map<String, ConfigValue> exProp) {
    existingValues = exProp;
  }

  public void addJsonConfigurations(final ConfigValue config) {
    final File file = new File(config.getValue());

    if (!file.canRead()) {
      log.warn("Can't read config from " + file.getAbsolutePath() + "");
    } else {
      try (final InputStream fis = new FileInputStream(file);
          final JsonReader reader = Json.createReader(fis)) {

        root = reader.readObject();
      } catch (final IOException ioe) {
        log.warn("Reading the config failed: " + ioe.getMessage());
      }
    }
  }

  @Override
  public Map<String, String> getProperties() {

    final Map<String, String> props = new HashMap<>();
    final Set<Map.Entry<String, ConfigValue>> entries = existingValues.entrySet();
    for (final Map.Entry<String, ConfigValue> entry : entries) {
      String newVal = getValue(entry.getKey());
      if (newVal == null) {
        newVal = entry.getValue().getValue();
      }
      props.put(entry.getKey(), newVal);
    }

    return props;
  }

  @Override
  public Set<String> getPropertyNames() {
    return existingValues.keySet();
  }

  @Override
  public int getOrdinal() {
    return 270;
  }

  @Override
  public String getValue(final String configKey) {

    final JsonValue jsonValue = root.get(configKey);

    if (jsonValue != null) {
      return getStringValue(jsonValue);
    }

    if (existingValues.containsKey(configKey)) {
      return existingValues.get(configKey).getValue();
    } else {
      return null;
    }
  }

  @Override
  public String getName() {
    return "EXTERNAL_JSON";
  }

  private String getStringValue(final JsonValue jsonValue) {
    if (jsonValue != null) {
      final JsonValue.ValueType valueType = jsonValue.getValueType();

      if (valueType == JsonValue.ValueType.STRING) {
        return ((JsonString) jsonValue).getString();
      } else if (valueType == JsonValue.ValueType.NUMBER) {
        // Handle integer and floating-point numbers
        return jsonValue.toString();
      } else if (valueType == JsonValue.ValueType.TRUE || valueType == JsonValue.ValueType.FALSE) {
        // Handle boolean values
        return Boolean.toString(jsonValue.getValueType() == JsonValue.ValueType.TRUE);
      } else if (valueType == JsonValue.ValueType.NULL) {
        // Handle null values
        return null;
      }
    }
    return null;
  }
}
