package fuzs.netherchested.common.client;

import fuzs.netherchested.common.client.gui.screens.inventory.NetherChestScreen;
import fuzs.netherchested.common.client.renderer.blockentity.NetherChestRenderer;
import fuzs.netherchested.common.init.ModRegistry;
import fuzs.netherchested.common.world.level.block.NetherChestBlock;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.common.api.client.core.v1.context.BuiltInBlockModelsContext;
import fuzs.puzzleslib.common.api.client.core.v1.context.LayerDefinitionsContext;
import fuzs.puzzleslib.common.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.common.api.client.gui.v2.tooltip.ItemTooltipRegistry;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.model.ConditionalBlockModel;
import net.minecraft.client.renderer.block.model.properties.conditional.IsXmas;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.Optional;

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
    public void onRegisterBuiltInBlockModels(BuiltInBlockModelsContext context) {
        context.registerModelFactory(ModRegistry.NETHER_CHEST_BLOCK.value(),
                createXmasChest(NetherChestRenderer.NETHER_CHEST_TEXTURE));
    }

    /**
     * TODO replace with Puzzles Lib method
     */
    @Deprecated
    public static BuiltInBlockModels.SpecialModelFactory createXmasChest(Identifier texture) {
        return BuiltInBlockModels.specialModelWithPropertyDispatch(ChestBlock.FACING,
                (Direction facing) -> new ConditionalBlockModel.Unbaked(Optional.empty(),
                        new IsXmas(),
                        BuiltInBlockModels.createChest(ChestSpecialRenderer.CHRISTMAS.single(),
                                ChestType.SINGLE,
                                facing),
                        BuiltInBlockModels.createChest(texture, ChestType.SINGLE, facing)));
    }
}
