package fuzs.netherchested.fabric.client;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.client.NetherChestedClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class NetherChestedFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(NetherChested.MOD_ID, NetherChestedClient::new);
    }
}
