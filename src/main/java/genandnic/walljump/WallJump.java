package genandnic.walljump;

import genandnic.walljump.registry.WallJumpEnchantmentRegistry;
import genandnic.walljump.registry.WallJumpPacketRegistry;
import net.fabricmc.api.ModInitializer;

public class WallJump implements ModInitializer {
	@Override
	public void onInitialize() {
		// Config Initialization
		WallJumpConfig.registerConfig();

		// Enchantments
		WallJumpEnchantmentRegistry.registerWallJumpEnchantment();
		WallJumpEnchantmentRegistry.registerDoubleJumpEnchantment();
		WallJumpEnchantmentRegistry.registerSpeedBoostEnchantment();

		// Packets
		WallJumpPacketRegistry.registerFallDistancePacket();
		WallJumpPacketRegistry.registerWallJumpPacket();

		Constants.LOGGER.info("[Wall Jump] initialized!");
	}
}