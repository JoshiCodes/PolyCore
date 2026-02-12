package de.joshicodes.polycore.plugins;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginData {

    String name();
    String version() default "1.0.0";

}
