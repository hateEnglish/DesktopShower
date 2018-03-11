package com.xubao.gui.struct.controlStruct;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author xubao
 * @Date 2018/3/11
 */
public final class AppKeeper
{
    private AppKeeper(){}
    private static Map<StageKey,Stage> stages = new HashMap<>();
    private static Map<ControlKey,Control> controls = new HashMap<>();
    private static Map<ParentKey,Parent> parents = new HashMap<>();
    private static Map<SceneKey,Scene> scenes = new HashMap<>();

    public static <T extends Control> T getControl(ControlKey key){
        return (T)controls.get(key);
    }

    public static void putControl(ControlKey key,Control control){
        controls.put(key,control);
    }

    public static <T extends Stage> T getStage(StageKey key){
        return (T)stages.get(key);
    }

    public static void putStage(StageKey key,Stage stage){
        stages.put(key,stage);
    }

    public static <T extends Scene> T getScene(SceneKey key){
        return (T)scenes.get(key);
    }

    public static void putScene(SceneKey key,Scene scene){
        scenes.put(key,scene);
    }
}
