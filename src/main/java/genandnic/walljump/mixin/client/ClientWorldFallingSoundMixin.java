package genandnic.walljump.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import genandnic.walljump.FallingSound;
import genandnic.walljump.WallJumpClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.RandomSource;

@Mixin(ClientLevel.class)
public class ClientWorldFallingSoundMixin {

	@Inject(method = "addPlayer", at = @At(value = "TAIL"))
	private void addPlayerFallingSound(int id, AbstractClientPlayer player, CallbackInfo ci) {
		if (player == Minecraft.getInstance().player) {
			WallJumpClient.FALLING_SOUND = new FallingSound(Minecraft.getInstance().player, RandomSource.create());
			Minecraft.getInstance().getSoundManager().play(WallJumpClient.FALLING_SOUND);
		}
	}
}
