package fuzs.netherchested;

import fuzs.netherchested.data.ModBlockTagsProvider;
import fuzs.netherchested.data.ModLanguageProvider;
import fuzs.netherchested.data.ModBlockLootProvider;
import fuzs.netherchested.data.ModRecipeProvider;
import fuzs.netherchested.init.ForgeModRegistry;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import java.util.concurrent.CompletableFuture;

@Mod(NetherChested.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetherChestedForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(NetherChested.MOD_ID, NetherChested::new);
        ForgeModRegistry.touch();
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        final DataGenerator dataGenerator = evt.getGenerator();
        final PackOutput packOutput = dataGenerator.getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = evt.getLookupProvider();
        final ExistingFileHelper fileHelper = evt.getExistingFileHelper();
        dataGenerator.addProvider(true, new ModBlockTagsProvider(packOutput, lookupProvider, NetherChested.MOD_ID, fileHelper));
        dataGenerator.addProvider(true, new ModLanguageProvider(packOutput, NetherChested.MOD_ID));
        dataGenerator.addProvider(true, new ModBlockLootProvider(packOutput, NetherChested.MOD_ID));
        dataGenerator.addProvider(true, new ModRecipeProvider(packOutput));
    }
}
