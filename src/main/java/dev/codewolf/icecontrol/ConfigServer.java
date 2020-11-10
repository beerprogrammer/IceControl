package dev.codewolf.icecontrol;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigServer {    
    private final ForgeConfigSpec spec;
    
    private final BooleanValue preserveOverBlockFunctionality;
    private final BooleanValue preserveOverWaterFunctionality;
    private final BooleanValue preserveLightMelting;
    private final IntValue iceMeltLightLevel;
    
    public ConfigServer() {
        
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        // General Configs
        builder.comment("Server side configurations for the mod. These have no effect on client side if playing on a multiplayer server");
        builder.push("Server");

        builder.comment("Whether or not the standard functionality should be maintained when ice is above a solid block. E.g. turns IceBlocks into water over solid blocks");
        this.preserveOverBlockFunctionality = builder.define("preserveOverBlockFunctionality", false);
        
        builder.comment("Whether or not the standard functionality should be maintained when ice is above a water block. E.g. turns IceBlocks into water over water blocks");
        this.preserveOverWaterFunctionality = builder.define("preserveOverWaterFunctionality", false);
        
        builder.comment("Whether or not the light level can still melt ice naturally.");
        this.preserveLightMelting = builder.define("preserveLightMelting", true);
        
        builder.comment("Light level that ice should melt into water at (default 12)");
        this.iceMeltLightLevel = builder.defineInRange("iceMeltLightLevel", 12, 0, 15);

        builder.pop();
        this.spec = builder.build();
    }
    
    public boolean shouldReplaceOverWater() {
        return this.preserveOverWaterFunctionality.get();
    }
    
    public boolean shouldReplaceOverSolidBlock() {
        return this.preserveOverBlockFunctionality.get();
    }
    
    public boolean shouldIceMelt() {
        return this.preserveLightMelting.get();
    }
    
    public int getIceMeltLightLevel() {
        return this.iceMeltLightLevel.get();
    }
    
    public ForgeConfigSpec getSpec() {
        return this.spec;
    }
}