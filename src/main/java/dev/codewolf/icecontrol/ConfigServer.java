package dev.codewolf.icecontrol;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigServer {    
    private final ForgeConfigSpec spec;
    
    private final BooleanValue preserveOverBlockFunctionality;
    private final BooleanValue preserveOverWaterFunctionality;
    private final BooleanValue preserveIceLightMelting;
    private final BooleanValue preserveSnowLightMelting;
    private final BooleanValue slowSnowMeltEnabled;
    private final BooleanValue freezeTopLayerOnly;
    private final IntValue iceMeltLightLevel;
    private final IntValue snowMeltLightLevel;
    private final IntValue waterFreezeLightLevel;
    private final DoubleValue waterThawTemperature;
    private final IntValue maxIceThickness;
    private final EnumValue<FreezeSpeed> freezingSpeed;
    
    private java.util.Random random = new java.util.Random();
    public ConfigServer() {
        
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        // General Configs
        builder.comment("Server side configurations for the mod. These have no effect on client side if playing on a multiplayer server");
        builder.push("Server");

        builder.push("Ice Config");
        builder.comment("Whether or not the standard functionality should be maintained when ice is above a solid block. E.g. turns IceBlocks into water over solid blocks");
        this.preserveOverBlockFunctionality = builder.define("preserveOverBlockFunctionality", false);
        
        builder.comment("Whether or not the standard functionality should be maintained when ice is above a water block. E.g. turns IceBlocks into water over water blocks");
        this.preserveOverWaterFunctionality = builder.define("preserveOverWaterFunctionality", false);
        
        builder.comment("Whether or not the light level can still melt ice naturally.");
        this.preserveIceLightMelting = builder.define("preserveIceLightMelting", true);
        
        builder.comment("Light level that ice should melt into water at (default 12)");
        this.iceMeltLightLevel = builder.defineInRange("iceMeltLightLevel", 12, 0, 15);        
        builder.pop();


        builder.push("Snow Config");
        builder.comment("Whether or not the light level can still melt snow naturally.");
        this.preserveSnowLightMelting = builder.define("preserveSnowLightMelting", true);
        
        builder.comment("Light level that snow should melt at (default 12)");
        this.snowMeltLightLevel = builder.defineInRange("snowMeltLightLevel", 12, 0, 15);

        builder.comment("When enabled, snow layers will decrease one at a time while melting.");
        this.slowSnowMeltEnabled = builder.define("slowSnowMeltEnabled", true);
        builder.pop();


        builder.push("Water Config");

        builder.comment("Light level required to maintain liquid water (default 11)");        
        this.waterFreezeLightLevel = builder.defineInRange("waterFreezeLightLevel", 11, 0, 15);

        builder.comment("Biome temperature required to freeze water (default 0.15)");
        this.waterThawTemperature = builder.defineInRange("waterThawTemperature", 0.15, 0.0, 1.0);

        builder.comment("Freeze top layer only (vanilla behavior) or freeze multiple layers (default false)");
        this.freezeTopLayerOnly = builder.define("freezeTopLayerOnly", true);

        builder.comment("Maximum thickness of ice from the surface (default 3)");
        this.maxIceThickness = builder.defineInRange("maxIceThickness", 3, 1, 256);

        builder.comment("Speed blocks will freeze at.  NORMAL = Approximately vanilla rate, FASTEST = matches random tick rate");
        this.freezingSpeed = builder.defineEnum("freezingSpeed", FreezeSpeed.NORMAL);
        builder.pop();


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
        return this.preserveIceLightMelting.get();
    }
    
    public int getIceMeltLightLevel() {
        return this.iceMeltLightLevel.get();
    }
    
    public ForgeConfigSpec getSpec() {
        return this.spec;
    }

	public boolean shouldSnowMelt() {
		return preserveSnowLightMelting.get();
	}

	public int getSnowMeltLightLevel() {
		return snowMeltLightLevel.get();
    }

    public int getWaterFreezeLightLevel() {
        return waterFreezeLightLevel.get();
    }
    
    public boolean slowSnowMeltEnabled() {
        return slowSnowMeltEnabled.get();
    }

	public float getWaterThawTemperature() {
		return waterThawTemperature.get().floatValue();
	}

	public boolean getFreezeTopLayerOnly() {
		return freezeTopLayerOnly.get();
    }
    
    public boolean shouldBlockFreeze() {
        FreezeSpeed value =  freezingSpeed.get();
        int chance;
        switch(value) {
            case FAST: {
                chance = 256;
                break;
            }
            case FASTER: {
                chance = 128;
                break;
            }
            case FASTEST: {
                chance = 1;
                break;
            }
            case NORMAL: {
                chance = 512;
                break;
            }
            default: {
                chance = 512;
                break;
            }
        }
        return random.nextInt(chance) == 0;
    }

    public int getMaxIceThickness() {
        return maxIceThickness.get();
    }
}