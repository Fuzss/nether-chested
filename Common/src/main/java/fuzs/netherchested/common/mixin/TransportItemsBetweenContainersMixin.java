package fuzs.netherchested.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import fuzs.netherchested.common.init.ModRegistry;
import fuzs.netherchested.common.world.MultipliedContainer;
import fuzs.netherchested.common.world.level.block.entity.NamedBlockEntity;
import fuzs.netherchested.common.world.level.block.entity.NetherChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Lets copper golems drop off items in a nether chest, just like they do for vanilla chests. The nether chest is only
 * ever a drop-off target and never a pick-up source, again exactly like a vanilla chest.
 * <p>
 * This behavior rejects the nether chest in two independent places: the target search only ever considers
 * {@link ChestBlockEntity} instances, and the destination predicate tests for
 * {@link net.minecraft.world.level.block.Blocks#CHEST} and
 * {@link net.minecraft.world.level.block.Blocks#TRAPPED_CHEST} directly. On top of that, item insertion caps every
 * slot at the item's own max stack size, ignoring the nether chest's multiplier. Everything else (reachability, queuing,
 * opening the lid, moving the items) is container agnostic and works as-is, provided
 * {@link TransportItemTargetMixin} manages to hand out the container.
 */
@Mixin(TransportItemsBetweenContainers.class)
abstract class TransportItemsBetweenContainersMixin {
    @Shadow
    @Final
    private Predicate<BlockState> destinationBlockType;

    @Shadow
    private AABB getTargetSearchArea(PathfinderMob pathfinderMob) {
        throw new AssertionError();
    }

    @Shadow
    private int getHorizontalSearchDistance(PathfinderMob pathfinderMob) {
        throw new AssertionError();
    }

    @Shadow
    @Nullable
    private TransportItemsBetweenContainers.TransportItemTarget isTargetValidToPick(PathfinderMob pathfinderMob, Level level, BlockEntity blockEntity, Set<GlobalPos> visitedPositions, Set<GlobalPos> unreachablePositions, AABB searchArea) {
        throw new AssertionError();
    }

    @Shadow
    private static Set<GlobalPos> getVisitedPositions(PathfinderMob pathfinderMob) {
        throw new AssertionError();
    }

    @Shadow
    private static Set<GlobalPos> getUnreachablePositions(PathfinderMob pathfinderMob) {
        throw new AssertionError();
    }

    @Inject(method = "getTransportTarget", at = @At("RETURN"), cancellable = true)
    public void getTransportTarget(ServerLevel serverLevel, PathfinderMob pathfinderMob, CallbackInfoReturnable<Optional<TransportItemsBetweenContainers.TransportItemTarget>> callback) {
        if (this.isNetherChestWanted(pathfinderMob)) {
            TransportItemsBetweenContainers.TransportItemTarget target = callback.getReturnValue().orElse(null);
            TransportItemsBetweenContainers.TransportItemTarget nearestTarget = this.findNearerNetherChest(serverLevel,
                    pathfinderMob,
                    target);
            if (nearestTarget != target) {
                callback.setReturnValue(Optional.of(nearestTarget));
            }
        }
    }

    /**
     * Runs the vanilla chunk scan a second time, this time for nether chests, and returns whichever of the two targets
     * is closer to the mob.
     * <p>
     * This only ever runs right after the vanilla scan has run, which happens once per target acquisition and never
     * every tick: the behavior requires an absent cooldown memory, and sets that memory to 140 ticks whenever no
     * target is found at all. The distance comparison runs before the expensive validation, so a nether chest that
     * cannot win costs a single comparison.
     */
    @Unique
    @Nullable
    private TransportItemsBetweenContainers.TransportItemTarget findNearerNetherChest(ServerLevel serverLevel, PathfinderMob pathfinderMob, @Nullable TransportItemsBetweenContainers.TransportItemTarget nearestTarget) {
        Vec3 mobPosition = pathfinderMob.position();
        double nearestDistance = nearestTarget != null ?
                nearestTarget.pos().distToCenterSqr(mobPosition) :
                Double.MAX_VALUE;
        AABB searchArea = this.getTargetSearchArea(pathfinderMob);
        Set<GlobalPos> visitedPositions = getVisitedPositions(pathfinderMob);
        Set<GlobalPos> unreachablePositions = getUnreachablePositions(pathfinderMob);
        ServerChunkCache chunkSource = serverLevel.getChunkSource();
        BlockPos mobBlockPos = pathfinderMob.blockPosition();
        int centerChunkX = SectionPos.blockToSectionCoord(mobBlockPos.getX());
        int centerChunkZ = SectionPos.blockToSectionCoord(mobBlockPos.getZ());
        int chunkRadius = Math.floorDiv(this.getHorizontalSearchDistance(pathfinderMob), 16) + 1;

        for (int chunkX = centerChunkX - chunkRadius; chunkX <= centerChunkX + chunkRadius; chunkX++) {
            for (int chunkZ = centerChunkZ - chunkRadius; chunkZ <= centerChunkZ + chunkRadius; chunkZ++) {
                LevelChunk levelChunk = chunkSource.getChunkNow(chunkX, chunkZ);
                if (levelChunk == null) {
                    continue;
                }

                for (BlockEntity blockEntity : levelChunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof NetherChestBlockEntity)) {
                        continue;
                    }

                    double distance = blockEntity.getBlockPos().distToCenterSqr(mobPosition);
                    if (distance >= nearestDistance) {
                        continue;
                    }

                    TransportItemsBetweenContainers.TransportItemTarget target = this.isTargetValidToPick(pathfinderMob,
                            serverLevel,
                            blockEntity,
                            visitedPositions,
                            unreachablePositions,
                            searchArea);
                    if (target != null) {
                        nearestTarget = target;
                        nearestDistance = distance;
                    }
                }
            }
        }

        return nearestTarget;
    }

    /**
     * Vanilla caps every slot at the item's own max stack size, so a nether chest slot would never be topped up past a
     * regular stack, and golems would spread items across new slots instead.
     */
    @WrapOperation(method = "addItemsToContainer",
                   at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"))
    private static int addItemsToContainer(ItemStack itemStack, Operation<Integer> operation, @Local(argsOnly = true) Container container) {
        if (container instanceof MultipliedContainer multipliedContainer) {
            return multipliedContainer.getMaxStackSize(itemStack);
        } else {
            return operation.call(itemStack);
        }
    }

    @Inject(method = "isWantedBlock", at = @At("HEAD"), cancellable = true)
    public void isWantedBlock(PathfinderMob pathfinderMob, BlockState blockState, CallbackInfoReturnable<Boolean> callback) {
        if (blockState.is(ModRegistry.NETHER_CHEST_BLOCK.value()) && this.isNetherChestWanted(pathfinderMob)) {
            callback.setReturnValue(true);
        }
    }

    /**
     * Vanilla only ever finds the lock code on a
     * {@link net.minecraft.world.level.block.entity.BaseContainerBlockEntity}, which the nether chest is not.
     */
    @Inject(method = "isContainerLocked", at = @At("HEAD"), cancellable = true)
    public void isContainerLocked(TransportItemsBetweenContainers.TransportItemTarget target, CallbackInfoReturnable<Boolean> callback) {
        if (target.blockEntity() instanceof NamedBlockEntity namedBlockEntity) {
            callback.setReturnValue(namedBlockEntity.isLocked());
        }
    }

    /**
     * The mob must already be carrying an item, and this behavior must accept vanilla chests as a destination in the
     * first place, so that no other mob using this behavior is affected. Both checks are cheap enough to run before
     * any scanning happens.
     */
    @Unique
    private boolean isNetherChestWanted(PathfinderMob pathfinderMob) {
        return !pathfinderMob.getMainHandItem().isEmpty()
                && this.destinationBlockType.test(Blocks.CHEST.defaultBlockState());
    }
}
