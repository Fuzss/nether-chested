package fuzs.netherchested.networking;

import fuzs.netherchested.NetherChested;
import fuzs.puzzleslib.network.Message;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntFunction;

public class ServerboundContainerClickMessage implements Message<ServerboundContainerClickMessage> {
    private int containerId;
    private int stateId;
    private int slotNum;
    private int buttonNum;
    private ClickType clickType;
    private ItemStack carriedItem;
    private Int2ObjectMap<ItemStack> changedSlots;

    public ServerboundContainerClickMessage() {
        
    }

    public ServerboundContainerClickMessage(int i, int j, int k, int l, ClickType clickType, ItemStack itemStack, Int2ObjectMap<ItemStack> int2ObjectMap) {
        this.containerId = i;
        this.stateId = j;
        this.slotNum = k;
        this.buttonNum = l;
        this.clickType = clickType;
        this.carriedItem = itemStack;
        this.changedSlots = Int2ObjectMaps.unmodifiable(int2ObjectMap);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.containerId = friendlyByteBuf.readByte();
        this.stateId = friendlyByteBuf.readVarInt();
        this.slotNum = friendlyByteBuf.readShort();
        this.buttonNum = friendlyByteBuf.readByte();
        this.clickType = friendlyByteBuf.readEnum(ClickType.class);
        IntFunction<Int2ObjectOpenHashMap<ItemStack>> intFunction = FriendlyByteBuf.limitValue(Int2ObjectOpenHashMap::new, 128);
        this.changedSlots = Int2ObjectMaps.unmodifiable((Int2ObjectMap)friendlyByteBuf.readMap(intFunction, (friendlyByteBufx) -> {
            return Integer.valueOf(friendlyByteBufx.readShort());
        }, ByteBufItemUtils::readItem));
        this.carriedItem = ByteBufItemUtils.readItem(friendlyByteBuf);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeByte(this.containerId);
        buffer.writeVarInt(this.stateId);
        buffer.writeShort(this.slotNum);
        buffer.writeByte(this.buttonNum);
        buffer.writeEnum(this.clickType);
        buffer.writeMap(this.changedSlots, FriendlyByteBuf::writeShort, ByteBufItemUtils::writeItem);
        ByteBufItemUtils.writeItem(buffer, this.carriedItem);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSlotNum() {
        return this.slotNum;
    }

    public int getButtonNum() {
        return this.buttonNum;
    }

    public ItemStack getCarriedItem() {
        return this.carriedItem;
    }

    public Int2ObjectMap<ItemStack> getChangedSlots() {
        return this.changedSlots;
    }

    public ClickType getClickType() {
        return this.clickType;
    }

    public int getStateId() {
        return this.stateId;
    }

    @Override
    public MessageHandler<ServerboundContainerClickMessage> makeHandler() {
        return new MessageHandler<>() {
            
            @Override
            public void handle(ServerboundContainerClickMessage message, Player player, Object instance) {
                ((ServerPlayer) player).resetLastActionTime();
                if (player.containerMenu.containerId == message.getContainerId()) {
                    if (player.isSpectator()) {
                        player.containerMenu.sendAllDataToRemote();
                    } else if (!player.containerMenu.stillValid(player)) {
                        NetherChested.LOGGER.debug("Player {} interacted with invalid menu {}", player, player.containerMenu);
                    } else {
                        int i = message.getSlotNum();
                        if (!player.containerMenu.isValidSlotIndex(i)) {
                            NetherChested.LOGGER.debug("Player {} clicked invalid slot index: {}, available slots: {}", player.getName(), i, player.containerMenu.slots.size());
                        } else {
                            boolean bl = message.getStateId() != player.containerMenu.getStateId();
                            player.containerMenu.suppressRemoteUpdates();
                            player.containerMenu.clicked(i, message.getButtonNum(), message.getClickType(), player);

                            for (Int2ObjectMap.Entry<ItemStack> itemStackEntry : Int2ObjectMaps.fastIterable(message.getChangedSlots())) {
                                player.containerMenu.setRemoteSlotNoCopy(itemStackEntry.getIntKey(), itemStackEntry.getValue());
                            }

                            player.containerMenu.setRemoteCarried(message.getCarriedItem());
                            player.containerMenu.resumeRemoteUpdates();
                            if (bl) {
                                player.containerMenu.broadcastFullState();
                            } else {
                                player.containerMenu.broadcastChanges();
                            }

                        }
                    }
                }
            }
        };
    }
}
