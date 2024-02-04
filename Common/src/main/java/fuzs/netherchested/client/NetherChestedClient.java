package fuzs.netherchested.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.netherchested.client.gui.screens.inventory.NetherChestScreen;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class NetherChestedClient implements ClientModConstructor {

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.value(), ChestRenderer::new);
    }

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        context.registerMenuScreen(ModRegistry.NETHER_CHEST_MENU_TYPE.value(), NetherChestScreen::new);
    }

    @Override
    public void onRegisterBuiltinModelItemRenderers(BuiltinModelItemRendererContext context) {
        NetherChestBlockEntity netherChest = new NetherChestBlockEntity(BlockPos.ZERO,
                ModRegistry.NETHER_CHEST_BLOCK.value().defaultBlockState()
        );
        context.registerItemRenderer((ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) -> {
            Minecraft.getInstance()
                    .getBlockEntityRenderDispatcher()
                    .renderItem(netherChest, matrices, vertexConsumers, light, overlay);
        }, ModRegistry.NETHER_CHEST_BLOCK.value());
    }
}
