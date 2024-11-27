package fuzs.netherchested.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.client.LimitlessContainerScreen;
import fuzs.netherchested.NetherChested;
import fuzs.netherchested.world.inventory.NetherChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class NetherChestScreen extends LimitlessContainerScreen<NetherChestMenu> {
    public static final ResourceLocation CONTAINER_BACKGROUND = NetherChested.id("textures/gui/container/nether_chest.png");

    public NetherChestScreen(NetherChestMenu chestMenu, Inventory inventory, Component component) {
        super(chestMenu, inventory, component);
        this.imageHeight = 227;
        this.titleLabelY = 7;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xCFCFCF, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(CONTAINER_BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
