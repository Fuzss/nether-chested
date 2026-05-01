package fuzs.netherchested.fabric;

import com.google.common.collect.MapMaker;
import fuzs.netherchested.common.world.MultipliedContainer;
import fuzs.netherchested.common.world.inventory.LimitlessContainerUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation heavily based on Fabric Api's {@link net.fabricmc.fabric.impl.transfer.item.ContainerStorageImpl} and
 * {@link net.fabricmc.fabric.impl.transfer.item.ContainerSlotWrapper}, but without all the special cases for vanilla
 * containers and no extensive performance considerations.
 */
@SuppressWarnings("UnstableApiUsage")
public class LimitlessSlotStorage extends SingleStackStorage {
    private static final Map<MultipliedContainer, Storage<ItemVariant>> WRAPPERS = new MapMaker().weakValues()
            .makeMap();

    private final MultipliedContainer container;
    private final int slot;

    LimitlessSlotStorage(MultipliedContainer container, int slot) {
        this.container = container;
        this.slot = slot;
    }

    @SafeVarargs
    public static <T extends BlockEntity & MultipliedContainer> void registerForBlockEntity(Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes) {
        registerForBlockEntity(Function.identity(), blockEntityTypes);
    }

    @SafeVarargs
    public static <T extends BlockEntity> void registerForBlockEntity(Function<? super T, MultipliedContainer> provider, Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes) {
        for (Holder<? extends BlockEntityType<? extends T>> blockEntityType : blockEntityTypes) {
            ItemStorage.SIDED.registerForBlockEntity((T blockEntity, @Nullable Direction direction) -> {
                return LimitlessSlotStorage.of(provider.apply(blockEntity), direction);
            }, blockEntityType.value());
        }
    }

    public static Storage<ItemVariant> of(MultipliedContainer container, @Nullable Direction direction) {
        return WRAPPERS.computeIfAbsent(container, LimitlessSlotStorage::getCombinedStorage);
    }

    private static Storage<ItemVariant> getCombinedStorage(MultipliedContainer container) {
        List<LimitlessSlotStorage> slots = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            slots.add(new LimitlessSlotStorage(container, i));
        }

        return new CombinedStorage<>(slots);
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
    protected int getCapacity(ItemVariant itemVariant) {
        return LimitlessContainerUtils.getMaxStackSize(itemVariant.toStack(), this.container.getStackSizeMultiplier())
                .orElseGet(() -> super.getCapacity(itemVariant));
    }

    @Override
    public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        if (!this.container.canPlaceItem(this.slot, insertedVariant.toStack())) {
            return 0;
        } else {
            return super.insert(insertedVariant, maxAmount, transaction);
        }
    }
}
