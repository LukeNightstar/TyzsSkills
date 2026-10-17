package com.tyzsskills.impl.client.key;

import net.minecraft.resources.Identifier;
import com.tyzsskills.Tyzsskills;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class MainKeybind {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Tyzsskills.MODID, "main"));

    public static final KeyMapping OPEN_SKILL_KEY = new KeyMapping(
            "key.tyzs_skills.open_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            CATEGORY
    );
}
