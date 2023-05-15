package fuzs.netherchested;

import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.inventory.UnlimitedSlotStorage;
import fuzs.puzzleslib.core.CommonFactories;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public class NetherChestedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonFactories.INSTANCE.modConstructor(NetherChested.MOD_ID).accept(new NetherChested());
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            return UnlimitedSlotStorage.of(blockEntity.container, direction);
        }, ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.get());
    }
}
