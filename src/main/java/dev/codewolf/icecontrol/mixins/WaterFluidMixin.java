package dev.codewolf.icecontrol.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.codewolf.icecontrol.IceControl;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin extends Fluid {

    @Override
    protected boolean ticksRandomly() {
        return true;
    }

    // Adds random tick freezing behavior, using biome temperature (not block temperature)
    @Override
    protected void randomTick(World worldIn, BlockPos pos, FluidState state, Random random) {
        Block up = worldIn.getBlockState(pos.up()).getBlock();
        if (worldIn.getBiome(pos).field_242423_j.field_242461_c >= IceControl.COMMON_CONFIG.getWaterThawTemperature() || 
            up == Blocks.WATER || 
            ((up == Blocks.ICE || up == Blocks.BLUE_ICE || up == Blocks.PACKED_ICE) && !IceControl.COMMON_CONFIG.getFreezeTopLayerOnly()) ||
            !IceControl.COMMON_CONFIG.shouldBlockFreeze()) {
            return;
        } else {
            if (pos.getY() >= 0 && pos.getY() < 256 && worldIn.getLightFor(LightType.BLOCK,
                    pos) <= IceControl.COMMON_CONFIG.getWaterFreezeLightLevel()) {

                if (IceControl.COMMON_CONFIG.allowNucleation()) {
                    turnToIce(worldIn, pos);
                    return;
                }

                boolean flag = worldIn.hasWater(pos.west()) && worldIn.hasWater(pos.east())
                        && worldIn.hasWater(pos.north()) && worldIn.hasWater(pos.south());
                if (!flag) {
                    turnToIce(worldIn, pos);
                }
            }
        }
    }

    private void turnToIce(World worldIn, BlockPos pos) {
        worldIn.setBlockState(pos, Blocks.ICE.getDefaultState(), 3);
        worldIn.neighborChanged(pos, Blocks.ICE, pos);
    }

}
