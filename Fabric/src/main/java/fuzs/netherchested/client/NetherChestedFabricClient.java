package fuzs.netherchested.client;

import fuzs.netherchested.NetherChested;
import fuzs.puzzleslib.client.core.ClientFactories;
import net.fabricmc.api.ClientModInitializer;

public class NetherChestedFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientFactories.INSTANCE.clientModConstructor(NetherChested.MOD_ID).accept(new NetherChestedClient());
    }
}
