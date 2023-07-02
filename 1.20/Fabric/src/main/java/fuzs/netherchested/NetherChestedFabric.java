package fuzs.netherchested;

import fuzs.netherchested.init.ModRegistry;
import fuzs.puzzlesapi.api.limitlesscontainers.v1.LimitlessSlotStorage;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class NetherChestedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(NetherChested.MOD_ID, NetherChested::new);
        LimitlessSlotStorage.registerForBlockEntity(ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.get(), blockEntity -> blockEntity.container);
    }
}
