package genandnic.walljump;

import mod.azure.azurelib.config.Config;
import mod.azure.azurelib.config.Configurable;

@Config(id = "walljump")
public class WallJumpConfig {

	@Configurable
	public EnchantConfigs enchantconfigs = new EnchantConfigs();

	public static class EnchantConfigs {
		@Configurable
		@Configurable.Synchronized
		public boolean enableEnchantments = true;

		@Configurable
		@Configurable.Synchronized
		public boolean enableDoubleJumpEnchantment = true;
		
		@Configurable
		@Configurable.Synchronized
		@Configurable.Range(min = 1)
		public int doublejump_maxlevel = 2;

		@Configurable
		@Configurable.Synchronized
		public boolean enableSpeedboostEnchantment = true;

		@Configurable
		@Configurable.Synchronized
		@Configurable.Range(min = 1)
		public int speedboost_maxlevel = 3;

		@Configurable
		@Configurable.Synchronized
		public boolean enableWallJumpEnchantment = true;

		@Configurable
		@Configurable.Synchronized
		@Configurable.Range(min = 1)
		public int walljump_maxlevel = 1;
	}
	
	@Configurable
	public DoubleConfigs doubleconfigs = new DoubleConfigs();

	public static class DoubleConfigs {
		@Configurable
		@Configurable.Synchronized
		public boolean useDoubleJump = false;

		@Configurable
		@Configurable.Synchronized
		@Configurable.DecimalRange(min = 0.0)
		public double minFallDistance = 7.5;
	}
	
	@Configurable
	public SpeedConfigs speedconfigs = new SpeedConfigs();

	public static class SpeedConfigs {
		@Configurable
		@Configurable.Synchronized
		@Configurable.DecimalRange(min = 0.0)
		public double sprintSpeedBoost = 0.0;
		
		@Configurable
		@Configurable.Synchronized
		@Configurable.DecimalRange(min = 0.0)
		public double elytraSpeedBoost = 0.0;
	}
	
	@Configurable
	public JumpConfigs jumpconfigs = new JumpConfigs();

	public static class JumpConfigs {
		@Configurable
		@Configurable.Synchronized
		public boolean useWallJump = true;

		@Configurable
		@Configurable.Synchronized
		@Configurable.DecimalRange(min = 0.0f)
		public float wallJumpHeight = 0.55f;

		@Configurable
		@Configurable.Synchronized
		@Configurable.DecimalRange(min = 0.0f)
		public float wallJumpXextra = 0.0f;

		@Configurable
		@Configurable.Synchronized
		@Configurable.DecimalRange(min = 0.0f)
		public float wallJumpZextra = 0.0f;
		
		@Configurable
		@Configurable.Synchronized
		public boolean allowReClinging = false;

		@Configurable
		@Configurable.Synchronized
		@Configurable.DecimalRange(min = 1)
		public int wallSlideDelay = 15;

		@Configurable
		@Configurable.Synchronized
		public boolean autoRotation = false;

		@Configurable
		@Configurable.Synchronized
		@Configurable.DecimalRange(min = 0.0)
		public float exhaustionWallJump = 0.8f;
	}

	@Configurable
	@Configurable.Synchronized
	public boolean playFallSound = true;

	@Configurable
	@Configurable.Synchronized
	public boolean stepAssist = true;
}
