package genandnic.walljump;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class WallJumpClient implements ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("WallJumpClient");

	public static boolean toggleDoubleJump;
	public static boolean toggleWallJump;

	private static KeyBinding wallJumpKeybind;
	private static KeyBinding doubleJumpKeybind;

	public static FallingSound FALLING_SOUND;

	public void registerBind() {
		if (ModConfig.getConfig().useWallJump) {
			wallJumpKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
					"key.walljump.walljump",
					InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_LEFT_SHIFT,
					"category.walljump.walljump"

			));
		}
		if (ModConfig.getConfig().useDoubleJump) {
			doubleJumpKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
					"key.walljump.doublejump",
					InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_SPACE,
					"category.walljump.walljump"

			));
		}
	}

	@Override
	public void onInitializeClient() {
		registerBind();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (ModConfig.getConfig().useWallJump) {
				toggleWallJump = wallJumpKeybind.isPressed();
			}
			if (ModConfig.getConfig().useDoubleJump) {
				toggleDoubleJump = doubleJumpKeybind.isPressed();
			}
		});

		FALLING_SOUND = new FallingSound(MinecraftClient.getInstance().player);

		LOGGER.info("[Wall Jump Client] initialized!");
	}
}
