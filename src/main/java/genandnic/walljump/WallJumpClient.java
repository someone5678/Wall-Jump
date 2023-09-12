package genandnic.walljump;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class WallJumpClient implements ClientModInitializer {

	public static FallingSound FALLING_SOUND;

	@Override
	public void onInitializeClient() {
		FALLING_SOUND = new FallingSound(Minecraft.getInstance().player, RandomSource.create());
	}
}
