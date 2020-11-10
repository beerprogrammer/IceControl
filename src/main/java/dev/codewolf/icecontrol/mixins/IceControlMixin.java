package dev.codewolf.icecontrol.mixins;

import java.util.Random;
import net.minecraft.block.IceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.codewolf.icecontrol.IceControl;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

@Mixin(IceBlock.class)
public abstract class IceControlMixin extends Block {

    public IceControlMixin(Properties properties) {
        super(properties);
    }

    // Replaces the harvesting of IceBlock to only turn to water if the block below
    // is water
    @Inject(at = @At("HEAD"), method = "harvestBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/item/ItemStack;)V", cancellable = true)
    private void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state,
            @Nullable TileEntity te, ItemStack stack, CallbackInfo callbackInfo) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            if (worldIn.func_230315_m_().func_236040_e_()) { // If is superhot
                worldIn.removeBlock(pos, false);
                return;
            }
            Material material = worldIn.getBlockState(pos.down()).getMaterial();
            if ((IceControl.COMMON_CONFIG.shouldReplaceOverSolidBlock() && material.blocksMovement()) || (IceControl.COMMON_CONFIG.shouldReplaceOverWater() && material.isLiquid())) {
                worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
                return;
            }
        }
        callbackInfo.cancel();
    }

    // Replaces melting behavior for IceBlock based on configuration
    @Inject(at = @At("HEAD"), method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V", cancellable = true)
    private void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random, CallbackInfo callbackInfo) {
        if (IceControl.COMMON_CONFIG.shouldIceMelt() && worldIn.getLightFor(LightType.BLOCK, pos) >= IceControl.COMMON_CONFIG.getIceMeltLightLevel() - state.getOpacity(worldIn, pos)) {
            if (worldIn.func_230315_m_().func_236040_e_()) {
                worldIn.removeBlock(pos, false);
            } else {
                worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
                worldIn.neighborChanged(pos, Blocks.WATER, pos);
            }
        }
        callbackInfo.cancel();
    }
}