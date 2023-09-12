package genandnic.walljump;

import mod.azure.azurelib.config.Config;
import mod.azure.azurelib.config.Configurable;

@Config(id = "walljump")
public class WallJumpConfig {

	@Configurable
	@Configurable.Synchronized
	public boolean allowReClinging = false;

	@Configurable
	@Configurable.Synchronized
	public boolean autoRotation = false;

	@Configurable
	@Configurable.Synchronized
	@Configurable.DecimalRange(min = 0.0)
	public double elytraSpeedBoost = 0.0;

	@Configurable
	@Configurable.Synchronized
	public boolean enableEnchantments = true;

	@Configurable
	@Configurable.Synchronized
	@Configurable.DecimalRange(min = 0.0)
	public float exhaustionWallJump = 0.8f;

	@Configurable
	@Configurable.Synchronized
	@Configurable.DecimalRange(min = 0.0)
	public double minFallDistance = 7.5;

	@Configurable
	@Configurable.Synchronized
	public boolean playFallSound = true;

	@Configurable
	@Configurable.Synchronized
	@Configurable.DecimalRange(min = 0.0)
	public double sprintSpeedBoost = 0.0;

	@Configurable
	@Configurable.Synchronized
	public boolean stepAssist = true;

	@Configurable
	@Configurable.Synchronized
	public boolean useDoubleJump = false;

	@Configurable
	@Configurable.Synchronized
	public boolean useWallJump = true;

	@Configurable
	@Configurable.Synchronized
	@Configurable.DecimalRange(min = 0.0)
	public double wallJumpHeight = 0.55;

	@Configurable
	@Configurable.Synchronized
	@Configurable.DecimalRange(min = 1)
	public int wallSlideDelay = 15;
}
