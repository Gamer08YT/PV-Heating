package de.bytestore.pvheating.configuration.context;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(ContextConfiguration.class)
public class ContextAutoConfiguration {
}