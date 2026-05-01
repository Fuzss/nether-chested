package fuzs.netherchested.common.world.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public abstract class LimitlessContainerMenu extends AbstractContainerMenu {

    public LimitlessContainerMenu(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Override
    public void doClick(int slotId, int button, ContainerInput clickType, Player player) {
        Inventory inventory = player.getInventory();
        if (clickType == ContainerInput.QUICK_CRAFT) {
            int i = this.quickcraftStatus;
            this.quickcraftStatus = getQuickcraftHeader(button);
            if ((i != 1 || this.quickcraftStatus != 2) && i != this.quickcraftStatus) {
                this.resetQuickCraft();
            } else if (this.getCarried().isEmpty()) {
                this.resetQuickCraft();
            } else if (this.quickcraftStatus == 0) {
                this.quickcraftType = getQuickcraftType(button);
                if (isValidQuickcraftType(this.quickcraftType, player)) {
                    this.quickcraftStatus = 1;
                    this.quickcraftSlots.clear();
                } else {
                    this.resetQuickCraft();
                }
            } else if (this.quickcraftStatus == 1) {
                Slot slot = this.slots.get(slotId);
                ItemStack itemStack = this.getCarried();
                if (LimitlessContainerUtils.canItemQuickReplace(slot, itemStack, true) && slot.mayPlace(itemStack) && (
                        this.quickcraftType == 2 || itemStack.getCount() > this.quickcraftSlots.size())
                        && this.canDragTo(slot)) {
                    this.quickcraftSlots.add(slot);
                }
            } else if (this.quickcraftStatus == 2) {
                if (!this.quickcraftSlots.isEmpty()) {
                    if (this.quickcraftSlots.size() == 1) {
                        int j = this.quickcraftSlots.iterator().next().index;
                        this.resetQuickCraft();
                        this.doClick(j, this.quickcraftType, ContainerInput.PICKUP, player);
                        return;
                    }

                    ItemStack itemStack2 = this.getCarried().copy();
                    if (itemStack2.isEmpty()) {
                        this.resetQuickCraft();
                        return;
                    }

                    int k = this.getCarried().getCount();

                    for (Slot slot2 : this.quickcraftSlots) {
                        ItemStack itemStack3 = this.getCarried();
                        if (slot2 != null && LimitlessContainerUtils.canItemQuickReplace(slot2, itemStack3, true)
                                && slot2.mayPlace(itemStack3) && (this.quickcraftType == 2
                                || itemStack3.getCount() >= this.quickcraftSlots.size()) && this.canDragTo(slot2)) {
                            int l = slot2.hasItem() ? slot2.getItem().getCount() : 0;
//                            int m = Math.min(itemStack2.getMaxStackSize(), slot2.getMaxStackSize(itemStack2));
                            int m = slot2.getMaxStackSize(itemStack2);
                            int n = Math.min(LimitlessContainerUtils.getQuickCraftPlaceCount(this.quickcraftSlots,
                                    this.quickcraftType,
                                    itemStack2,
                                    slot2) + l, m);
                            k -= n - l;
                            slot2.setByPlayer(itemStack2.copyWithCount(n));
                        }
                    }

                    itemStack2.setCount(k);
                    this.setCarried(itemStack2);
                }

                this.resetQuickCraft();
            } else {
                this.resetQuickCraft();
            }
        } else if (this.quickcraftStatus != 0) {
            this.resetQuickCraft();
        } else if ((clickType == ContainerInput.PICKUP || clickType == ContainerInput.QUICK_MOVE) && (button == 0
                || button == 1)) {
            ClickAction clickAction = button == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
            if (slotId == -999) {
                if (!this.getCarried().isEmpty()) {
                    if (clickAction == ClickAction.PRIMARY) {
                        LimitlessContainerUtils.drop(player, this.getCarried(), true);
                        this.setCarried(ItemStack.EMPTY);
                    } else {
                        LimitlessContainerUtils.drop(player, this.getCarried().split(1), true);
                    }
                }
            } else if (clickType == ContainerInput.QUICK_MOVE) {
                if (slotId < 0) {
                    return;
                }

                Slot slot = this.slots.get(slotId);
                if (!slot.mayPickup(player)) {
                    return;
                }

                ItemStack itemStack = this.quickMoveStack(player, slotId);

                while (!itemStack.isEmpty() && ItemStack.isSameItem(slot.getItem(), itemStack)) {
                    itemStack = this.quickMoveStack(player, slotId);
                }
            } else {
                if (slotId < 0) {
                    return;
                }

                Slot slot = this.slots.get(slotId);
                ItemStack itemStack = slot.getItem();
                ItemStack itemStack4 = this.getCarried();
                player.updateTutorialInventoryAction(itemStack4, slot.getItem(), clickAction);
                if (!this.tryItemClickBehaviourOverride(player, clickAction, slot, itemStack, itemStack4)) {
                    if (itemStack.isEmpty()) {
                        if (!itemStack4.isEmpty()) {
                            int o = clickAction == ClickAction.PRIMARY ? itemStack4.getCount() : 1;
                            this.setCarried(slot.safeInsert(itemStack4, o));
                        }
                    } else if (slot.mayPickup(player)) {
                        if (itemStack4.isEmpty()) {
                            int o = clickAction == ClickAction.PRIMARY ? itemStack.getCount() :
                                    (itemStack.getCount() + 1) / 2;
                            Optional<ItemStack> optional = slot.tryRemove(o, Integer.MAX_VALUE, player);
                            optional.ifPresent(itemStackx -> {
                                this.setCarried(itemStackx);
                                slot.onTake(player, itemStackx);
                            });
                        } else if (slot.mayPlace(itemStack4)) {
                            if (ItemStack.isSameItemSameComponents(itemStack, itemStack4)) {
                                int o = clickAction == ClickAction.PRIMARY ? itemStack4.getCount() : 1;
                                this.setCarried(slot.safeInsert(itemStack4, o));
                            } else if (itemStack4.getCount() <= slot.getMaxStackSize(itemStack4)) {
                                this.setCarried(itemStack);
                                slot.setByPlayer(itemStack4);
                            }
                        } else if (ItemStack.isSameItemSameComponents(itemStack, itemStack4)) {
//                            Optional<ItemStack> optional2 = slot.tryRemove(itemStack.getCount(), itemStack4.getMaxStackSize() - itemStack4.getCount(), player);
                            Optional<ItemStack> optional2 = slot.tryRemove(itemStack.getCount(),
                                    slot.getMaxStackSize(itemStack4) - itemStack4.getCount(),
                                    player);
                            optional2.ifPresent(itemStack2x -> {
                                itemStack4.grow(itemStack2x.getCount());
                                slot.onTake(player, itemStack2x);
                            });
                        }
                    }
                }

                slot.setChanged();
            }
        } else if (clickType == ContainerInput.SWAP && (button >= 0 && button < 9 || button == 40)) {
            ItemStack itemStack5 = inventory.getItem(button);
            Slot slot = this.slots.get(slotId);
            ItemStack itemStack = slot.getItem();
            if (!itemStack5.isEmpty() || !itemStack.isEmpty()) {
                if (itemStack5.isEmpty()) {
                    if (slot.mayPickup(player)) {
//                        inventory.setItem(button, itemStack);
//                        slot.onSwapCraft(itemStack.getCount());
//                        slot.setByPlayer(ItemStack.EMPTY);
                        int maxStackSize = inventory.getMaxStackSize(itemStack);
                        if (itemStack.getCount() > maxStackSize) {
                            inventory.setItem(button, itemStack.split(maxStackSize));
                            slot.onSwapCraft(maxStackSize);
                        } else {
                            inventory.setItem(button, itemStack);
                            slot.onSwapCraft(itemStack.getCount());
                            slot.setByPlayer(ItemStack.EMPTY);
                        }
                        slot.onTake(player, itemStack);
                    }
                } else if (itemStack.isEmpty()) {
                    if (slot.mayPlace(itemStack5)) {
                        int maxStackSize = slot.getMaxStackSize(itemStack5);
                        if (itemStack5.getCount() > maxStackSize) {
                            slot.setByPlayer(itemStack5.split(maxStackSize));
                        } else {
                            inventory.setItem(button, ItemStack.EMPTY);
                            slot.setByPlayer(itemStack5);
                        }
                    }
                } else if (slot.mayPickup(player) && slot.mayPlace(itemStack5)) {
                    int maxStackSize = slot.getMaxStackSize(itemStack5);
                    if (itemStack5.getCount() > maxStackSize) {
                        slot.setByPlayer(itemStack5.split(maxStackSize));
                        slot.onTake(player, itemStack);
                        if (!inventory.add(itemStack)) {
                            LimitlessContainerUtils.drop(player, itemStack, true);
                        }
                        // only swap when the item stack can fit the inventory
                    } else if (itemStack.getCount() <= inventory.getMaxStackSize(itemStack)) {
                        inventory.setItem(button, itemStack);
                        slot.setByPlayer(itemStack5);
                        slot.onTake(player, itemStack);
                    }
                }
            }
        } else if (clickType == ContainerInput.CLONE && player.hasInfiniteMaterials() && this.getCarried().isEmpty()
                && slotId >= 0) {
            Slot slot3 = this.slots.get(slotId);
            if (slot3.hasItem()) {
                ItemStack itemStack2 = slot3.getItem();
//                this.setCarried(itemStack2.copyWithCount(itemStack2.getMaxStackSize()));
                this.setCarried(itemStack2.copyWithCount(slot3.getMaxStackSize(itemStack2)));
            }
        } else if (clickType == ContainerInput.THROW && this.getCarried().isEmpty() && slotId >= 0) {
            Slot slot3 = this.slots.get(slotId);
            int j = button == 0 ? 1 : slot3.getItem().getCount();
            if (!player.canDropItems()) {
                return;
            }

            ItemStack itemStack = slot3.safeTake(j, Integer.MAX_VALUE, player);
            LimitlessContainerUtils.drop(player, itemStack, true);
            player.handleCreativeModeItemDrop(itemStack);
            if (button == 1) {
                while (!itemStack.isEmpty() && ItemStack.isSameItem(slot3.getItem(), itemStack)) {
                    if (!player.canDropItems()) {
                        return;
                    }

                    itemStack = slot3.safeTake(j, Integer.MAX_VALUE, player);
                    LimitlessContainerUtils.drop(player, itemStack, true);
                    player.handleCreativeModeItemDrop(itemStack);
                }
            }
        } else if (clickType == ContainerInput.PICKUP_ALL && slotId >= 0) {
            Slot slot3x = this.slots.get(slotId);
            ItemStack itemStack2 = this.getCarried();
            if (!itemStack2.isEmpty() && (!slot3x.hasItem() || !slot3x.mayPickup(player))) {
                int k = button == 0 ? 0 : this.slots.size() - 1;
                int p = button == 0 ? 1 : -1;

                for (int o = 0; o < 2; o++) {
//                    for (int q = k; q >= 0 && q < this.slots.size() && itemStack2.getCount() < itemStack2.getMaxStackSize(); q += p) {
                    for (int q = k; q >= 0 && q < this.slots.size() && itemStack2.getCount() < this.slots.get(q)
                            .getMaxStackSize(itemStack2); q += p) {
                        Slot slot4 = this.slots.get(q);
                        if (slot4.hasItem() && LimitlessContainerUtils.canItemQuickReplace(slot4, itemStack2, true)
                                && slot4.mayPickup(player) && this.canTakeItemForPickAll(itemStack2, slot4)) {
                            ItemStack itemStack6 = slot4.getItem();
//                            if (o != 0 || itemStack6.getCount() != itemStack6.getMaxStackSize()) {
                            if (o != 0 || itemStack6.getCount() != slot4.getMaxStackSize(itemStack6)) {
//                                ItemStack itemStack7 = slot4.safeTake(itemStack6.getCount(), itemStack2.getMaxStackSize() - itemStack2.getCount(), player);
                                ItemStack itemStack7 = slot4.safeTake(itemStack6.getCount(),
                                        slot4.getMaxStackSize(itemStack2) - itemStack2.getCount(),
                                        player);
                                itemStack2.grow(itemStack7.getCount());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void removed(Player player) {
        if (player instanceof ServerPlayer) {
            ItemStack itemStack = this.getCarried();
            if (!itemStack.isEmpty()) {
                LimitlessContainerUtils.dropOrPlaceInInventory(player, itemStack);
                this.setCarried(ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void clearContainer(Player player, Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            LimitlessContainerUtils.dropOrPlaceInInventory(player, container.removeItemNoUpdate(i));
        }
    }
}
