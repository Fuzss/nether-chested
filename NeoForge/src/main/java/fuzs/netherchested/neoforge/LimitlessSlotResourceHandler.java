package fuzs.netherchested.neoforge;

import com.google.common.collect.MapMaker;
import fuzs.netherchested.common.world.MultipliedContainer;
import fuzs.netherchested.common.world.inventory.LimitlessContainerUtils;
import fuzs.puzzleslib.neoforge.api.init.v3.capability.NeoForgeCapabilityHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Copied from the existing implementation on Fabric.
 */
public class LimitlessSlotResourceHandler extends ItemStackResourceHandler {
    private static final Map<MultipliedContainer, ResourceHandler<ItemResource>> WRAPPERS = new MapMaker().weakValues()
            .makeMap();

    private final MultipliedContainer container;
    private final int slot;

    LimitlessSlotResourceHandler(MultipliedContainer container, int slot) {
        this.container = container;
        this.slot = slot;
    }

    @SafeVarargs
    public static <T extends BlockEntity & MultipliedContainer> void registerLimitlessBlockEntityContainer(Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes) {
        registerLimitlessBlockEntityContainer(Function.identity(), blockEntityTypes);
    }

    @SafeVarargs
    public static <T extends BlockEntity> void registerLimitlessBlockEntityContainer(Function<? super T, MultipliedContainer> provider, Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes) {
        NeoForgeCapabilityHelper.registerBlockEntity((T blockEntity, @Nullable Direction direction) -> {
            return of(provider.apply(blockEntity), direction);
        }, blockEntityTypes);
    }

    public static ResourceHandler<ItemResource> of(MultipliedContainer container, @Nullable Direction direction) {
        return WRAPPERS.computeIfAbsent(container, LimitlessSlotResourceHandler::getCombinedStorage);
    }

    private static ResourceHandler<ItemResource> getCombinedStorage(MultipliedContainer container) {
        List<LimitlessSlotResourceHandler> slots = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            slots.add(new LimitlessSlotResourceHandler(container, i));
        }

        return new CombinedResourceHandler<>(slots.toArray(LimitlessSlotResourceHandler[]::new));
    }

    @Override
    protected ItemStack getStack() {
        return this.container.getItem(this.slot);
    }

    @Override
    protected void setStack(ItemStack itemStack) {
        this.container.setItem(this.slot, itemStack);
    }

    @Override
    protected int getCapacity(ItemResource itemResource) {
        return LimitlessContainerUtils.getMaxStackSize(itemResource.toStack(), this.container.getStackSizeMultiplier())
                .orElseGet(() -> super.getCapacity(itemResource));
    }

    @Override
    public int insert(int index, ItemResource itemResource, int maxAmount, TransactionContext transaction) {
        if (!this.container.canPlaceItem(this.slot, itemResource.toStack())) {
            return 0;
        } else {
            return super.insert(index, itemResource, maxAmount, transaction);
        }
    }
}
