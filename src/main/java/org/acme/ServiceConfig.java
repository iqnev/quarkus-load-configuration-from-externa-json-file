package org.acme;

import io.smallrye.config.ConfigMapping;
import java.util.List;

@ConfigMapping(prefix = "configuration")
public interface ServiceConfig {

  List<Environment> environments();

  interface Environment {
    String source();
    String service();
    String destination();
  }
}
