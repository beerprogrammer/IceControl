package dev.codewolf.icecontrol;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;

@Mod(IceControl.MOD_ID)
public class IceControl {
    public static final String MOD_ID = "icecontrol";
    public static final ConfigServer COMMON_CONFIG = new ConfigServer();

    public IceControl() {
        ModLoadingContext.get().registerConfig(Type.COMMON, COMMON_CONFIG.getSpec());
    }
}