package genandnic.walljump;

import genandnic.walljump.enchantment.DoubleJumpEnchantment;
import genandnic.walljump.enchantment.SpeedBoostEnchantment;
import genandnic.walljump.enchantment.WallJumpEnchantment;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WallJump implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("WallJump");

	public static final String MOD_ID = "walljump";

	public static Enchantment WALLJUMP_ENCHANTMENT;
	public static Enchantment DOUBLEJUMP_ENCHANTMENT;
	public static Enchantment SPEEDBOOST_ENCHANTMENT;


	public static final Identifier FALL_DISTANCE_PACKET_ID = new Identifier("walljump", "falldistance");
	public static final Identifier WALL_JUMP_PACKET_ID = new Identifier("walljump", "walljump");

	public static ConfigHolder<WallJumpConfig> config;

	@Override
	public void onInitialize() {

		// Config Initialization
		AutoConfig.register(WallJumpConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(WallJumpConfig.class);

		// Enchantments
		if (!WallJumpConfig.getConfig().useWallJump) {
			WALLJUMP_ENCHANTMENT = Registry.register(
					Registry.ENCHANTMENT,
					new Identifier("walljump", "walljump"),
					new WallJumpEnchantment(
							Enchantment.Rarity.UNCOMMON,
							EnchantmentTarget.ARMOR_FEET,
							new EquipmentSlot[] {
									EquipmentSlot.FEET
							}
					)
			);
		}

		if (!WallJumpConfig.getConfig().useDoubleJump) {
			DOUBLEJUMP_ENCHANTMENT = Registry.register(
					Registry.ENCHANTMENT,
					new Identifier("walljump", "doublejump"),
					new DoubleJumpEnchantment(
							Enchantment.Rarity.RARE,
							EnchantmentTarget.ARMOR_FEET,
							new EquipmentSlot[] {
									EquipmentSlot.FEET
							}
					)
			);
		}


		if (WallJumpConfig.getConfig().sprintSpeedBoost == 0.0) {
			SPEEDBOOST_ENCHANTMENT = Registry.register(
					Registry.ENCHANTMENT,
					new Identifier("walljump", "speedboost"),
					new SpeedBoostEnchantment(
							Enchantment.Rarity.RARE,
							EnchantmentTarget.ARMOR_FEET,
							new EquipmentSlot[] {
									EquipmentSlot.FEET
							}
					)
			);
		}
		// Packets
		ServerPlayNetworking.registerGlobalReceiver(FALL_DISTANCE_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			float fallDistance = buf.readFloat();
			server.execute(() -> {
				player.fallDistance = fallDistance;
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(WALL_JUMP_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			boolean didWallJump = buf.readBoolean();

			server.execute(() -> {
				if(didWallJump)
					player.addExhaustion((float) WallJumpConfig.getConfig().exhaustionWallJump);
			});
		});

		LOGGER.info("[Wall Jump] initialized!");
	}

	// Configuration

}
