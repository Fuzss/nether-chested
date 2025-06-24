package fuzs.netherchested.client;

import fuzs.netherchested.client.gui.screens.inventory.NetherChestScreen;
import fuzs.netherchested.client.renderer.blockentity.NetherChestRenderer;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.level.block.NetherChestBlock;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.client.core.v1.context.SpecialBlockModelRenderersContext;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.ItemTooltipRegistry;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;

public class NetherChestedClient implements ClientModConstructor {

    @Override
    public void onClientSetup() {
        ItemTooltipRegistry.BLOCK.registerItemTooltip(NetherChestBlock.class,
                NetherChestBlock::getDescriptionComponent);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.value(),
                NetherChestRenderer::new);
    }

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        context.registerMenuScreen(ModRegistry.NETHER_CHEST_MENU_TYPE.value(), NetherChestScreen::new);
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(NetherChestRenderer.NETHER_CHEST_MODEL_LAYER_LOCATION,
                ChestModel::createSingleBodyLayer);
    }

    @Override
    public void onRegisterSpecialBlockModelRenderers(SpecialBlockModelRenderersContext context) {
        context.registerSpecialBlockModelRenderer(ModRegistry.NETHER_CHEST_BLOCK.value(),
                new ChestSpecialRenderer.Unbaked(NetherChestRenderer.NETHER_CHEST_TEXTURE));
    }
}
