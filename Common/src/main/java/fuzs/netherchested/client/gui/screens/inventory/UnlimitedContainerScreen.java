package fuzs.netherchested.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import fuzs.netherchested.NetherChested;
import fuzs.netherchested.networking.ServerboundContainerClickMessage;
import fuzs.netherchested.world.inventory.UnlimitedContainerUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class UnlimitedContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    @Nullable
    private Slot clickedSlot;
    @Nullable
    private Slot snapbackEnd;
    @Nullable
    private Slot quickdropSlot;
    @Nullable
    private Slot lastClickSlot;
    private boolean isSplittingStack;
    private ItemStack draggingItem = ItemStack.EMPTY;
    private int snapbackStartX;
    private int snapbackStartY;
    private long snapbackTime;
    private ItemStack snapbackItem = ItemStack.EMPTY;
    private long quickdropTime;
    private int quickCraftingType;
    private int quickCraftingButton;
    private boolean skipNextRelease;
    private int quickCraftingRemainder;
    private long lastClickTime;
    private int lastClickButton;
    private boolean doubleclick;
    private ItemStack lastQuickMoved = ItemStack.EMPTY;

    public UnlimitedContainerScreen(T abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        this.skipNextRelease = true;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    public static void renderSlotHighlight(PoseStack poseStack, int x, int y, int blitOffset) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        fillGradient(poseStack, x, y, x + 16, y + 16, -2130706433, -2130706433, blitOffset);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void init() {
//        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int i = this.leftPos;
        int j = this.topPos;
        this.renderBg(poseStack, partialTick, mouseX, mouseY);
        RenderSystem.disableDepthTest();
//        super.render(poseStack, mouseX, mouseY, partialTick);
        PoseStack poseStack2 = RenderSystem.getModelViewStack();
        poseStack2.pushPose();
        poseStack2.translate(i, j, 0.0);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.hoveredSlot = null;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (int k = 0; k < this.menu.slots.size(); ++k) {
            Slot slot = this.menu.slots.get(k);
            if (slot.isActive()) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                this.renderSlot(poseStack, slot);
            }

            if (this.isHovering(slot, mouseX, mouseY) && slot.isActive()) {
                this.hoveredSlot = slot;
                int l = slot.x;
                int m = slot.y;
                renderSlotHighlight(poseStack, l, m, this.getBlitOffset());
            }
        }

        this.renderLabels(poseStack, mouseX, mouseY);
        ItemStack itemStack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
        if (!itemStack.isEmpty()) {
            int n = 8;
            int l = this.draggingItem.isEmpty() ? 8 : 16;
            Style string = Style.EMPTY;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                itemStack = itemStack.copy();
                itemStack.setCount(Mth.ceil((float) itemStack.getCount() / 2.0F));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                itemStack = itemStack.copy();
                itemStack.setCount(this.quickCraftingRemainder);
                if (itemStack.isEmpty()) {
                    string = string.withColor(ChatFormatting.YELLOW);
                }
            }

            this.renderFloatingItem(itemStack, mouseX - i - 8, mouseY - j - l, string);
        }

        if (!this.snapbackItem.isEmpty()) {
            float f = (float) (Util.getMillis() - this.snapbackTime) / 100.0F;
            if (f >= 1.0F) {
                f = 1.0F;
                this.snapbackItem = ItemStack.EMPTY;
            }

            int l = this.snapbackEnd.x - this.snapbackStartX;
            int m = this.snapbackEnd.y - this.snapbackStartY;
            int o = this.snapbackStartX + (int) ((float) l * f);
            int p = this.snapbackStartY + (int) ((float) m * f);
            this.renderFloatingItem(this.snapbackItem, o, p, Style.EMPTY);
        }

        poseStack2.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            this.renderTooltip(poseStack, this.hoveredSlot.getItem(), x, y);
        }

    }

    @Override
    protected void renderTooltip(PoseStack poseStack, ItemStack itemStack, int mouseX, int mouseY) {
        List<Component> tooltipFromItem = this.getTooltipFromItem(itemStack);
        AdvancedItemRenderer.getStackSizeComponent(itemStack).ifPresent(component -> tooltipFromItem.add(1, component));
        this.renderTooltip(poseStack, tooltipFromItem, itemStack.getTooltipImage(), mouseX, mouseY);
    }

    private void renderFloatingItem(ItemStack stack, int x, int y, Style altText) {
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.translate(0.0, 0.0, 32.0);
        RenderSystem.applyModelViewMatrix();
        this.setBlitOffset(200);
        this.itemRenderer.blitOffset = 200.0F;
        this.itemRenderer.renderAndDecorateItem(stack, x, y);
        AdvancedItemRenderer.renderGuiItemDecorations(this.font, stack, x, y - (this.draggingItem.isEmpty() ? 0 : 8), altText);
        this.setBlitOffset(0);
        this.itemRenderer.blitOffset = 0.0F;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
        this.font.draw(poseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, 4210752);
    }

    @Override
    protected abstract void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY);

    private void renderSlot(PoseStack poseStack, Slot slot) {
        int i = slot.x;
        int j = slot.y;
        ItemStack itemStack = slot.getItem();
        boolean bl = false;
        boolean bl2 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack itemStack2 = this.menu.getCarried();
        Style string = Style.EMPTY;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            itemStack.setCount(itemStack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (UnlimitedContainerUtils.canItemQuickReplace(slot, itemStack2, true) && this.menu.canDragTo(slot)) {
                itemStack = itemStack2.copy();
                bl = true;
                UnlimitedContainerUtils.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemStack, slot.getItem().isEmpty() ? 0 : slot.getItem().getCount(), slot);
                int k = slot.getMaxStackSize(itemStack);
                if (itemStack.getCount() > k) {
                    string = string.withColor(ChatFormatting.YELLOW);
                    itemStack.setCount(k);
                }
            } else {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining(slot);
            }
        }

        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;
        if (itemStack.isEmpty() && slot.isActive()) {
            Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
            if (pair != null) {
                TextureAtlasSprite textureAtlasSprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                RenderSystem.setShaderTexture(0, textureAtlasSprite.atlas().location());
                blit(poseStack, i, j, this.getBlitOffset(), 16, 16, textureAtlasSprite);
                bl2 = true;
            }
        }

        if (!bl2) {
            if (bl) {
                fill(poseStack, i, j, i + 16, j + 16, -2130706433);
            }

            RenderSystem.enableDepthTest();
            this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemStack, i, j, slot.x + slot.y * this.imageWidth);
            AdvancedItemRenderer.renderGuiItemDecorations(this.font, itemStack, i, j, string);
        }

        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    private void recalculateQuickCraftRemaining(Slot slot1) {
        ItemStack itemStack = this.menu.getCarried();
        if (!itemStack.isEmpty() && this.isQuickCrafting) {
            if (this.quickCraftingType == 2) {
                this.quickCraftingRemainder = slot1.getMaxStackSize(itemStack);
            } else {
                this.quickCraftingRemainder = itemStack.getCount();

                for (Slot slot : this.quickCraftSlots) {
                    ItemStack itemStack2 = itemStack.copy();
                    ItemStack itemStack3 = slot.getItem();
                    int i = itemStack3.isEmpty() ? 0 : itemStack3.getCount();
                    UnlimitedContainerUtils.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemStack2, i, slot);
                    int j = slot.getMaxStackSize(itemStack2);
                    if (itemStack2.getCount() > j) {
                        itemStack2.setCount(j);
                    }

                    this.quickCraftingRemainder -= itemStack2.getCount() - i;
                }

            }
        }
    }

    @Nullable
    private Slot findSlot(double mouseX, double mouseY) {
        for (int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = this.menu.slots.get(i);
            if (this.isHovering(slot, mouseX, mouseY) && slot.isActive()) {
                return slot;
            }
        }

        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = this.minecraft.options.keyPickItem.matchesMouse(button) && this.minecraft.gameMode.hasInfiniteItems();
        Slot slot = this.findSlot(mouseX, mouseY);
        long l = Util.getMillis();
        this.doubleclick = this.lastClickSlot == slot && l - this.lastClickTime < 250L && this.lastClickButton == button;
        this.skipNextRelease = false;
        if (button != 0 && button != 1 && !bl) {
            this.checkHotbarMouseClicked(button);
        } else {
            int i = this.leftPos;
            int j = this.topPos;
            boolean bl2 = this.hasClickedOutside(mouseX, mouseY, i, j, button);
            int k = -1;
            if (slot != null) {
                k = slot.index;
            }

            if (bl2) {
                k = -999;
            }

            if (this.minecraft.options.touchscreen().get() && bl2 && this.menu.getCarried().isEmpty()) {
                this.onClose();
                return true;
            }

            if (k != -1) {
                if (this.minecraft.options.touchscreen().get()) {
                    if (slot != null && slot.hasItem()) {
                        this.clickedSlot = slot;
                        this.draggingItem = ItemStack.EMPTY;
                        this.isSplittingStack = button == 1;
                    } else {
                        this.clickedSlot = null;
                    }
                } else if (!this.isQuickCrafting) {
                    if (this.menu.getCarried().isEmpty()) {
                        if (bl) {
                            this.slotClicked(slot, k, button, ClickType.CLONE);
                        } else {
                            boolean bl3 = k != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                            ClickType clickType = ClickType.PICKUP;
                            if (bl3) {
                                this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                                clickType = ClickType.QUICK_MOVE;
                            } else if (k == -999) {
                                clickType = ClickType.THROW;
                            }

                            this.slotClicked(slot, k, button, clickType);
                        }

                        this.skipNextRelease = true;
                    } else {
                        this.isQuickCrafting = true;
                        this.quickCraftingButton = button;
                        this.quickCraftSlots.clear();
                        if (button == 0) {
                            this.quickCraftingType = 0;
                        } else if (button == 1) {
                            this.quickCraftingType = 1;
                        } else if (bl) {
                            this.quickCraftingType = 2;
                        }
                    }
                }
            }
        }

        this.lastClickSlot = slot;
        this.lastClickTime = l;
        this.lastClickButton = button;
        return true;
    }

    private void checkHotbarMouseClicked(int keyCode) {
        if (this.hoveredSlot != null && this.menu.getCarried().isEmpty()) {
            if (this.minecraft.options.keySwapOffhand.matchesMouse(keyCode)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                return;
            }

            for (int i = 0; i < 9; ++i) {
                if (this.minecraft.options.keyHotbarSlots[i].matchesMouse(keyCode)) {
                    this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ClickType.SWAP);
                }
            }
        }
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        return mouseX < (double) guiLeft || mouseY < (double) guiTop || mouseX >= (double) (guiLeft + this.imageWidth) || mouseY >= (double) (guiTop + this.imageHeight);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        Slot slot = this.findSlot(mouseX, mouseY);
        ItemStack itemStack = this.menu.getCarried();
        if (this.clickedSlot != null && this.minecraft.options.touchscreen().get()) {
            if (button == 0 || button == 1) {
                if (this.draggingItem.isEmpty()) {
                    if (slot != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                        this.draggingItem = this.clickedSlot.getItem().copy();
                    }
                } else if (this.draggingItem.getCount() > 1 && slot != null && UnlimitedContainerUtils.canItemQuickReplace(slot, this.draggingItem, false)) {
                    long l = Util.getMillis();
                    if (this.quickdropSlot == slot) {
                        if (l - this.quickdropTime > 500L) {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.slotClicked(slot, slot.index, 1, ClickType.PICKUP);
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.quickdropTime = l + 750L;
                            this.draggingItem.shrink(1);
                        }
                    } else {
                        this.quickdropSlot = slot;
                        this.quickdropTime = l;
                    }
                }
            }
        } else if (this.isQuickCrafting && slot != null && !itemStack.isEmpty() && (itemStack.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && UnlimitedContainerUtils.canItemQuickReplace(slot, itemStack, true) && slot.mayPlace(itemStack) && this.menu.canDragTo(slot)) {
            this.quickCraftSlots.add(slot);
            this.recalculateQuickCraftRemaining(slot);
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Slot slot = this.findSlot(mouseX, mouseY);
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

        if (this.doubleclick && slot != null && button == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, slot)) {
            if (hasShiftDown()) {
                if (!this.lastQuickMoved.isEmpty()) {
                    for (Slot slot2 : this.menu.slots) {
                        if (slot2 != null && slot2.mayPickup(this.minecraft.player) && slot2.hasItem() && slot2.container == slot.container && UnlimitedContainerUtils.canItemQuickReplace(slot2, this.lastQuickMoved, true)) {
                            this.slotClicked(slot2, slot2.index, button, ClickType.QUICK_MOVE);
                        }
                    }
                }
            } else {
                this.slotClicked(slot, k, button, ClickType.PICKUP_ALL);
            }

            this.doubleclick = false;
            this.lastClickTime = 0L;
        } else {
            if (this.isQuickCrafting && this.quickCraftingButton != button) {
                this.isQuickCrafting = false;
                this.quickCraftSlots.clear();
                this.skipNextRelease = true;
                return true;
            }

            if (this.skipNextRelease) {
                this.skipNextRelease = false;
                return true;
            }

            if (this.clickedSlot != null && this.minecraft.options.touchscreen().get()) {
                if (button == 0 || button == 1) {
                    if (this.draggingItem.isEmpty() && slot != this.clickedSlot) {
                        this.draggingItem = this.clickedSlot.getItem();
                    }

                    boolean bl2 = UnlimitedContainerUtils.canItemQuickReplace(slot, this.draggingItem, false);
                    if (k != -1 && !this.draggingItem.isEmpty() && bl2) {
                        this.slotClicked(this.clickedSlot, this.clickedSlot.index, button, ClickType.PICKUP);
                        this.slotClicked(slot, k, 0, ClickType.PICKUP);
                        if (this.menu.getCarried().isEmpty()) {
                            this.snapbackItem = ItemStack.EMPTY;
                        } else {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, button, ClickType.PICKUP);
                            this.snapbackStartX = Mth.floor(mouseX - (double) i);
                            this.snapbackStartY = Mth.floor(mouseY - (double) j);
                            this.snapbackEnd = this.clickedSlot;
                            this.snapbackItem = this.draggingItem;
                            this.snapbackTime = Util.getMillis();
                        }
                    } else if (!this.draggingItem.isEmpty()) {
                        this.snapbackStartX = Mth.floor(mouseX - (double) i);
                        this.snapbackStartY = Mth.floor(mouseY - (double) j);
                        this.snapbackEnd = this.clickedSlot;
                        this.snapbackItem = this.draggingItem;
                        this.snapbackTime = Util.getMillis();
                    }

                    this.clearDraggingState();
                }
            } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);

                for (Slot slot2 : this.quickCraftSlots) {
                    this.slotClicked(slot2, slot2.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
                }

                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
            } else if (!this.menu.getCarried().isEmpty()) {
                if (this.minecraft.options.keyPickItem.matchesMouse(button)) {
                    this.slotClicked(slot, k, button, ClickType.CLONE);
                } else {
                    boolean bl2 = k != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                    if (bl2) {
                        this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                    }

                    this.slotClicked(slot, k, button, bl2 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }

        if (this.menu.getCarried().isEmpty()) {
            this.lastClickTime = 0L;
        }

        this.isQuickCrafting = false;
        return true;
    }

    @Override
    public void clearDraggingState() {
        this.draggingItem = ItemStack.EMPTY;
        this.clickedSlot = null;
    }

    private boolean isHovering(Slot slot, double mouseX, double mouseY) {
        return this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        mouseX -= i;
        mouseY -= j;
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot != null) {
            slotId = slot.index;
        }

        this.handleInventoryMouseClick(this.menu.containerId, slotId, mouseButton, type, this.minecraft.player);
    }

    public void handleInventoryMouseClick(int containerId, int slotId, int mouseButton, ClickType clickType, Player player) {
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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else if (keyCode == 258) {
            boolean bl = !hasShiftDown();
            if (!this.changeFocus(bl)) {
                this.changeFocus(bl);
            }

            return false;
        } else if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        } else {
            this.checkHotbarKeyPressed(keyCode, scanCode);
            if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
                if (this.minecraft.options.keyPickItem.matches(keyCode, scanCode)) {
                    this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
                } else if (this.minecraft.options.keyDrop.matches(keyCode, scanCode)) {
                    this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, hasControlDown() ? 1 : 0, ClickType.THROW);
                }
            }

            return true;
        }
    }

    @Override
    protected boolean checkHotbarKeyPressed(int keyCode, int scanCode) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            if (this.minecraft.options.keySwapOffhand.matches(keyCode, scanCode)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                return true;
            }

            for (int i = 0; i < 9; ++i) {
                if (this.minecraft.options.keyHotbarSlots[i].matches(keyCode, scanCode)) {
                    this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ClickType.SWAP);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
//        super.onClose();
        this.minecraft.setScreen(null);
    }
}
