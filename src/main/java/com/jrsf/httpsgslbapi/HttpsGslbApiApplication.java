package com.jrsf.httpsgslbapi;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;

@SpringBootApplication
public class HttpsGslbApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpsGslbApiApplication.class, args);
	}

	@Bean
	public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
		return registry -> registry.config().commonTags("application", "https-gslb-api");
	}

	@Bean
	public ClassLoaderMetrics classLoaderMetrics() {
		return new ClassLoaderMetrics();
	}

	@Bean
	public JvmThreadMetrics jvmThreadMetrics() {
		return new JvmThreadMetrics();
	}

	@Bean
	public LogbackMetrics logbackMetrics() {
		return new LogbackMetrics();
	}

	@Bean
	public ProcessorMetrics processorMetrics() {
		return new ProcessorMetrics();
	}

	@Bean
	public JvmMemoryMetrics jvmMemoryMetrics() {
		return new JvmMemoryMetrics();
	}
}
