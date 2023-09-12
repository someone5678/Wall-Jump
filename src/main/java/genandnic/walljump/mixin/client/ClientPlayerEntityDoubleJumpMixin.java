package genandnic.walljump.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import genandnic.walljump.ClientPlayerEntityWallJumpInterface;
import genandnic.walljump.WallJump;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityDoubleJumpMixin extends AbstractClientPlayer implements ClientPlayerEntityWallJumpInterface {

	@Shadow
	public abstract boolean isHandsBusy();

	@Shadow
	public Input input;

	private int jumpCount = 0;
	private boolean jumpKey = false;

	public ClientPlayerEntityDoubleJumpMixin(ClientLevel world, GameProfile profile, ProfilePublicKey playerPublicKey) {
		super(world, profile);
	}

	@Inject(method = "aiStep", at = @At("TAIL"))
	private void doubleJumpTickMovement(CallbackInfo ci) {
		this.doDoubleJump();
	}

	@SuppressWarnings("static-access")
	private void doDoubleJump() {
		var pos = this.position();
		var motion = this.getDeltaMovement();

		var box = new AABB(pos.x(), pos.y() + this.getEyeHeight(this.getPose()) * 0.8, pos.z(), pos.x(), pos.y() + this.getBbHeight(), pos.z());

		if (this.onGround() || this.level().containsAnyLiquid(box) || this.ticksWallClinged > 0 || this.isHandsBusy() || this.getAbilities().mayfly) {

			this.jumpCount = this.getMultiJumps();

		} else if (this.input.jumping) {

			if (!this.jumpKey && this.jumpCount > 0 && motion.y() < 0.333 && this.ticksWallClinged < 1 && this.getFoodData().getFoodLevel() > 0) {

				this.jumpFromGround();
				this.jumpCount--;

				this.fallDistance = 0.0F;

				var passedData = new FriendlyByteBuf(Unpooled.buffer());
				passedData.writeFloat(this.fallDistance);
				ClientPlayNetworking.send(WallJump.FALL_DISTANCE_PACKET_ID, passedData);
			}

			this.jumpKey = true;

		} else {

			this.jumpKey = false;

		}
	}

	private int getMultiJumps() {
		var jumpCount = 0;
		if (WallJump.CONFIGURATION.useDoubleJump)
			jumpCount += 1;

		var stack = this.getItemBySlot(EquipmentSlot.FEET);
		if (!stack.isEmpty()) {
			var enchantments = EnchantmentHelper.getEnchantments(stack);
			if (enchantments.containsKey(WallJump.DOUBLEJUMP_ENCHANTMENT))
				jumpCount += enchantments.get(WallJump.DOUBLEJUMP_ENCHANTMENT);
		}
		return jumpCount;
	}
}
