package genandnic.walljump.mixin.client;

import com.mojang.authlib.GameProfile;
import genandnic.walljump.ClientPlayerEntityWallJumpInterface;
import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityDoubleJumpMixin extends AbstractClientPlayerEntity implements ClientPlayerEntityWallJumpInterface {

    @Shadow public abstract boolean isRiding();

    @Shadow public Input input;

    private int jumpCount = 0;

    private boolean jumpKey = false;
    private boolean useDoubleJump = WallJumpConfig.getConfig().useDoubleJump;

    private Vec3d pos = this.getPos();
    private Vec3d motion = this.getVelocity();

    private Box box = new Box(
            pos.getX(),
            pos.getY() + this.getEyeHeight(this.getPose()) * 0.8,
            pos.getZ(),
            pos.getX(),
            pos.getY() + this.getHeight(),
            pos.getZ()
    );

    public ClientPlayerEntityDoubleJumpMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }


    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void doubleJumpTickMovement(CallbackInfo ci) {
        this.doDoubleJump();
    }


    private void doDoubleJump() {
        boolean doubleJumpCountDetection = this.onGround || this.world.containsFluid(box) || this.ticksWallClinged > 0 || this.isRiding() || this.getAbilities().allowFlying;
        boolean classicDoubleJump = WallJumpConfig.getConfig().classicDoubleJump && this.input.jumping;
        boolean noClassicDoubleJump = !WallJumpConfig.getConfig().classicDoubleJump && WallJumpClient.toggleDoubleJump;

        if(doubleJumpCountDetection)
            this.jumpCount = this.getMultiJumps();

        if(useDoubleJump)
        {
                if (noClassicDoubleJump)
                    this.DoubleJump();
                else if (classicDoubleJump)
                    this.DoubleJump();
        }
    }


    private int getMultiJumps() {
        int jumpCount = 0;
        ItemStack stack = this.getEquippedStack(EquipmentSlot.FEET);
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        boolean enchantmentDetection = !stack.isEmpty() && enchantments.containsKey(WallJump.DOUBLEJUMP_ENCHANTMENT);

        if(useDoubleJump)
            jumpCount += 1;
        if(enchantmentDetection)
            jumpCount += enchantments.get(WallJump.DOUBLEJUMP_ENCHANTMENT);


        return jumpCount;
    }

    private void DoubleJump() {
        boolean doDoubleJump = !this.jumpKey && this.jumpCount > 0 && motion.getY() < 0.333 && this.ticksWallClinged < 1 && this.getHungerManager().getFoodLevel() > 0 && !this.world.containsFluid(box);

        if (doDoubleJump) {
            this.jump();
            this.jumpCount--;

            this.fallDistance = 0.0F;

            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
            passedData.writeFloat(this.fallDistance);
            ClientPlayNetworking.send(WallJump.FALL_DISTANCE_PACKET_ID, passedData);
        }
        else
        {
            this.jumpKey = false;
        }
    }
}
