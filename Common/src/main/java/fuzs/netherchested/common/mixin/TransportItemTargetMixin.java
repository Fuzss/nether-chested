package fuzs.netherchested.common.mixin;

import fuzs.netherchested.common.world.level.block.entity.NetherChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla only knows how to extract a container from a {@link net.minecraft.world.level.block.ChestBlock} or from a
 * block entity that implements {@link Container} itself, neither of which applies to the nether chest.
 */
@Mixin(TransportItemsBetweenContainers.TransportItemTarget.class)
abstract class TransportItemTargetMixin {

    @Inject(method = "getBlockEntityContainer", at = @At("HEAD"), cancellable = true)
    private static void getBlockEntityContainer(BlockEntity blockEntity, BlockState blockState, Level level, BlockPos blockPos, CallbackInfoReturnable<Container> callback) {
        if (blockEntity instanceof NetherChestBlockEntity netherChestBlockEntity) {
            callback.setReturnValue(netherChestBlockEntity.getContainer());
        }
    }
}
