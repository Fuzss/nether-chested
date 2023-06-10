package fuzs.netherchested.client.gui.screens.inventory;

import com.google.common.collect.ImmutableSortedMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class AdvancedItemRenderer {
    private static final NavigableMap<Integer, Character> MAP = ImmutableSortedMap.<Integer, Character>naturalOrder().put(1_000, 'K').put(1_000_000, 'M').put(1_000_000_000, 'B').build();

    private static String shortenValue(int value) {
        Map.Entry<Integer, Character> entry = MAP.floorEntry(value);
        if (entry == null) {
            return String.valueOf(value);
        }
        return String.valueOf(value / entry.getKey()) + entry.getValue();
    }

    public static Optional<Component> getStackSizeComponent(ItemStack stack) {
        Map.Entry<Integer, Character> entry = MAP.floorEntry(stack.getCount());
        if (entry == null) return Optional.empty();
        return Optional.of(Component.literal(formatStackSize(stack.getCount())).withStyle(ChatFormatting.GRAY));
    }

    private static String formatStackSize(int stackSize) {
        String value = String.format(getCurrentLocale(), "%,d", stackSize);
        // fix a weird issue where a bunch of locales use an odd char Minecraft cannot render as separator
        if (!Pattern.compile("\\p{Punct}").matcher(value).find()) {
            value = value.replaceAll("\\D", ",");
        }
        return value;
    }

    private static Locale getCurrentLocale() {
        // just an idea to format the number according to the system's locale
        // not sure if Minecraft already uses a more proper way for this somewhere
        String currentCode = Minecraft.getInstance().getLanguageManager().getSelected();
        String[] code = currentCode.split("_");
        // seems to simply return an english locale if code is invalid, so that's ok
        return new Locale(code[0], code[1]);
    }

    /**
     * Pretty much copied from {@link GuiGraphics#renderItemDecorations(Font, ItemStack, int, int, String)}.
     */
    public static void renderItemDecorations(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int i, int j, @Nullable String string) {
        if (!itemStack.isEmpty()) {
            guiGraphics.pose().pushPose();
            if (itemStack.getCount() != 1 || string != null) {
                String string2 = shortenValue(getCountFromString(string).orElse(itemStack.getCount()));
                Style style = getStyleFromString(string);
                Component stackCount = Component.literal(string2).withStyle(style);
                guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
                float scale = Math.min(1.0F, 16.0F / font.width(stackCount));
                guiGraphics.pose().scale(scale, scale, 1.0F);
                int posX = (int) ((i + 17) / scale - font.width(stackCount));
                int posY = (int) ((j + font.lineHeight * 2) / scale - font.lineHeight);
                guiGraphics.drawString(font, string2, posX, posY, 16777215, true);
            }

            int m;
            int n;
            if (itemStack.isBarVisible()) {
                int k = itemStack.getBarWidth();
                int l = itemStack.getBarColor();
                m = i + 2;
                n = j + 13;
                guiGraphics.fill(RenderType.guiOverlay(), m, n, m + 13, n + 2, -16777216);
                guiGraphics.fill(RenderType.guiOverlay(), m, n, m + k, n + 1, l | -16777216);
            }

            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localPlayer = minecraft.player;
            float f = localPlayer == null ? 0.0F : localPlayer.getCooldowns().getCooldownPercent(itemStack.getItem(), minecraft.getFrameTime());
            if (f > 0.0F) {
                m = j + Mth.floor(16.0F * (1.0F - f));
                n = m + Mth.ceil(16.0F * f);
                guiGraphics.fill(RenderType.guiOverlay(), i, m, i + 16, n, Integer.MAX_VALUE);
            }

            guiGraphics.pose().popPose();
        }
    }

    private static OptionalInt getCountFromString(@Nullable String text) {
        if (text != null) {
            try {
                text = ChatFormatting.stripFormatting(text);
                if (text != null) {
                    return OptionalInt.of(Integer.parseInt(text));
                }
            } catch (NumberFormatException ignored) {

            }
        }
        return OptionalInt.empty();
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
