package genandnic.walljump;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import static genandnic.walljump.WallJump.config;


@Config(name = WallJump.MOD_ID)
public class WallJumpConfig implements ConfigData {
        @Comment("Allows you to climb up without alternating walls")
        public boolean allowReclinging = false;

        @Comment("Automatically turn the player when wall clinging")
        public boolean autoRotation = false;

        @Comment("1.16 DoubleJump which isn't Keybound.")
        @ConfigEntry.Gui.RequiresRestart
        public boolean classicDoubleJump = false;

        @Comment("Enables Elytra Wall Cling: Clinging to the Wall with Elytra Deployed.")
        @ConfigEntry.Gui.RequiresRestart
        public boolean enableElytraWallCling = false;

        @Comment("Elytra speed boost; set to 0.0 to disable")
        @ConfigEntry.Gui.RequiresRestart
        public double elytraSpeedBoost = 0.0;

        @Comment("Exhaustion gained per wall jump")
        public double exhaustionWallJump = 0.8;

        @Comment("Minimum distance for fall damage; set to 3.0 to disable")
        public double minFallDistance = 7.5;

        @Comment("Play a rush of wind as you fall to your doom")
        public boolean playFallSound = true;

        @Comment("Sprint speed boost; set to 0.0 to disable")
        @ConfigEntry.Gui.RequiresRestart
        public double sprintSpeedBoost = 0.0;

        @Comment("Sprint speed boost; set to 0.0 to disable")
        @ConfigEntry.Gui.RequiresRestart
        public boolean enableSpeedBoostEnchantment = false;

        @Comment("Walk up steps even while airborne, also jump over fences")
        public boolean stepAssist = true;

        @Comment("Allows you to jump in mid-air; will crash your game inside mod menu if you change it there. DO NOT BIND TO SPACE, IF YOU WANT TO USE SPACE, SET CLASSIC DOUBLE JUMP TO TRUE")
        @ConfigEntry.Gui.RequiresRestart
        public boolean useDoubleJump = true;

        @Comment("Allows you to wall cling and wall jump")
        @ConfigEntry.Gui.RequiresRestart
        public boolean useWallJump = true;

        @Comment("Height of Wall Jumps")
        public double wallJumpHeight = 0.55;

        @Comment("Ticks wall clinged before starting wall slide")
        public int wallSlideDelay = 20;

        public static WallJumpConfig getConfig() {
                if (config == null) {
                        AutoConfig.register(WallJumpConfig.class, JanksonConfigSerializer::new);
                        config = AutoConfig.getConfigHolder(WallJumpConfig.class);
                }
                return config.getConfig();
        }
}


