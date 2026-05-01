package fuzs.netherchested.neoforge.client;

import fuzs.netherchested.common.NetherChested;
import fuzs.netherchested.common.client.NetherChestedClient;
import fuzs.netherchested.common.data.client.ModLanguageProvider;
import fuzs.netherchested.common.data.client.ModModelProvider;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = NetherChested.MOD_ID, dist = Dist.CLIENT)
public class NetherChestedNeoForgeClient {

    public NetherChestedNeoForgeClient() {
        ClientModConstructor.construct(NetherChested.MOD_ID, NetherChestedClient::new);
        DataProviderHelper.registerDataProviders(NetherChested.MOD_ID, ModLanguageProvider::new, ModModelProvider::new);
    }
}
