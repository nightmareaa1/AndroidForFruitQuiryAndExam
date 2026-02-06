package com.example.userauth.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
public class MetricsConfig {

  @Value("${spring.application.name:userauth-backend}")
  private String applicationName;

  @Value("${spring.profiles.active:dev}")
  private String activeProfile;

  @Bean
  public MeterFilter commonTagsMeterFilter() {
    return MeterFilter.commonTags(
        Tags.of("application", applicationName, "environment", activeProfile));
  }

  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  @Bean
  public InfoContributor applicationInfoContributor() {
    return builder -> {
      Map<String, Object> app = new HashMap<>();
      app.put("name", applicationName);
      app.put("profile", activeProfile);
      app.put("startTime", Instant.now().toString());
      builder.withDetail("app", app);
    };
  }
}
