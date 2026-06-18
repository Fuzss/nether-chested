package fuzs.netherchested.common.client.gui.screens.inventory;

import fuzs.netherchested.common.client.gui.AdvancedItemRenderer;
import fuzs.netherchested.common.world.inventory.LimitlessContainerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class LimitlessContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public LimitlessContainerScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    public LimitlessContainerScreen(T menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title, imageWidth, imageHeight);
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack itemStack) {
        List<Component> tooltipLines = super.getTooltipFromContainerItem(itemStack);
        AdvancedItemRenderer.getStackSizeComponent(itemStack).ifPresent((Component component) -> {
            tooltipLines.add(tooltipLines.isEmpty() ? 0 : 1, component);
        });
        return tooltipLines;
    }

    @Override
    public void extractFloatingItem(GuiGraphicsExtractor guiGraphics, ItemStack carried, int x, int y, @Nullable String itemCount) {
        guiGraphics.item(carried, x, y);
        AdvancedItemRenderer.itemDecorations(guiGraphics, this.font, carried, x, y, itemCount);
    }

    @Override
    public void extractSlot(GuiGraphicsExtractor guiGraphics, Slot slot, int mouseX, int mouseY) {
        int x = slot.x;
        int y = slot.y;
        ItemStack itemStack = slot.getItem();
        boolean quickCraftStack = false;
        boolean done = false;
        ItemStack carried = this.menu.getCarried();
        String itemCount = null;
        if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !carried.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (LimitlessContainerUtils.canItemQuickReplace(slot, carried, true) && this.menu.canDragTo(slot)) {
                quickCraftStack = true;
//                int maxSize = Math.min(carried.getMaxStackSize(), slot.getMaxStackSize(carried));
                int maxSize = slot.getMaxStackSize(carried);
                int carry = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
                int newCount = LimitlessContainerUtils.getQuickCraftPlaceCount(this.quickCraftSlots,
                        this.quickCraftingType,
                        carried,
                        slot) + carry;
                if (newCount > maxSize) {
                    newCount = maxSize;
                    itemCount = ChatFormatting.YELLOW.toString() + maxSize;
                }

                itemStack = carried.copyWithCount(newCount);
            } else {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining(slot);
            }
        }

        if (itemStack.isEmpty() && slot.isActive()) {
            Identifier identifier = slot.getNoItemIcon();
            if (identifier != null) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, x, y, 16, 16);
                done = true;
            }
        }

        if (!done) {
            if (quickCraftStack) {
                guiGraphics.fill(x, y, x + 16, y + 16, -2130706433);
            }

            int seed = slot.x + slot.y * this.imageWidth;
            if (slot.isFake()) {
                guiGraphics.fakeItem(itemStack, x, y, seed);
            } else {
                guiGraphics.item(itemStack, x, y, seed);
            }

            AdvancedItemRenderer.itemDecorations(guiGraphics, this.font, itemStack, x, y, itemCount);
        }
    }

    /**
     * @see AbstractContainerScreen#recalculateQuickCraftRemaining()
     */
    private void recalculateQuickCraftRemaining(Slot slot) {
        ItemStack itemStack = this.menu.getCarried();
        if (!itemStack.isEmpty() && this.isQuickCrafting) {
            if (this.quickCraftingType == 2) {
                this.quickCraftingRemainder = slot.getMaxStackSize(itemStack);
            } else {
                this.quickCraftingRemainder = itemStack.getCount();

                for (Slot quickCraftSlot : this.quickCraftSlots) {
                    ItemStack itemStack2 = quickCraftSlot.getItem();
                    int carry = itemStack2.isEmpty() ? 0 : itemStack2.getCount();
                    int maxSize = Math.min(itemStack.getMaxStackSize(), quickCraftSlot.getMaxStackSize(itemStack));
                    int newCount = Math.min(LimitlessContainerUtils.getQuickCraftPlaceCount(this.quickCraftSlots,
                            this.quickCraftingType,
                            itemStack,
                            quickCraftSlot) + carry, maxSize);
                    this.quickCraftingRemainder -= newCount - carry;
                }
            }
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        Slot slot = this.getHoveredSlot(mouseButtonEvent.x(), mouseButtonEvent.y());
        int xo = this.leftPos;
        int yo = this.topPos;
        boolean clickedOutside = this.hasClickedOutside(mouseButtonEvent.x(), mouseButtonEvent.y(), xo, yo);
        int slotId = -1;
        if (slot != null) {
            slotId = slot.index;
        }

        if (clickedOutside) {
            slotId = -999;
        }

        if (this.doubleclick && slot != null && mouseButtonEvent.button() == 0 && this.menu.canTakeItemForPickAll(
                ItemStack.EMPTY,
                slot)) {
            if (mouseButtonEvent.hasShiftDown()) {
                if (!this.lastQuickMoved.isEmpty()) {
                    for (Slot slot2 : this.menu.slots) {
                        if (slot2 != null && slot2.mayPickup(this.minecraft.player) && slot2.hasItem()
                                && slot2.container == slot.container && LimitlessContainerUtils.canItemQuickReplace(
                                slot2,
                                this.lastQuickMoved,
                                true)) {
                            this.slotClicked(slot2, slot2.index, mouseButtonEvent.button(), ContainerInput.QUICK_MOVE);
                        }
                    }
                }
            } else {
                this.slotClicked(slot, slotId, mouseButtonEvent.button(), ContainerInput.PICKUP_ALL);
            }

            this.doubleclick = false;
        } else {
            if (this.isQuickCrafting && this.quickCraftingButton != mouseButtonEvent.button()) {
                this.isQuickCrafting = false;
                this.quickCraftSlots.clear();
                this.skipNextRelease = true;
                return true;
            }

            if (this.skipNextRelease) {
                this.skipNextRelease = false;
                return true;
            }

            if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
                this.quickCraftToSlots();
            } else if (!this.menu.getCarried().isEmpty()) {
                if (this.minecraft.options.keyPickItem.matchesMouse(mouseButtonEvent)) {
                    this.slotClicked(slot, slotId, mouseButtonEvent.button(), ContainerInput.CLONE);
                } else {
                    boolean quickKey = slotId != -999 && mouseButtonEvent.hasShiftDown();
                    if (quickKey) {
                        this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                    }

                    this.slotClicked(slot,
                            slotId,
                            mouseButtonEvent.button(),
                            quickKey ? ContainerInput.QUICK_MOVE : ContainerInput.PICKUP);
                }
            }
        }

        this.isQuickCrafting = false;
        return true;
    }

    @Override
    public boolean shouldAddSlotToQuickCraft(Slot slot, ItemStack carried) {
        return this.isQuickCrafting && !carried.isEmpty() && (carried.getCount() > this.quickCraftSlots.size()
                || this.quickCraftingType == 2) && LimitlessContainerUtils.canItemQuickReplace(slot, carried, true)
                && slot.mayPlace(carried) && this.menu.canDragTo(slot);
    }
}
