package com.zeller.letmecook.configuration;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerConfiguration {

	@Bean
	public MeterFilter meterFilter() {
		return new MeterFilter() {
			@Override
			public Meter.Id map(Meter.Id id) {
				if(id.getName().startsWith("custom")) {
					return id.withName("letmecook." + id.getName());
				}
				return id;
			}
		};
	}
}
