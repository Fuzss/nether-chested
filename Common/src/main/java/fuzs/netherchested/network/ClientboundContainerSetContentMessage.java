package fuzs.netherchested.network;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientboundContainerSetContentMessage implements MessageV2<ClientboundContainerSetContentMessage> {
    private int containerId;
    private int stateId;
    private List<ItemStack> items;
    private ItemStack carriedItem;

    public ClientboundContainerSetContentMessage() {

    }

    public ClientboundContainerSetContentMessage(int i, int j, NonNullList<ItemStack> nonNullList, ItemStack itemStack) {
        this.containerId = i;
        this.stateId = j;
        this.items = NonNullList.withSize(nonNullList.size(), ItemStack.EMPTY);

        for (int k = 0; k < nonNullList.size(); ++k) {
            this.items.set(k, nonNullList.get(k).copy());
        }

        this.carriedItem = itemStack.copy();
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.containerId = friendlyByteBuf.readUnsignedByte();
        this.stateId = friendlyByteBuf.readVarInt();
        this.items = friendlyByteBuf.readCollection(NonNullList::createWithCapacity, ByteBufItemUtils::readItem);
        this.carriedItem = ByteBufItemUtils.readItem(friendlyByteBuf);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeByte(this.containerId);
        buffer.writeVarInt(this.stateId);
        buffer.writeCollection(this.items, ByteBufItemUtils::writeItem);
        ByteBufItemUtils.writeItem(buffer, this.carriedItem);
    }

    @Override
    public MessageHandler<ClientboundContainerSetContentMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(ClientboundContainerSetContentMessage message, Player player, Object instance) {
                if (message.getContainerId() == 0) {
                    player.inventoryMenu.initializeContents(message.getStateId(), message.getItems(), message.getCarriedItem());
                } else if (message.getContainerId() == player.containerMenu.containerId) {
                    player.containerMenu.initializeContents(message.getStateId(), message.getItems(), message.getCarriedItem());
                }
            }
        };
    }

    public int getContainerId() {
        return this.containerId;
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public ItemStack getCarriedItem() {
        return this.carriedItem;
    }

    public int getStateId() {
        return this.stateId;
    }
}
