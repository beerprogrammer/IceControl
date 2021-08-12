package dev.codewolf.icecontrol.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import dev.codewolf.icecontrol.IceControl;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin extends Fluid {

    private static ITag<Block> WATER_FREEZES_BENEATH = null;

    @Override
    protected boolean ticksRandomly() {
        return true;
    }

    // Adds random tick freezing behavior, using biome temperature (not block temperature)
    @Override
    protected void randomTick(World worldIn, BlockPos pos, FluidState state, Random random) {
        initializeTag();
        Block up = worldIn.getBlockState(pos.up()).getBlock();
        if (worldIn.getBiome(pos).field_242423_j.field_242461_c >= IceControl.COMMON_CONFIG.getWaterThawTemperature() || 
            (!WATER_FREEZES_BENEATH.func_230235_a_(up)) || // up != Blocks.AIR && up != Blocks.ICE && up != Blocks.PACKED_ICE && up != Blocks.BLUE_ICE
            (up != Blocks.AIR && IceControl.COMMON_CONFIG.getFreezeTopLayerOnly()) || // Up is ice and freeze top layer only
            !IceControl.COMMON_CONFIG.shouldBlockFreeze()) {
            return;
        } else {
            if (pos.getY() >= 0 && pos.getY() < 256 && worldIn.getLightFor(LightType.BLOCK,
                    pos) <= IceControl.COMMON_CONFIG.getWaterFreezeLightLevel()) {

                // Check additional blocks above for ice\
                int limit = IceControl.COMMON_CONFIG.getMaxIceThickness() - 1;
                BlockPos mutablePos = pos.up();
                for(int i = 0; i < limit; i++) {
                    mutablePos = mutablePos.up();
                    up = worldIn.getBlockState(mutablePos).getBlock();
                    if(up != Blocks.AIR && WATER_FREEZES_BENEATH.func_230235_a_(up)) {
                        continue;
                    }
                    else if(up == Blocks.AIR) {
                        turnToIce(worldIn, pos);
                        return;
                    } else { // Water only freezes below certain blocks
                        return;
                    }
                }
            }
        }
    }

    private void initializeTag() {
        if(WATER_FREEZES_BENEATH == null) {
            WATER_FREEZES_BENEATH = BlockTags.getCollection().func_241834_b(new ResourceLocation(IceControl.MOD_ID, "water_freezes_beneath"));
        }
    }

    private void turnToIce(World worldIn, BlockPos pos) {
        worldIn.setBlockState(pos, Blocks.ICE.getDefaultState(), 3);
        worldIn.neighborChanged(pos, Blocks.ICE, pos);
    }

}
