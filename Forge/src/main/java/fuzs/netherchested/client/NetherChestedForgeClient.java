package fuzs.netherchested.client;

import fuzs.netherchested.NetherChested;
import fuzs.puzzleslib.client.core.ClientFactories;
import fuzs.puzzleslib.core.ContentRegistrationFlags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = NetherChested.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NetherChestedForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientFactories.INSTANCE.clientModConstructor(NetherChested.MOD_ID, ContentRegistrationFlags.BUILT_IN_ITEM_MODEL_RENDERERS).accept(new NetherChestedClient());
    }
}
