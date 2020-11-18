package dev.codewolf.icecontrol.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistryEntry;

@Mixin(Biome.class)
public abstract class BiomeFreezeControlMixin extends ForgeRegistryEntry<Biome> {

    @Shadow
    private float getTemperature(BlockPos pos) {
        return 0.0f;
    }

    // Skip, handled in randomTick in WaterFluidMixin
    @Inject(at = @At("HEAD"), method = "doesWaterFreeze(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;Z)Z", cancellable = true)
    private void doesWaterFreeze(IWorldReader worldIn, BlockPos water, boolean mustBeAtEdge,
            CallbackInfoReturnable<Boolean> callbackInfo) {
                callbackInfo.setReturnValue(false);
    }
}
