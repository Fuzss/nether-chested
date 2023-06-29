package fuzs.netherchested.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class LimitlessByteBufUtils {

    public static ItemStack readItem(FriendlyByteBuf friendlyByteBuf) {
        ItemStack stack = friendlyByteBuf.readItem();
        stack.setCount(friendlyByteBuf.readInt());
        return stack;
    }

    public static void writeItem(FriendlyByteBuf friendlyByteBuf, ItemStack stack) {
        friendlyByteBuf.writeItem(stack);
        friendlyByteBuf.writeInt(stack.getCount());
    }
}
