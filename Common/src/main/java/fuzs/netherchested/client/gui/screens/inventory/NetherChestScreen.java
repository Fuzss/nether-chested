package fuzs.netherchested.client.gui.screens.inventory;

import fuzs.limitlesscontainers.common.api.limitlesscontainers.v1.client.LimitlessContainerScreen;
import fuzs.netherchested.NetherChested;
import fuzs.netherchested.world.inventory.NetherChestMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class NetherChestScreen extends LimitlessContainerScreen<NetherChestMenu> {
    public static final Identifier CONTAINER_BACKGROUND = NetherChested.id("textures/gui/container/nether_chest.png");

    public NetherChestScreen(NetherChestMenu chestMenu, Inventory inventory, Component component) {
        super(chestMenu, inventory, component, DEFAULT_IMAGE_WIDTH, 227);
        this.titleLabelY = 7;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xCFCFCF, false);
        guiGraphics.text(this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY,
                0x404040,
                false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                CONTAINER_BACKGROUND,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                256,
                256);
    }
}
