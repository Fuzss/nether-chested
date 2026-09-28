package fuzs.netherchested.neoforge;

import fuzs.netherchested.common.NetherChested;
import fuzs.netherchested.common.data.ModBlockLootProvider;
import fuzs.netherchested.common.data.ModBlockTagProvider;
import fuzs.netherchested.common.data.ModRecipeProvider;
import fuzs.netherchested.common.init.ModRegistry;
import fuzs.netherchested.common.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.fml.common.Mod;

@Mod(NetherChested.MOD_ID)
public class NetherChestedNeoForge {

    public NetherChestedNeoForge() {
        ModConstructor.construct(NetherChested.MOD_ID, NetherChested::new);
        LimitlessSlotResourceHandler.registerLimitlessBlockEntityContainer(NetherChestBlockEntity::getContainer,
                ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE);
        DataProviderBuilder.of(NetherChested.MOD_ID)
                .addLootProvider(ModBlockLootProvider::new, LootContextParamSets.BLOCK)
                .addProvider(ModBlockTagProvider::new)
                .addRecipeProvider(ModRecipeProvider::new);
    }
}
