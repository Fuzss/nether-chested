package fuzs.netherchested.client.gui.screens.inventory;

import com.google.common.collect.ImmutableSortedMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
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
     * Pretty much copied from {@link net.minecraft.client.renderer.entity.ItemRenderer#renderGuiItemDecorations(PoseStack, Font, ItemStack, int, int, String)}
     */
    public static void renderGuiItemDecorations(PoseStack poseStack, Font fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            if (stack.getCount() != 1 || text != null) {

                String value = shortenValue(getCountFromString(text).orElse(stack.getCount()));
                Style style = getStyleFromString(text);
                Component stackCount = Component.literal(value).withStyle(style);

                poseStack.translate(0.0, 0.0, 200.0F);

                float scale = Math.min(1.0F, 16.0F / fr.width(stackCount));
                poseStack.scale(scale, scale, 1.0F);

                MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                float posX = (xPosition + 17) / scale - fr.width(stackCount);
                float posY = (yPosition + fr.lineHeight * 2) / scale - fr.lineHeight;
                fr.drawInBatch(stackCount, posX, posY, 16777215, true, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                bufferSource.endBatch();
            }

            if (stack.isBarVisible()) {
                RenderSystem.disableDepthTest();
                int k = stack.getBarWidth();
                int j = stack.getBarColor();
                int m = xPosition + 2;
                int n = yPosition + 13;
                GuiComponent.fill(poseStack, m, n, m + 13, n + 2, -16777216);
                GuiComponent.fill(poseStack, m, n, m + k, n + 1, j | -16777216);
                RenderSystem.enableDepthTest();
            }

            LocalPlayer localPlayer = Minecraft.getInstance().player;
            float f = localPlayer == null ? 0.0F : localPlayer.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
            if (f > 0.0F) {
                RenderSystem.disableDepthTest();
                int m = yPosition + Mth.floor(16.0F * (1.0F - f));
                int n = m + Mth.ceil(16.0F * f);
                GuiComponent.fill(poseStack, xPosition, m, xPosition + 16, n, Integer.MAX_VALUE);
                RenderSystem.enableDepthTest();
            }

            poseStack.popPose();
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
