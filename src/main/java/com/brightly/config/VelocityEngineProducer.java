package com.brightly.config;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.apache.velocity.app.VelocityEngine;

@Singleton
public class VelocityEngineProducer {

    @Produces
    @Singleton
    public VelocityEngine produceVelocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "file");
        velocityEngine.setProperty("file.resource.loader.path", "C:\\Users\\z004vxjk\\IdeaProjects\\mail-engine\\src\\main\\resources\\templates\\velocity");
        velocityEngine.init();
        return velocityEngine;
    }
}

