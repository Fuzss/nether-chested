package fuzs.netherchested.forge;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.forge.init.ForgeModRegistry;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(NetherChested.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetherChestedForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ForgeModRegistry.touch();
        ModConstructor.construct(NetherChested.MOD_ID, NetherChested::new);
    }
}
