package fuzs.netherchested.network.client;

import fuzs.netherchested.network.LimitlessByteBufUtils;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntFunction;

public class ServerboundContainerClickMessage implements MessageV2<ServerboundContainerClickMessage> {
    private ServerboundContainerClickPacket packet;

    public ServerboundContainerClickMessage() {
        
    }

    public ServerboundContainerClickMessage(int i, int j, int k, int l, ClickType clickType, ItemStack itemStack, Int2ObjectMap<ItemStack> int2ObjectMap) {
        this.packet = new ServerboundContainerClickPacket(i, j, k, l, clickType, itemStack, int2ObjectMap);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.packet = new ServerboundContainerClickPacket(friendlyByteBuf) {
            private final ItemStack carriedItem;
            private final Int2ObjectMap<ItemStack> changedSlots;

            {
                IntFunction<Int2ObjectOpenHashMap<ItemStack>> intFunction = FriendlyByteBuf.limitValue(Int2ObjectOpenHashMap::new, 128);
                this.changedSlots = Int2ObjectMaps.unmodifiable(friendlyByteBuf.readMap(intFunction, (friendlyByteBufx) -> {
                    return Integer.valueOf(friendlyByteBufx.readShort());
                }, LimitlessByteBufUtils::readItem));
                this.carriedItem = LimitlessByteBufUtils.readItem(friendlyByteBuf);
            }

            @Override
            public ItemStack getCarriedItem() {
                return this.carriedItem;
            }

            @Override
            public Int2ObjectMap<ItemStack> getChangedSlots() {
                return this.changedSlots;
            }
        };
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        this.packet.write(buffer);
        buffer.writeMap(this.packet.getChangedSlots(), FriendlyByteBuf::writeShort, LimitlessByteBufUtils::writeItem);
        LimitlessByteBufUtils.writeItem(buffer, this.packet.getCarriedItem());
    }

    @Override
    public MessageHandler<ServerboundContainerClickMessage> makeHandler() {
        return new MessageHandler<>() {
            
            @Override
            public void handle(ServerboundContainerClickMessage message, Player player, Object instance) {
                ((ServerPlayer) player).connection.handleContainerClick(message.packet);
            }
        };
    }
}
