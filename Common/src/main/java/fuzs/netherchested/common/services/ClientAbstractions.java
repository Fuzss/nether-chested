package fuzs.netherchested.common.services;

import fuzs.puzzleslib.common.api.core.v1.ServiceProviderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public interface ClientAbstractions {
    ClientAbstractions INSTANCE = ServiceProviderHelper.load(ClientAbstractions.class);

    void onExtractItemDecorations(GuiGraphicsExtractor guiGraphics, Font font, ItemStack itemStack, int posX, int posY);
}
