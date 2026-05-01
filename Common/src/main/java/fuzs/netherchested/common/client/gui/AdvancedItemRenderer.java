package fuzs.netherchested.common.client.gui;

import com.google.common.collect.ImmutableSortedMap;
import fuzs.netherchested.common.services.ClientAbstractions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class AdvancedItemRenderer {
    private static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat(), decimalFormat -> {
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    });
    private static final NavigableMap<Integer, Character> NUMBER_SUFFIXES = ImmutableSortedMap.<Integer, Character>naturalOrder()
            .put(1_000, 'K')
            .put(1_000_000, 'M')
            .put(1_000_000_000, 'B')
            .build();

    public static Optional<Component> getStackSizeComponent(ItemStack stack) {
        Map.Entry<Integer, Character> entry = NUMBER_SUFFIXES.floorEntry(stack.getCount());
        return entry == null ? Optional.empty() :
                Optional.of(Component.literal(DECIMAL_FORMAT.format(stack.getCount())).withStyle(ChatFormatting.GRAY));
    }

    /**
     * @see GuiGraphicsExtractor#itemDecorations(Font, ItemStack, int, int)
     */
    public static void itemDecorations(GuiGraphicsExtractor guiGraphics, Font font, ItemStack itemStack, int x, int y, @Nullable String text) {
        if (!itemStack.isEmpty()) {
            guiGraphics.pose().pushMatrix();
            guiGraphics.itemBar(itemStack, x, y);
            itemCount(guiGraphics, font, itemStack, x, y, text);
            guiGraphics.itemCooldown(itemStack, x, y);
            guiGraphics.pose().popMatrix();
            ClientAbstractions.INSTANCE.onExtractItemDecorations(guiGraphics, font, itemStack, x, y);
        }
    }

    /**
     * @see GuiGraphicsExtractor#itemCount(Font, ItemStack, int, int, String)
     */
    private static void itemCount(GuiGraphicsExtractor guiGraphics, Font font, ItemStack itemStack, int x, int y, @Nullable String text) {
        if (itemStack.getCount() != 1 || text != null) {
            String string = shortenValue(getCountFromString(text).orElse(itemStack.getCount()));
            Style style = getStyleFromString(text);
            Component stackCount = Component.literal(string).withStyle(style);
            float scale = Math.min(1.0F, 16.0F / font.width(stackCount));
            guiGraphics.pose().scale(scale, scale);
            int posX = (int) ((x + 17) / scale - font.width(stackCount));
            int posY = (int) ((y + font.lineHeight * 2) / scale - font.lineHeight);
            guiGraphics.text(font, string, posX, posY, -1, true);
        }
    }

    private static OptionalInt getCountFromString(@Nullable String text) {
        if (text != null) {
            try {
                text = ChatFormatting.stripFormatting(text);
                return OptionalInt.of(Integer.parseInt(text));
            } catch (NumberFormatException ignored) {

            }
        }

        return OptionalInt.empty();
    }

    private static String shortenValue(int value) {
        Map.Entry<Integer, Character> entry = NUMBER_SUFFIXES.floorEntry(value);
        return entry == null ? String.valueOf(value) : String.valueOf(value / entry.getKey()) + entry.getValue();
    }

    private static Style getStyleFromString(@Nullable String text) {
        Style style = Style.EMPTY;
        if (text != null) {
            char[] charArray = text.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                char c = charArray[i];
                if (c == ChatFormatting.PREFIX_CODE) {
                    if (++i >= charArray.length) {
                        break;
                    } else {
                        c = charArray[i];
                        ChatFormatting chatFormatting = ChatFormatting.getByCode(c);
                        if (chatFormatting == ChatFormatting.RESET) {
                            style = Style.EMPTY;
                        } else if (chatFormatting != null) {
                            style = style.applyLegacyFormat(chatFormatting);
                        }
                    }
                }
            }
        }

        return style;
    }
}
