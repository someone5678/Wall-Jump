package genandnic.walljump;

import genandnic.walljump.helper.Keybindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class WallJumpClient implements ClientModInitializer {

	public static FallingSound FALLING_SOUND;

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(Keybindings.CLING);
		FALLING_SOUND = new FallingSound(Minecraft.getInstance().player, RandomSource.create());
	}
}
