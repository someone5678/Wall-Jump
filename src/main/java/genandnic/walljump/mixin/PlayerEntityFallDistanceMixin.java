package genandnic.walljump.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import genandnic.walljump.WallJump;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public abstract class PlayerEntityFallDistanceMixin {
	@Shadow
	public abstract void playSound(SoundEvent sound, float volume, float pitch);

	@ModifyArg(method = "causeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z"), index = 0)
	private float adjustFallDistance(float value) {
		if (value > 3 && value <= WallJump.CONFIGURATION.doubleconfigs.minFallDistance) {
			this.playSound(SoundEvents.GENERIC_SMALL_FALL, 0.5F, 1.0F);
			return 3.0F;
		}
		return value;
	}
}