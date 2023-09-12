package genandnic.walljump.enchantment;

import genandnic.walljump.WallJump;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class WallJumpEnchantment extends Enchantment {

	public WallJumpEnchantment(Enchantment.Rarity weight, EnchantmentCategory target, EquipmentSlot[] slots) {
		super(weight, target, slots);
	}

	@Override
	public int getMinLevel() {
		return 1;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinCost(int level) {
		return 20;
	}

	@Override
	public int getMaxCost(int level) {
		return 60;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {

		if (WallJump.CONFIGURATION.useWallJump || !WallJump.CONFIGURATION.enableEnchantments)
			return false;

		return stack.isEnchantable();
	}
}
