package fuzs.netherchested.fabric.services;

import fuzs.netherchested.common.services.ClientAbstractions;
import net.fabricmc.fabric.api.client.rendering.v1.ExtractItemDecorationsCallback;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public final class FabricClientAbstractions implements ClientAbstractions {
    @Override
    public void onExtractItemDecorations(GuiGraphicsExtractor guiGraphics, Font font, ItemStack itemStack, int posX, int posY) {
        ExtractItemDecorationsCallback.EVENT.invoker()
                .onExtractItemDecorations(guiGraphics, font, itemStack, posX, posY);
    }
}
