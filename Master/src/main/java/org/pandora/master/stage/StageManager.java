package org.pandora.master.stage;

import lombok.Getter;
import org.pandora.master.builder.impl.scene.ControllerBuilder;
import org.pandora.master.scene.impl.agent.Screenshot;

import java.util.concurrent.ConcurrentHashMap;

public class StageManager {

    @Getter
    private final ConcurrentHashMap<String, Screenshot> screenshots;
    @Getter
    private final ConcurrentHashMap<ControllerBuilder, String> controllers;
    @Getter
    private final ConcurrentHashMap<ControllerBuilder, Object> scenes;

    public StageManager() {
        this.controllers = new ConcurrentHashMap<>();
        this.scenes = new ConcurrentHashMap<>();
        this.screenshots = new ConcurrentHashMap<>();
    }
}
