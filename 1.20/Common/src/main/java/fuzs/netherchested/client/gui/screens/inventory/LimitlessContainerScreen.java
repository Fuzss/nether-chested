package fuzs.netherchested.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import fuzs.netherchested.NetherChested;
import fuzs.netherchested.network.client.ServerboundContainerClickMessage;
import fuzs.netherchested.world.inventory.UnlimitedContainerUtils;
import fuzs.puzzlesaccessapi.api.client.container.v1.ExtendableContainerScreen;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class LimitlessContainerScreen<T extends AbstractContainerMenu> extends ExtendableContainerScreen<T> {

    public LimitlessContainerScreen(T abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack itemStack) {
        List<Component> lines = super.getTooltipFromContainerItem(itemStack);
        AdvancedItemRenderer.getStackSizeComponent(itemStack).ifPresent(component -> {
            final int index = 1;
            if (index <= lines.size()) {
                lines.add(1, component);
            } else {
                lines.add(component);
            }
        });

        return lines;
    }

    @Override
    protected void _renderFloatingItem(GuiGraphics guiGraphics, ItemStack itemStack, int i, int j, String string) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 232.0F);
        guiGraphics.renderItem(itemStack, i, j);
        AdvancedItemRenderer.renderItemDecorations(guiGraphics, this.font, itemStack, i, j - (this.getDraggingItem().isEmpty() ? 0 : 8), string);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void _renderSlot(GuiGraphics guiGraphics, Slot slot) {
        int i = slot.x;
        int j = slot.y;
        ItemStack itemStack = slot.getItem();
        boolean bl = false;
        boolean bl2 = slot == this.getClickedSlot() && !this.getDraggingItem().isEmpty() && !this.isSplittingStack();
        ItemStack itemStack2 = this.menu.getCarried();
        String string = null;
        if (slot == this.getClickedSlot() && !this.getDraggingItem().isEmpty() && this.isSplittingStack() && !itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            itemStack.setCount(itemStack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (UnlimitedContainerUtils.canItemQuickReplace(slot, itemStack2, true) && this.menu.canDragTo(slot)) {
                itemStack = itemStack2.copy();
                bl = true;
                UnlimitedContainerUtils.getQuickCraftSlotCount(this.quickCraftSlots, this.getQuickCraftingType(), itemStack, slot.getItem().isEmpty() ? 0 : slot.getItem().getCount(), slot);
                int k = slot.getMaxStackSize(itemStack);
                if (itemStack.getCount() > k) {
                    string = ChatFormatting.YELLOW.toString() + k;
                    itemStack.setCount(k);
                }
            } else {
                this.quickCraftSlots.remove(slot);
                this._recalculateQuickCraftRemaining();
            }
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        if (itemStack.isEmpty() && slot.isActive()) {
            Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
            if (pair != null) {
                TextureAtlasSprite textureAtlasSprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                RenderSystem.setShaderTexture(0, textureAtlasSprite.atlasLocation());
                guiGraphics.blit(i, j, 0, 16, 16, textureAtlasSprite);
                bl2 = true;
            }
        }

        if (!bl2) {
            if (bl) {
                guiGraphics.fill(i, j, i + 16, j + 16, -2130706433);
            }

            guiGraphics.renderItem(this.minecraft.player, itemStack, i, j, slot.x + slot.y * this.imageWidth);
            AdvancedItemRenderer.renderItemDecorations(guiGraphics, this.font, itemStack, i, j, string);
        }

        guiGraphics.pose().popPose();
    }

    @Override
    protected void _recalculateQuickCraftRemaining() {
        ItemStack itemStack = this.menu.getCarried();
        if (!itemStack.isEmpty() && this.isQuickCrafting) {
            if (this.getQuickCraftingType() == 2) {
                this.setQuickCraftingRemainder(itemStack.getMaxStackSize());
            } else {
                this.setQuickCraftingRemainder(itemStack.getCount());

                for (Slot slot : this.quickCraftSlots) {
                    ItemStack itemStack2 = itemStack.copy();
                    ItemStack itemStack3 = slot.getItem();
                    int i = itemStack3.isEmpty() ? 0 : itemStack3.getCount();
                    UnlimitedContainerUtils.getQuickCraftSlotCount(this.quickCraftSlots, this.getQuickCraftingType(), itemStack2, i, slot);
                    int j = slot.getMaxStackSize(itemStack2);
                    if (itemStack2.getCount() > j) {
                        itemStack2.setCount(j);
                    }

                    this.setQuickCraftingRemainder(this.getQuickCraftingRemainder() - (itemStack2.getCount() - i));
                }

            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        Slot slot = this._findSlot(mouseX, mouseY);
        ItemStack itemStack = this.menu.getCarried();
        if (this.getClickedSlot() != null && this.minecraft.options.touchscreen().get()) {
            if (button == 0 || button == 1) {
                if (this.getDraggingItem().isEmpty()) {
                    if (slot != this.getClickedSlot() && !this.getClickedSlot().getItem().isEmpty()) {
                        this.setDraggingItem(this.getClickedSlot().getItem().copy());
                    }
                } else if (this.getDraggingItem().getCount() > 1 && slot != null && UnlimitedContainerUtils.canItemQuickReplace(slot, this.getDraggingItem(), false)) {
                    long l = Util.getMillis();
                    if (this.getQuickdropSlot() == slot) {
                        if (l - this.getQuickdropTime() > 500L) {
                            this.slotClicked(this.getClickedSlot(), this.getClickedSlot().index, 0, ClickType.PICKUP);
                            this.slotClicked(slot, slot.index, 1, ClickType.PICKUP);
                            this.slotClicked(this.getClickedSlot(), this.getClickedSlot().index, 0, ClickType.PICKUP);
                            this.setQuickdropTime(l + 750L);
                            this.getDraggingItem().shrink(1);
                        }
                    } else {
                        this.setQuickdropSlot(slot);
                        this.setQuickdropTime(l);
                    }
                }
            }
        } else if (this.isQuickCrafting && slot != null && !itemStack.isEmpty() && (itemStack.getCount() > this.quickCraftSlots.size() || this.getQuickCraftingType() == 2) && UnlimitedContainerUtils.canItemQuickReplace(slot, itemStack, true) && slot.mayPlace(itemStack) && this.menu.canDragTo(slot)) {
            this.quickCraftSlots.add(slot);
            this._recalculateQuickCraftRemaining();
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Slot slot = this._findSlot(mouseX, mouseY);
        int i = this.leftPos;
        int j = this.topPos;
        boolean bl = this.hasClickedOutside(mouseX, mouseY, i, j, button);
        int k = -1;
        if (slot != null) {
            k = slot.index;
        }

        if (bl) {
            k = -999;
        }

        if (this.isDoubleclick() && slot != null && button == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, slot)) {
            if (hasShiftDown()) {
                if (!this.getLastQuickMoved().isEmpty()) {
                    for (Slot slot2 : this.menu.slots) {
                        if (slot2 != null && slot2.mayPickup(this.minecraft.player) && slot2.hasItem() && slot2.container == slot.container && UnlimitedContainerUtils.canItemQuickReplace(slot2, this.getLastQuickMoved(), true)) {
                            this.slotClicked(slot2, slot2.index, button, ClickType.QUICK_MOVE);
                        }
                    }
                }
            } else {
                this.slotClicked(slot, k, button, ClickType.PICKUP_ALL);
            }

            this.setDoubleclick(false);
            this.setLastClickTime(0L);
        } else {
            if (this.isQuickCrafting && this.getQuickCraftingButton() != button) {
                this.isQuickCrafting = false;
                this.quickCraftSlots.clear();
                this.setSkipNextRelease(true);
                return true;
            }

            if (this.isSkipNextRelease()) {
                this.setSkipNextRelease(false);
                return true;
            }

            if (this.getClickedSlot() != null && this.minecraft.options.touchscreen().get()) {
                if (button == 0 || button == 1) {
                    if (this.getDraggingItem().isEmpty() && slot != this.getClickedSlot()) {
                        this.setDraggingItem(this.getClickedSlot().getItem());
                    }

                    boolean bl2 = UnlimitedContainerUtils.canItemQuickReplace(slot, this.getDraggingItem(), false);
                    if (k != -1 && !this.getDraggingItem().isEmpty() && bl2) {
                        this.slotClicked(this.getClickedSlot(), this.getClickedSlot().index, button, ClickType.PICKUP);
                        this.slotClicked(slot, k, 0, ClickType.PICKUP);
                        if (this.menu.getCarried().isEmpty()) {
                            this.setSnapbackItem(ItemStack.EMPTY);
                        } else {
                            this.slotClicked(this.getClickedSlot(), this.getClickedSlot().index, button, ClickType.PICKUP);
                            this.setSnapbackStartX(Mth.floor(mouseX - (double) i));
                            this.setSnapbackStartY(Mth.floor(mouseY - (double) j));
                            this.setSnapbackEnd(this.getClickedSlot());
                            this.setSnapbackItem(this.getDraggingItem());
                            this.setSnapbackTime(Util.getMillis());
                        }
                    } else if (!this.getDraggingItem().isEmpty()) {
                        this.setSnapbackStartX(Mth.floor(mouseX - (double) i));
                        this.setSnapbackStartY(Mth.floor(mouseY - (double) j));
                        this.setSnapbackEnd(this.getClickedSlot());
                        this.setSnapbackItem(this.getDraggingItem());
                        this.setSnapbackTime(Util.getMillis());
                    }

                    this.clearDraggingState();
                }
            } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.getQuickCraftingType()), ClickType.QUICK_CRAFT);

                for (Slot slot2 : this.quickCraftSlots) {
                    this.slotClicked(slot2, slot2.index, AbstractContainerMenu.getQuickcraftMask(1, this.getQuickCraftingType()), ClickType.QUICK_CRAFT);
                }

                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.getQuickCraftingType()), ClickType.QUICK_CRAFT);
            } else if (!this.menu.getCarried().isEmpty()) {
                if (this.minecraft.options.keyPickItem.matchesMouse(button)) {
                    this.slotClicked(slot, k, button, ClickType.CLONE);
                } else {
                    boolean bl2 = k != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                    if (bl2) {
                        this.setLastQuickMoved(slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY);
                    }

                    this.slotClicked(slot, k, button, bl2 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }

        if (this.menu.getCarried().isEmpty()) {
            this.setLastClickTime(0L);
        }

        this.isQuickCrafting = false;
        return true;
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot != null) {
            slotId = slot.index;
        }

        this.handleInventoryMouseClick(this.menu.containerId, slotId, mouseButton, type, this.minecraft.player);
    }

    private void handleInventoryMouseClick(int containerId, int slotId, int mouseButton, ClickType clickType, Player player) {
        AbstractContainerMenu abstractContainerMenu = player.containerMenu;
        if (containerId != abstractContainerMenu.containerId) {
            NetherChested.LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", containerId, abstractContainerMenu.containerId);
        } else {
            NonNullList<Slot> nonNullList = abstractContainerMenu.slots;
            int i = nonNullList.size();
            List<ItemStack> list = Lists.newArrayListWithCapacity(i);

            for (Slot slot : nonNullList) {
                list.add(slot.getItem().copy());
            }

            abstractContainerMenu.clicked(slotId, mouseButton, clickType, player);
            Int2ObjectMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();

            for (int j = 0; j < i; ++j) {
                ItemStack itemStack = list.get(j);
                ItemStack itemStack2 = nonNullList.get(j).getItem();
                if (!ItemStack.matches(itemStack, itemStack2)) {
                    int2ObjectMap.put(j, itemStack2.copy());
                }
            }

            NetherChested.NETWORK.sendToServer(new ServerboundContainerClickMessage(containerId, abstractContainerMenu.getStateId(), slotId, mouseButton, clickType, abstractContainerMenu.getCarried().copy(), int2ObjectMap));
        }
    }
}
