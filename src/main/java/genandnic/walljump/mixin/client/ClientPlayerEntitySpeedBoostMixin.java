package genandnic.walljump.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import genandnic.walljump.WallJump;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntitySpeedBoostMixin extends AbstractClientPlayer {

	@Shadow
	public abstract boolean isShiftKeyDown();

	public ClientPlayerEntitySpeedBoostMixin(ClientLevel world, GameProfile profile, ProfilePublicKey playerPublicKey) {
		super(world, profile);
	}

	@Inject(method = "aiStep", at = @At("TAIL"))
	private void miscellaneousTickMovement(CallbackInfo ci) {
		this.doSpeedBoost();
	}

	private void doSpeedBoost() {
		var jumpBoostEffect = ((LocalPlayer) (Object) this).getEffect(MobEffects.JUMP);
		var jumpBoostLevel = 0;
		if (jumpBoostEffect != null)
			jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;

		this.getAbilities().setFlyingSpeed((float) (this.getSpeed() * (this.isSprinting() ? 1 : 1.3) / 5) * (jumpBoostLevel * 0.5F + 1));

		var pos = this.position();
		var look = this.getLookAngle();
		var motion = this.getDeltaMovement();

		if (this.isFallFlying()) {
			if (this.isShiftKeyDown())
				if (this.getXRot() < 30F)
					this.setDeltaMovement(motion.subtract(motion.scale(0.05)));
				else if (this.isSprinting()) {
					var elytraSpeedBoost = (float) WallJump.CONFIGURATION.elytraSpeedBoost + (getEquipmentBoost(EquipmentSlot.CHEST) * 0.75F);
					var boost = new Vec3(look.x(), look.y() + 0.5, look.z()).normalize().scale(elytraSpeedBoost);
					if (motion.length() <= boost.length())
						this.setDeltaMovement(motion.add(boost.scale(0.05)));
					if (boost.length() > 0.5)
						this.level().addParticle(ParticleTypes.FIREWORK, pos.x(), pos.y(), pos.z(), 0, 0, 0);
				}

		} else if (this.isSprinting()) {
			var sprintSpeedBoost = (float) WallJump.CONFIGURATION.sprintSpeedBoost + (getEquipmentBoost(EquipmentSlot.FEET) * 0.375F);
			if (!this.onGround())
				sprintSpeedBoost /= 3.125;
			var boost = new Vec3(look.x(), 0.0, look.z()).scale(sprintSpeedBoost * 0.125F);
			this.setDeltaMovement(motion.add(boost));
		}
	}

	private int getEquipmentBoost(EquipmentSlot slot) {
		var stack = this.getItemBySlot(slot);
		if (!stack.isEmpty()) {
			var enchantments = EnchantmentHelper.getEnchantments(stack);
			if (enchantments.containsKey(WallJump.SPEEDBOOST_ENCHANTMENT))
				return enchantments.get(WallJump.SPEEDBOOST_ENCHANTMENT);
		}
		return 0;
	}
}
