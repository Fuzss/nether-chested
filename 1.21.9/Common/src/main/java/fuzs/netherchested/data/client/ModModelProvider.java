package fuzs.netherchested.data.client;

import fuzs.netherchested.client.renderer.blockentity.NetherChestRenderer;
import fuzs.netherchested.init.ModRegistry;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.world.level.block.Blocks;

public class ModModelProvider extends AbstractModelProvider {

    public ModModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addBlockModels(BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createChest(ModRegistry.NETHER_CHEST_BLOCK.value(),
                Blocks.NETHER_BRICKS,
                NetherChestRenderer.NETHER_CHEST_TEXTURE,
                true);
    }
}
