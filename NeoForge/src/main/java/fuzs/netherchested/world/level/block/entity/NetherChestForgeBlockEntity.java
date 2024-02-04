package fuzs.netherchested.world.level.block.entity;

import fuzs.puzzlesapi.api.limitlesscontainers.v1.LimitlessInvWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Forge methods copied from {@link net.minecraft.world.level.block.entity.ChestBlockEntity}.
 */
public class NetherChestForgeBlockEntity extends NetherChestBlockEntity {
    private LazyOptional<IItemHandlerModifiable> chestHandler;

    public NetherChestForgeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public void setBlockState(BlockState pBlockState) {
        super.setBlockState(pBlockState);
        if (this.chestHandler != null) {
            LazyOptional<?> oldHandler = this.chestHandler;
            this.chestHandler = null;
            oldHandler.invalidate();
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            if (this.chestHandler == null)
                this.chestHandler = LazyOptional.of(() -> new LimitlessInvWrapper(this.container));
            return this.chestHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (this.chestHandler != null) {
            this.chestHandler.invalidate();
            this.chestHandler = null;
        }
    }
}
