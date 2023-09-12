package genandnic.walljump.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import genandnic.walljump.FallingSound;
import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.phys.AABB;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMiscellaneousMixin extends AbstractClientPlayer {

	public ClientPlayerEntityMiscellaneousMixin(ClientLevel world, GameProfile profile, ProfilePublicKey playerPublicKey) {
		super(world, profile);
	}

	private boolean isFree(AABB box) {
		return this.level().noCollision(this, box) && !this.level().containsAnyLiquid(box);
	}

	@Inject(method = "aiStep", at = @At("TAIL"))
	private void miscellaneousTickMovement(CallbackInfo ci) {
		if (this.horizontalCollision && WallJump.CONFIGURATION.stepAssist && this.getDeltaMovement().y() > -0.2 && this.getDeltaMovement().y() < 0.01)
			if (this.isFree(this.getBoundingBox().inflate(0.01, -this.maxUpStep() + 0.02, 0.01)))
				this.setOnGround(true);

		if (this.fallDistance > 1.5 && !this.isFallFlying())
			if (WallJump.CONFIGURATION.playFallSound && WallJumpClient.FALLING_SOUND.isStopped()) {
				WallJumpClient.FALLING_SOUND = new FallingSound((LocalPlayer) (Object) this, RandomSource.create());
				Minecraft.getInstance().getSoundManager().play(WallJumpClient.FALLING_SOUND);
			}
	}
}
