package fuzs.netherchested.fabric;

import fuzs.netherchested.common.NetherChested;
import fuzs.netherchested.common.init.ModRegistry;
import fuzs.netherchested.common.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class NetherChestedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(NetherChested.MOD_ID, NetherChested::new);
        LimitlessSlotStorage.registerForBlockEntity(NetherChestBlockEntity::getContainer,
                ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE);
    }
}
