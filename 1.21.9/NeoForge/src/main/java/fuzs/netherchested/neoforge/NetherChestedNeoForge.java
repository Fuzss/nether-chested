package fuzs.netherchested.neoforge;

import fuzs.limitlesscontainers.neoforge.api.limitlesscontainers.v2.LimitlessSlotResourceHandler;
import fuzs.netherchested.NetherChested;
import fuzs.netherchested.data.ModBlockLootProvider;
import fuzs.netherchested.data.ModBlockTagProvider;
import fuzs.netherchested.data.ModRecipeProvider;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(NetherChested.MOD_ID)
public class NetherChestedNeoForge {

    public NetherChestedNeoForge() {
        ModConstructor.construct(NetherChested.MOD_ID, NetherChested::new);
        LimitlessSlotResourceHandler.registerLimitlessBlockEntityContainer(NetherChestBlockEntity::getContainer,
                ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE);
        DataProviderHelper.registerDataProviders(NetherChested.MOD_ID,
                ModBlockTagProvider::new,
                ModBlockLootProvider::new,
                ModRecipeProvider::new);
    }
}
