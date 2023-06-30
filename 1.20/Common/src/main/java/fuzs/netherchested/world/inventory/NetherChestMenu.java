package fuzs.netherchested.world.inventory;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.config.ServerConfig;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NetherChestMenu extends LimitlessContainerMenu {
    private final int containerRows = 6;
    private final Container container;

    public NetherChestMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new MultipliedSimpleContainer(NetherChested.CONFIG.get(ServerConfig.class).stackSizeMultiplier, NetherChestBlockEntity.CONTAINER_SIZE));
    }

    public NetherChestMenu(int containerId, Inventory inventory, MultipliedContainer container) {
        super(ModRegistry.NETHER_CHEST_MENU_TYPE.get(), containerId);
        checkContainerSize(container, this.containerRows * 9);
        this.container = container;
        container.startOpen(inventory.player);
        for (int l = 0; l < this.containerRows; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new MultipliedSlot(container, m + l * 9, 8 + m * 18, 18 + 1 + l * 18));
            }
        }

        int k = (this.containerRows - 4) * 18;

        for (int l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(inventory, m + l * 9 + 9, 8 + m * 18, 103 + 6 + l * 18 + k));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l, 8 + l * 18, 161 + 6 + k));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index < this.containerRows * 9) {
                if (!this.moveItemStackTo(itemStack2, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack2, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public Container getContainer() {
        return this.container;
    }
}