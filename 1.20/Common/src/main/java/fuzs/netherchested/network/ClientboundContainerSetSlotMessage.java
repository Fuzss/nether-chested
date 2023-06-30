package fuzs.netherchested.network;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetSlotMessage implements MessageV2<ClientboundContainerSetSlotMessage> {
    private ClientboundContainerSetSlotPacket packet;

    public ClientboundContainerSetSlotMessage() {

    }

    public ClientboundContainerSetSlotMessage(int i, int j, int k, ItemStack itemStack) {
        this.packet = new ClientboundContainerSetSlotPacket(i, j, k, itemStack);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.packet = new ClientboundContainerSetSlotPacket(friendlyByteBuf) {
            private final ItemStack itemStack;

            {
                this.itemStack = LimitlessByteBufUtils.readItem(friendlyByteBuf);
            }

            @Override
            public ItemStack getItem() {
                return this.itemStack;
            }
        };
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        this.packet.write(buffer);
        LimitlessByteBufUtils.writeItem(buffer, this.packet.getItem());
    }

    @Override
    public MessageHandler<ClientboundContainerSetSlotMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(ClientboundContainerSetSlotMessage message, Player player, Object instance) {
                ((LocalPlayer) player).connection.handleContainerSetSlot(message.packet);
            }
        };
    }
}
