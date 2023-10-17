package org.acme;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "simple")
public interface SimpleConfig {
  @WithName("source")
  String source();

  @WithName("service")
  String service();

  @WithName("destination")
  String destination();
}
