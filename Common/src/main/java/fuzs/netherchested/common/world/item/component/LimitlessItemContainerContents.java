package fuzs.netherchested.common.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.netherchested.common.NetherChested;
import fuzs.netherchested.common.world.inventory.LimitlessContainerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An extension to {@link ItemContainerContents} replacing all usages of {@link ItemStackTemplate#create()} w
 */
public final class LimitlessItemContainerContents extends ItemContainerContents {
    /**
     * @see ItemContainerContents.Slot#CODEC
     */
    public static final Codec<ItemContainerContents.Slot> SLOT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 255).fieldOf("slot").forGetter(ItemContainerContents.Slot::index),
            LimitlessContainerUtils.ITEM_STACK_TEMPLATE_CODEC.fieldOf("item")
                    .forGetter(ItemContainerContents.Slot::item)).apply(instance, ItemContainerContents.Slot::new));
    /**
     * @see ItemContainerContents#CODEC
     */
    public static final Codec<LimitlessItemContainerContents> CODEC = SLOT_CODEC.sizeLimitedListOf(256)
            .xmap(LimitlessItemContainerContents::fromSlots, ItemContainerContents::asSlots);
    public static final StreamCodec<RegistryFriendlyByteBuf, LimitlessItemContainerContents> STREAM_CODEC = ItemContainerContents.STREAM_CODEC.map(
            (ItemContainerContents contents) -> {
                return new LimitlessItemContainerContents(contents.items);
            },
            Function.identity());

    private LimitlessItemContainerContents(List<Optional<ItemStackTemplate>> items) {
        super(items);
    }

    public static LimitlessItemContainerContents fromItems(List<ItemStack> itemStacks) {
        return new LimitlessItemContainerContents(ItemContainerContents.fromItems(itemStacks).items);
    }

    public static LimitlessItemContainerContents fromSlots(List<ItemContainerContents.Slot> slots) {
        return new LimitlessItemContainerContents(ItemContainerContents.fromSlots(slots).items);
    }

    /**
     * @see ItemStackTemplate#create()
     */
    private ItemStack createFromTemplate(ItemStackTemplate template) {
        return this.validateFromTemplate(new ItemStack(template.item(), template.count(), template.components()));
    }

    /**
     * @see ItemStackTemplate#validate(ItemStack)
     */
    private ItemStack validateFromTemplate(ItemStack result) {
        Optional<DataResult.Error<ItemStack>> error = LimitlessContainerUtils.validateStrict(result).error();
        if (error.isPresent()) {
            NetherChested.LOGGER.warn("Can't create item stack with properties {}, error: {}",
                    this,
                    error.get().message());
            return ItemStack.EMPTY;
        } else {
            return result;
        }
    }

    @Override
    public ItemStack createStackFromSlot(int slot) {
        if (slot < this.items.size()) {
            Optional<ItemStackTemplate> slotContents = this.items.get(slot);
            if (slotContents.isPresent()) {
                return this.createFromTemplate(slotContents.get());
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public Stream<ItemStack> allItemsCopyStream() {
        return this.items.stream().map((Optional<ItemStackTemplate> optional) -> {
            return optional.map(this::createFromTemplate).orElse(ItemStack.EMPTY);
        });
    }

    @Override
    public Stream<ItemStack> nonEmptyItemCopyStream() {
        return this.nonEmptyItemsStream().map(this::createFromTemplate);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        int lineCount = 0;
        int itemCount = 0;

        for (Optional<ItemStackTemplate> item : this.items) {
            if (!item.isEmpty()) {
                itemCount++;
                if (lineCount <= 4) {
                    lineCount++;
                    ItemStack itemStack = this.createFromTemplate(item.get());
                    consumer.accept(Component.translatable("item.container.item_count",
                            itemStack.getHoverName(),
                            itemStack.getCount()));
                }
            }
        }

        if (itemCount - lineCount > 0) {
            consumer.accept(Component.translatable("item.container.more_items", itemCount - lineCount)
                    .withStyle(ChatFormatting.ITALIC));
        }
    }
}
