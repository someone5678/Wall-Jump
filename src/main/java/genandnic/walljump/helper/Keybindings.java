package genandnic.walljump.helper;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

public class Keybindings {

	public static KeyMapping CLING = new KeyMapping("key.walljump.cling", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT, "category.walljump.binds");
}
