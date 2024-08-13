package fuzs.netherchested.neoforge;

import fuzs.limitlesscontainers.neoforge.api.limitlesscontainers.v1.LimitlessInvWrapper;
import fuzs.netherchested.NetherChested;
import fuzs.netherchested.data.ModBlockLootProvider;
import fuzs.netherchested.data.ModBlockTagProvider;
import fuzs.netherchested.data.ModRecipeProvider;
import fuzs.netherchested.data.client.ModLanguageProvider;
import fuzs.netherchested.data.client.ModModelProvider;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(NetherChested.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetherChestedNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(NetherChested.MOD_ID, NetherChested::new);
        DataProviderHelper.registerDataProviders(NetherChested.MOD_ID,
                ModBlockTagProvider::new,
                ModBlockLootProvider::new,
                ModRecipeProvider::new,
                ModLanguageProvider::new,
                ModModelProvider::new
        );
        LimitlessInvWrapper.registerLimitlessBlockEntityContainer(NetherChestBlockEntity::getContainer,
                ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE
        );
    }
}
