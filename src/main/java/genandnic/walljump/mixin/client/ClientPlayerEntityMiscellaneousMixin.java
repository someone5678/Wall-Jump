package genandnic.walljump.mixin.client;

import com.mojang.authlib.GameProfile;
import genandnic.walljump.FallingSound;
import genandnic.walljump.WallJump;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import genandnic.walljump.WallJumpClient;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMiscellaneousMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMiscellaneousMixin(ClientWorld world, GameProfile profile, PlayerPublicKey playerPublicKey) {
        super(world, profile);
    }

    private boolean doesNotCollide(Box box) {
        return this.getWorld().isSpaceEmpty(this, box) && !this.getWorld().containsFluid(box);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void miscellaneousTickMovement(CallbackInfo ci) {

        if(this.horizontalCollision
                && WallJump.CONFIGURATION.stepAssist()
                && this.getVelocity().getY() > -0.2
                && this.getVelocity().getY() < 0.01
        ) {

            if(this.doesNotCollide(this.getBoundingBox().expand(0.01, -this.getStepHeight() + 0.02, 0.01))) {
                this.setOnGround(true);

            }
        }

        if(this.fallDistance > 1.5 && !this.isFallFlying()) {

            if(WallJump.CONFIGURATION.playFallSound() && WallJumpClient.FALLING_SOUND.isDone()) {

                WallJumpClient.FALLING_SOUND = new FallingSound((ClientPlayerEntity) (Object) this, Random.create());
                MinecraftClient.getInstance().getSoundManager().play(WallJumpClient.FALLING_SOUND);

            }
        }
    }
}
