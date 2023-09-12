package genandnic.walljump;

import genandnic.walljump.enchantment.DoubleJumpEnchantment;
import genandnic.walljump.enchantment.SpeedBoostEnchantment;
import genandnic.walljump.enchantment.WallJumpEnchantment;
import mod.azure.azurelib.AzureLibMod;
import mod.azure.azurelib.config.format.ConfigFormats;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class WallJump implements ModInitializer {

	public static Enchantment WALLJUMP_ENCHANTMENT;
	public static Enchantment DOUBLEJUMP_ENCHANTMENT;
	public static Enchantment SPEEDBOOST_ENCHANTMENT;

	public static WallJumpConfig CONFIGURATION;

	public static final ResourceLocation FALL_DISTANCE_PACKET_ID = new ResourceLocation("walljump", "falldistance");
	public static final ResourceLocation WALL_JUMP_PACKET_ID = new ResourceLocation("walljump", "walljump");

	@Override
	public void onInitialize() {

		// Configuration
		CONFIGURATION = AzureLibMod.registerConfig(WallJumpConfig.class, ConfigFormats.json()).getConfigInstance();

		// Enchantments
		WALLJUMP_ENCHANTMENT = Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("walljump", "walljump"), new WallJumpEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[] { EquipmentSlot.FEET }));

		DOUBLEJUMP_ENCHANTMENT = Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("walljump", "doublejump"), new DoubleJumpEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[] { EquipmentSlot.FEET }));

		SPEEDBOOST_ENCHANTMENT = Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("walljump", "speedboost"), new SpeedBoostEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[] { EquipmentSlot.FEET }));

		// Packets
		ServerPlayNetworking.registerGlobalReceiver(FALL_DISTANCE_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			var fallDistance = buf.readFloat();
			server.execute(() -> {
				player.fallDistance = fallDistance;
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(WALL_JUMP_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			var didWallJump = buf.readBoolean();
			server.execute(() -> {
				if (didWallJump)
					player.causeFoodExhaustion(CONFIGURATION.exhaustionWallJump);
			});
		});
	}
}
