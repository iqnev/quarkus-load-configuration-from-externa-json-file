package org.acme.config;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;
import io.smallrye.config.ConfigValue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.spi.ConfigSource;

@Slf4j
public class JsonConfigSourceFactory implements ConfigSourceFactory {

  public static final String CONFIG_JSON_FILE = "config.json.file";

  @Override
  public Iterable<ConfigSource> getConfigSources(final ConfigSourceContext configSourceContext) {
    final ConfigValue value = configSourceContext.getValue(CONFIG_JSON_FILE);

    if (value == null || value.getValue() == null) {
      return Collections.emptyList();
    }

    final Map<String, ConfigValue> exProp = new HashMap<>();
    final Iterator<String> stringIterator = configSourceContext.iterateNames();

    while (stringIterator.hasNext()) {
      final String key = stringIterator.next();
      final ConfigValue cValue = configSourceContext.getValue(key);
      exProp.put(key, cValue);
    }

    log.info("Json config:  " + exProp);

    final JsonConfigSource configSource = new JsonConfigSource(exProp);
    final List<ConfigValue> configValueList = List.of(value);

    for (final ConfigValue config : configValueList) {
      if (ConfigExists(config)) {
        configSource.addJsonConfigurations(config);
      }
    }

    return Collections.singletonList(configSource);
  }

  @Override
  public OptionalInt getPriority() {
    return OptionalInt.of(270);
  }

  private boolean ConfigExists(final ConfigValue config) {

    if (config == null || config.getValue() == null) {
      log.warn("The given ConfigValue object is null");
      return false;
    } else if (!(Files.exists(Path.of(config.getValue())))) {
      return false;
    }

    return true;
  }
}
