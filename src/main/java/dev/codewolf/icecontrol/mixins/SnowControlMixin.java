package dev.codewolf.icecontrol.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.codewolf.icecontrol.IceControl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.server.ServerWorld;

@Mixin(SnowBlock.class)
public abstract class SnowControlMixin extends Block {

    public SnowControlMixin(Properties properties) {
        super(properties);
    }

    // Replaces melting behavior for SnowBlock based on configuration
    @Inject(at = @At("HEAD"), method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V", cancellable = true)
    private void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random,
            CallbackInfo callbackInfo) {
        if (IceControl.COMMON_CONFIG.shouldSnowMelt()
                && worldIn.getLightFor(LightType.BLOCK, pos) >= IceControl.COMMON_CONFIG.getSnowMeltLightLevel()) {
            if(IceControl.COMMON_CONFIG.slowSnowMeltEnabled() && state.get(SnowBlock.LAYERS) > 1) {
                worldIn.setBlockState(pos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, state.get(SnowBlock.LAYERS) - 1), 2);
            } else {
                spawnDrops(state, worldIn, pos);
                worldIn.removeBlock(pos, false);
            }
        }
        callbackInfo.cancel();
    }
}
