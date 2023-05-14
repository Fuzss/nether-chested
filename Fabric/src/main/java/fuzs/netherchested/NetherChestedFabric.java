package fuzs.netherchested;

import fuzs.puzzleslib.core.CommonFactories;
import net.fabricmc.api.ModInitializer;

public class NetherChestedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonFactories.INSTANCE.modConstructor(NetherChested.MOD_ID).accept(new NetherChested());
    }
}
