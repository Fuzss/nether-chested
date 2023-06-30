package fuzs.netherchested.network;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientboundContainerSetContentMessage implements MessageV2<ClientboundContainerSetContentMessage> {
    private ClientboundContainerSetContentPacket packet;

    public ClientboundContainerSetContentMessage() {

    }

    public ClientboundContainerSetContentMessage(int i, int j, NonNullList<ItemStack> nonNullList, ItemStack itemStack) {
        this.packet = new ClientboundContainerSetContentPacket(i, j, nonNullList, itemStack);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.packet = new ClientboundContainerSetContentPacket(friendlyByteBuf) {
            private final NonNullList<ItemStack> items;
            private final ItemStack carriedItem;

            {
                this.items = friendlyByteBuf.readCollection(NonNullList::createWithCapacity, LimitlessByteBufUtils::readItem);
                this.carriedItem = LimitlessByteBufUtils.readItem(friendlyByteBuf);
            }

            @Override
            public List<ItemStack> getItems() {
                return this.items;
            }

            @Override
            public ItemStack getCarriedItem() {
                return this.carriedItem;
            }
        };
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        this.packet.write(buffer);
        buffer.writeCollection(this.packet.getItems(), LimitlessByteBufUtils::writeItem);
        LimitlessByteBufUtils.writeItem(buffer, this.packet.getCarriedItem());
    }

    @Override
    public MessageHandler<ClientboundContainerSetContentMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(ClientboundContainerSetContentMessage message, Player player, Object instance) {
                ((LocalPlayer) player).connection.handleContainerContent(message.packet);
            }
        };
    }
}
