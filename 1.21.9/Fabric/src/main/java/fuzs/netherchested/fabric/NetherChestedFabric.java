package fuzs.netherchested.fabric;

import fuzs.limitlesscontainers.fabric.api.limitlesscontainers.v1.LimitlessSlotStorage;
import fuzs.netherchested.NetherChested;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class NetherChestedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(NetherChested.MOD_ID, NetherChested::new);
        LimitlessSlotStorage.registerForBlockEntity(NetherChestBlockEntity::getContainer,
                ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE);
    }
}
