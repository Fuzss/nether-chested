package fuzs.netherchested;

import fuzs.netherchested.data.ModBlockTagsProvider;
import fuzs.netherchested.data.ModLanguageProvider;
import fuzs.netherchested.data.ModLootTableProvider;
import fuzs.netherchested.data.ModRecipeProvider;
import fuzs.puzzleslib.core.CommonFactories;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryManager;

import java.util.List;

@Mod(NetherChested.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetherChestedForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        CommonFactories.INSTANCE.modConstructor(NetherChested.MOD_ID).accept(new NetherChested());
        registerHandlers();
    }

    @SuppressWarnings("unchecked")
    private static <T> void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final MissingMappingsEvent evt) -> {
            List<MissingMappingsEvent.Mapping<T>> mappings = evt.getMappings((ResourceKey<? extends Registry<T>>) evt.getKey(), "netherchest");
            for (MissingMappingsEvent.Mapping<T> mapping : mappings) {
                // don't use the registry provided by the event, it's not the one that is active
                // honestly this is kinda pointless though as this event doesn't fire when loading block entities, so their contents will be lost
                // only really useful for migrating items stored in some container
                ResourceKey<Registry<T>> registryKey = mapping.getRegistry().getRegistryKey();
                ForgeRegistry<T> activeRegistry = RegistryManager.ACTIVE.getRegistry(registryKey);
                ResourceLocation id = NetherChested.id(mapping.getKey().getPath());
                mapping.remap(activeRegistry.getValue(id));
            }
        });
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        DataGenerator dataGenerator = evt.getGenerator();
        final ExistingFileHelper fileHelper = evt.getExistingFileHelper();
        dataGenerator.addProvider(true, new ModBlockTagsProvider(dataGenerator, NetherChested.MOD_ID, fileHelper));
        dataGenerator.addProvider(true, new ModLanguageProvider(dataGenerator, NetherChested.MOD_ID));
        dataGenerator.addProvider(true, new ModLootTableProvider(dataGenerator, NetherChested.MOD_ID));
        dataGenerator.addProvider(true, new ModRecipeProvider(dataGenerator));
    }
}
