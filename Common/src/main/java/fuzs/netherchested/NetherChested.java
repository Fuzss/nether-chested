package fuzs.netherchested;

import fuzs.netherchested.config.ServerConfig;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.networking.ClientboundContainerSetContentMessage;
import fuzs.netherchested.networking.ClientboundContainerSetSlotMessage;
import fuzs.netherchested.networking.ServerboundContainerClickMessage;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.network.MessageDirection;
import fuzs.puzzleslib.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetherChested implements ModConstructor {
    public static final String MOD_ID = "netherchested";
    public static final String MOD_NAME = "Nether Chested";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = CommonFactories.INSTANCE.network(MOD_ID);
    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder CONFIG = CommonFactories.INSTANCE.serverConfig(ServerConfig.class, () -> new ServerConfig());

    @Override
    public void onConstructMod() {
        CONFIG.bakeConfigs(MOD_ID);
        ModRegistry.touch();
        registerMessages();
    }

    private static void registerMessages() {
        NETWORK.register(ServerboundContainerClickMessage.class, ServerboundContainerClickMessage::new, MessageDirection.TO_SERVER);
        NETWORK.register(ClientboundContainerSetSlotMessage.class, ClientboundContainerSetSlotMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(ClientboundContainerSetContentMessage.class, ClientboundContainerSetContentMessage::new, MessageDirection.TO_CLIENT);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
