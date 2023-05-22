package fuzs.netherchested;

import fuzs.netherchested.config.ServerConfig;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.network.ClientboundContainerSetContentMessage;
import fuzs.netherchested.network.ClientboundContainerSetSlotMessage;
import fuzs.netherchested.network.client.ServerboundContainerClickMessage;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.network.v2.MessageDirection;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetherChested implements ModConstructor {
    public static final String MOD_ID = "netherchested";
    public static final String MOD_NAME = "Nether Chested";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final NetworkHandlerV2 NETWORK = NetworkHandlerV2.build(MOD_ID);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
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
