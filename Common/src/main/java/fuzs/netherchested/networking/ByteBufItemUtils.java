package fuzs.netherchested.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ByteBufItemUtils {

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
