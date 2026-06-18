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
    public void extractFloatingItem(GuiGraphicsExtractor graphics, ItemStack carried, int x, int y, @Nullable String itemCount) {
        graphics.item(carried, x, y);
//        graphics.itemDecorations(this.font, carried, x, y, itemCount);
        AdvancedItemRenderer.itemDecorations(graphics, this.font, carried, x, y, itemCount);
    }

    @Override
    public void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
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

//            if (AbstractContainerMenu.canItemQuickReplace(slot, carried, true) && this.menu.canDragTo(slot)) {
            if (LimitlessContainerUtils.canItemQuickReplace(slot, carried, true) && this.menu.canDragTo(slot)) {
                quickCraftStack = true;
//                int maxSize = Math.min(carried.getMaxStackSize(), slot.getMaxStackSize(carried));
                int maxSize = slot.getMaxStackSize(carried);
                int carry = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
//                int newCount = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots.size(),
//                        this.quickCraftingType,
//                        carried) + carry;
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
//                this.recalculateQuickCraftRemaining();
                this.recalculateQuickCraftRemaining(slot);
            }
        }

        if (itemStack.isEmpty() && slot.isActive()) {
            Identifier identifier = slot.getNoItemIcon();
            if (identifier != null) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, x, y, 16, 16);
                done = true;
            }
        }

        if (!done) {
            if (quickCraftStack) {
                graphics.fill(x, y, x + 16, y + 16, -2130706433);
            }

            int seed = slot.x + slot.y * this.imageWidth;
            if (slot.isFake()) {
                graphics.fakeItem(itemStack, x, y, seed);
            } else {
                graphics.item(itemStack, x, y, seed);
            }

//            graphics.itemDecorations(this.font, itemStack, x, y, itemCount);
            AdvancedItemRenderer.itemDecorations(graphics, this.font, itemStack, x, y, itemCount);
        }
    }

    /**
     * @see AbstractContainerScreen#recalculateQuickCraftRemaining()
     */
    private void recalculateQuickCraftRemaining(Slot slot) {
        ItemStack carried = this.menu.getCarried();
        if (!carried.isEmpty() && this.isQuickCrafting) {
            if (this.quickCraftingType == 2) {
//                this.quickCraftingRemainder = carried.getMaxStackSize();
                this.quickCraftingRemainder = slot.getMaxStackSize(carried);
            } else {
                this.quickCraftingRemainder = carried.getCount();
                for (Slot quickCraftSlot : this.quickCraftSlots) {
                    ItemStack slotItemStack = quickCraftSlot.getItem();
                    int carry = slotItemStack.isEmpty() ? 0 : slotItemStack.getCount();
//                    int maxSize = Math.min(carried.getMaxStackSize(), slot.getMaxStackSize(carried));
                    int maxSize = Math.min(slot.getMaxStackSize(carried), quickCraftSlot.getMaxStackSize(carried));
//                    int newCount = Math.min(AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots.size(),
//                            this.quickCraftingType,
//                            carried) + carry, maxSize);
                    int newCount = Math.min(LimitlessContainerUtils.getQuickCraftPlaceCount(this.quickCraftSlots,
                            this.quickCraftingType,
                            carried,
                            quickCraftSlot) + carry, maxSize);
                    this.quickCraftingRemainder -= newCount - carry;
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        Slot slot = this.getHoveredSlot(event.x(), event.y());
        ItemStack carried = this.menu.getCarried();
        if (slot != null && this.shouldAddSlotToQuickCraft(slot, carried) && this.quickCraftSlots.add(slot)) {
//            this.recalculateQuickCraftRemaining();
            this.recalculateQuickCraftRemaining(slot);
            return true;
        } else {
            return slot == null && this.menu.getCarried().isEmpty() ? super.mouseDragged(event, dx, dy) : true;
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        Slot slot = this.getHoveredSlot(event.x(), event.y());
        int xo = this.leftPos;
        int yo = this.topPos;
        boolean clickedOutside = this.hasClickedOutside(event.x(), event.y(), xo, yo);
        int slotId = -1;
        if (slot != null) {
            slotId = slot.index;
        }

        if (clickedOutside) {
            slotId = -999;
        }

        if (this.doubleclick && slot != null && event.button() == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY,
                slot)) {
            if (event.hasShiftDown()) {
                if (!this.lastQuickMoved.isEmpty()) {
                    for (Slot target : this.menu.slots) {
//                        if (target != null && target.mayPickup(this.minecraft.player) && target.hasItem()
//                                && target.container == slot.container && AbstractContainerMenu.canItemQuickReplace(
//                                target,
//                                this.lastQuickMoved,
//                                true)) {
                        if (target != null && target.mayPickup(this.minecraft.player) && target.hasItem()
                                && target.container == slot.container && LimitlessContainerUtils.canItemQuickReplace(
                                target,
                                this.lastQuickMoved,
                                true)) {
                            this.slotClicked(target, target.index, event.button(), ContainerInput.QUICK_MOVE);
                        }
                    }
                }
            } else {
                this.slotClicked(slot, slotId, event.button(), ContainerInput.PICKUP_ALL);
            }

            this.doubleclick = false;
        } else {
            if (this.isQuickCrafting && this.quickCraftingButton != event.button()) {
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
                if (this.minecraft.options.keyPickItem.matchesMouse(event)) {
                    this.slotClicked(slot, slotId, event.button(), ContainerInput.CLONE);
                } else {
                    boolean quickKey = slotId != -999 && event.hasShiftDown();
                    if (quickKey) {
                        this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                    }

                    this.slotClicked(slot,
                            slotId,
                            event.button(),
                            quickKey ? ContainerInput.QUICK_MOVE : ContainerInput.PICKUP);
                }
            }
        }

        this.isQuickCrafting = false;
        return true;
    }

    @Override
    public boolean shouldAddSlotToQuickCraft(Slot slot, ItemStack carried) {
//        return this.isQuickCrafting && !carried.isEmpty() && (carried.getCount() > this.quickCraftSlots.size()
//                || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace(slot, carried, true)
//                && slot.mayPlace(carried) && this.menu.canDragTo(slot);
        return this.isQuickCrafting && !carried.isEmpty() && (carried.getCount() > this.quickCraftSlots.size()
                || this.quickCraftingType == 2) && LimitlessContainerUtils.canItemQuickReplace(slot, carried, true)
                && slot.mayPlace(carried) && this.menu.canDragTo(slot);
    }
}
