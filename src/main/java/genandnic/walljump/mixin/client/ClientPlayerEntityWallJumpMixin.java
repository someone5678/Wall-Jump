package genandnic.walljump.mixin.client;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import genandnic.walljump.WallJump;
import genandnic.walljump.helper.Keybindings;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.AABB;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityWallJumpMixin extends AbstractClientPlayer {

	@Shadow
	public abstract boolean isHandsBusy();

	@Shadow
	public abstract float getViewYRot(float tickDelta);

	@Shadow
	public Input input;

	public int ticksWallClinged;
	private int ticksKeyDown;
	private double clingX;
	private double clingZ;
	private double lastJumpY = Double.MAX_VALUE;
	private Set<Direction> walls = new HashSet<>();
	private Set<Direction> staleWalls = new HashSet<>();

	private boolean isFree(AABB box) {
		return this.level().noCollision(this, box) && !this.level().containsAnyLiquid(box);
	}

	public ClientPlayerEntityWallJumpMixin(ClientLevel world, GameProfile profile, ProfilePublicKey playerPublicKey) {
		super(world, profile);
	}

	@Inject(method = "aiStep", at = @At("TAIL"))
	private void wallJumpTickMovement(CallbackInfo ci) {
		this.doWallJump();
	}

	private void doWallJump() {

		if (!this.canWallJump())
			return;

		if (this.onGround() || this.getAbilities().flying || !this.level().getFluidState(this.blockPosition()).isEmpty() || this.isHandsBusy()) {
			this.ticksWallClinged = 0;
			this.clingX = Double.NaN;
			this.clingZ = Double.NaN;
			this.lastJumpY = Double.MAX_VALUE;
			this.staleWalls.clear();

			return;
		}

		this.updateWalls();
		this.ticksKeyDown = Keybindings.CLING.isDown() ? this.ticksKeyDown + 1 : 0;

		if (this.ticksWallClinged < 1) {

			if (this.ticksKeyDown > 0 && this.ticksKeyDown < 4 && !this.walls.isEmpty() && this.canWallCling()) {

				this.walkAnimation.speed(2.5F);
				this.walkAnimation.speedOld = 2.5F;

				if (WallJump.CONFIGURATION.jumpconfigs.autoRotation) {
					this.setYRot(this.getClingDirection().getOpposite().toYRot());
					this.yRotO = this.getYRot();
				}

				this.ticksWallClinged = 1;
				this.clingX = this.getX();
				this.clingZ = this.getZ();

				this.playHitSound(this.getWallPos());
				this.spawnWallParticle(this.getWallPos());
			}

			return;
		}

		if (!Keybindings.CLING.isDown() || this.onGround() || !this.level().getFluidState(this.blockPosition()).isEmpty() || this.walls.isEmpty() || this.getFoodData().getFoodLevel() < 1) {

			this.ticksWallClinged = 0;

			if ((this.zza != 0 || this.xxa != 0) && !this.onGround() && !this.walls.isEmpty()) {

				this.fallDistance = 0.0F;

				FriendlyByteBuf passedData = new FriendlyByteBuf(Unpooled.buffer());
				passedData.writeBoolean(true);
				ClientPlayNetworking.send(WallJump.WALL_JUMP_PACKET_ID, passedData);

				this.wallJump(WallJump.CONFIGURATION.jumpconfigs.wallJumpXextra, WallJump.CONFIGURATION.jumpconfigs.wallJumpHeight, WallJump.CONFIGURATION.jumpconfigs.wallJumpZextra);
				this.staleWalls = new HashSet<>(this.walls);
			}

			return;
		}

		if (WallJump.CONFIGURATION.jumpconfigs.autoRotation) {
			this.setYRot(this.getClingDirection().getOpposite().toYRot());
			this.yRotO = this.getYRot();
		}

		this.setPosRaw(this.clingX, this.getY(), this.clingZ);

		var motionY = this.getDeltaMovement().y();

		if (motionY > 0.0)
			motionY = 0.0;
		else if (motionY < -0.6) {
			motionY = motionY + 0.2;
			this.spawnWallParticle(this.getWallPos());
		} else if (this.ticksWallClinged++ > WallJump.CONFIGURATION.jumpconfigs.wallSlideDelay) {
			motionY = -0.1;
			this.spawnWallParticle(this.getWallPos());
		} else
			motionY = 0.0;

		if (this.fallDistance > 2) {
			this.fallDistance = 0;
			var passedData = new FriendlyByteBuf(Unpooled.buffer());
			passedData.writeFloat((float) (motionY * motionY * 8));
			ClientPlayNetworking.send(WallJump.FALL_DISTANCE_PACKET_ID, passedData);
		}

		this.setDeltaMovement(0.0, motionY, 0.0);
	}

	private boolean canWallJump() {
		if (WallJump.CONFIGURATION.jumpconfigs.useWallJump)
			return true;

		var stack = this.getItemBySlot(EquipmentSlot.FEET);
		if (!stack.isEmpty()) {
			var enchantments = EnchantmentHelper.getEnchantments(stack);
			return enchantments.containsKey(WallJump.WALLJUMP_ENCHANTMENT);
		}
		return false;
	}

	private boolean canWallCling() {
		if (this.onClimbable() || this.getDeltaMovement().y() > 0.1 || this.getFoodData().getFoodLevel() < 1)
			return false;

		if (!this.isFree(this.getBoundingBox().move(0, -0.8, 0)))
			return false;

		if (WallJump.CONFIGURATION.jumpconfigs.allowReClinging || (this.getY() < this.lastJumpY - 1 || this.getY() > this.lastJumpY + 1))
			return true;

		return !this.staleWalls.containsAll(this.walls);
	}

	private void updateWalls() {
		var box = new AABB(this.getX() - 0.001, this.getY(), this.getZ() - 0.001, this.getX() + 0.001, this.getY() + this.getEyeHeight(), this.getZ() + 0.001);

		var dist = (this.getBbWidth() / 2) + (this.ticksWallClinged > 0 ? 0.1 : 0.06);

		AABB[] axes = { box.expandTowards(0, 0, dist), box.expandTowards(-dist, 0, 0), box.expandTowards(0, 0, -dist), box.expandTowards(dist, 0, 0) };

		var i = 0;
		Direction direction;
		this.walls = new HashSet<>();

		for (var axis : axes) {
			direction = Direction.from2DDataValue(i++);
			if (!this.isFree(axis)) {
				this.walls.add(direction);
				this.horizontalCollision = true;
			}
		}
	}

	private Direction getClingDirection() {
		return this.walls.isEmpty() ? Direction.UP : this.walls.iterator().next();
	}

	private BlockPos getWallPos() {
		var clingPos = this.blockPosition().relative(this.getClingDirection());
		return this.level().getBlockState(clingPos).isSolid() ? clingPos : clingPos.relative(Direction.UP);
	}

	private void wallJump(float x, float up, float z) {
		var strafe = Math.signum(this.xxa) * up * up;
		var forward = Math.signum(this.zza) * up * up;
		var f = 1.0F / Mth.sqrt(strafe * strafe + up * up + forward * forward);
		strafe = strafe * f;
		forward = forward * f;

		var f1 = Mth.sin(this.getYHeadRot() * 0.017453292F) * 0.45F;
		var f2 = Mth.cos(this.getYHeadRot() * 0.017453292F) * 0.45F;

		var jumpBoostLevel = 0;
		var jumpBoostEffect = this.getEffect(MobEffects.JUMP);
		if (jumpBoostEffect != null)
			jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;

		var motion = this.getDeltaMovement();
		this.setDeltaMovement(motion.x() + (strafe * f2 - forward * f1) + x + (jumpBoostLevel * 0.125), up + (jumpBoostLevel * 0.125), motion.z() + (forward * f2 + strafe * f1) + z + (jumpBoostLevel * 0.125));

		this.lastJumpY = this.getY();
		this.playBreakSound(this.getWallPos());
		this.spawnWallParticle(this.getWallPos());
	}

	private void playHitSound(BlockPos blockPos) {
		var blockState = this.level().getBlockState(blockPos);
		var soundType = blockState.getBlock().getSoundType(blockState);
		this.playSound(soundType.getHitSound(), soundType.getVolume() * 0.25F, soundType.getPitch());
	}

	private void playBreakSound(BlockPos blockPos) {
		var blockState = this.level().getBlockState(blockPos);
		var soundType = blockState.getBlock().getSoundType(blockState);
		this.playSound(soundType.getFallSound(), soundType.getVolume() * 0.5F, soundType.getPitch());
	}

	private void spawnWallParticle(BlockPos blockPos) {
		var blockState = this.level().getBlockState(blockPos);
		if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
			var pos = this.position();
			var motion = this.getClingDirection().getNormal();
			this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), pos.x(), pos.y(), pos.z(), motion.getX() * -1.0D, -1.0D, motion.getZ() * -1.0D);
		}
	}
}