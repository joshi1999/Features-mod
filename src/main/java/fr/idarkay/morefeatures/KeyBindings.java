package fr.idarkay.morefeatures;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * File <b>KeyBindings</b> located on fr.idarkay.morefeatures
 * KeyBindings is a part of featurs-mod.
 * <p>
 * Copyright (c) 2020 featurs-mod.
 * <p>
 *
 * @author Alois. B. (IDarKay),
 * Created the 31/07/2020 at 01:44
 */
public abstract class KeyBindings {
    private static final String MOD_ID = FeaturesMod.MOD_ID;

    public static final KeyBinding OPEN_OPTIONS_KEYS = new KeyBinding("key." + MOD_ID + ".options",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            "key.categories." + MOD_ID);
    public static final KeyBinding ADD_LOCAL_TIME_KEYS = new KeyBinding("key." + MOD_ID + ".addTime",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_ADD,
            "key.categories." + MOD_ID);
    public static final KeyBinding REMOVE_LOCAL_TIME_KEYS = new KeyBinding("key." + MOD_ID + ".removeTime",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_SUBTRACT,
            "key.categories." + MOD_ID);
    public static final KeyBinding ACTIVE_LOCAL_TIME = new KeyBinding("key." + MOD_ID + ".localTime",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "key.categories." + MOD_ID);
    public static final KeyBinding TOGGLE_BREAK_PROTECTION = new KeyBinding("key." + MOD_ID + ".toggleBreakProtection",
            InputUtil.UNKNOWN_KEY.getCode(),
            "key.categories." + MOD_ID);

    public static void init() {
        KeyBindingHelper.registerKeyBinding(OPEN_OPTIONS_KEYS);
        KeyBindingHelper.registerKeyBinding(ADD_LOCAL_TIME_KEYS);
        KeyBindingHelper.registerKeyBinding(REMOVE_LOCAL_TIME_KEYS);
        KeyBindingHelper.registerKeyBinding(ACTIVE_LOCAL_TIME);
        KeyBindingHelper.registerKeyBinding(TOGGLE_BREAK_PROTECTION);
    }

}
