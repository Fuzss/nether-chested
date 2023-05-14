package fuzs.netherchested.client.gui.screens.inventory;

import com.google.common.collect.ImmutableSortedMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
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
        LanguageInfo language = Minecraft.getInstance().getLanguageManager().getSelected();
        String[] code = language.getCode().split("_");
        // seems to simply return an english locale if code is invalid, so that's ok
        return new Locale(code[0], code[1]);
    }

    public static void renderGuiItemDecorations(Font fr, ItemStack stack, int xPosition, int yPosition, Style style) {
        if (!stack.isEmpty()) {
            PoseStack poseStack = new PoseStack();
            if (stack.getCount() != 1 || !style.isEmpty()) {
                Component string = Component.literal(shortenValue(stack.getCount())).withStyle(style);

                poseStack.translate(0.0, 0.0, Minecraft.getInstance().getItemRenderer().blitOffset + 200.0F);

                float scale = Math.min(1.0F, 16.0F / fr.width(string));
                poseStack.scale(scale, scale, 1.0F);

                MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                float posX = (xPosition + 17) / scale - fr.width(string);
                float posY = (yPosition + fr.lineHeight * 2) / scale - fr.lineHeight;
                fr.drawInBatch(string, posX, posY, 16777215, true, poseStack.last().pose(), bufferSource, false, 0, 15728880);
                bufferSource.endBatch();
            }

            if (stack.isBarVisible()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tesselator.getBuilder();
                int i = stack.getBarWidth();
                int j = stack.getBarColor();
                fillRect(bufferBuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                fillRect(bufferBuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            LocalPlayer localPlayer = Minecraft.getInstance().player;
            float f = localPlayer == null ? 0.0F : localPlayer.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
            if (f > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tesselator tesselator2 = Tesselator.getInstance();
                BufferBuilder bufferBuilder2 = tesselator2.getBuilder();
                fillRect(bufferBuilder2, xPosition, yPosition + Mth.floor(16.0F * (1.0F - f)), 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    private static void fillRect(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderer.vertex(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        renderer.vertex(x, y + height, 0.0).color(red, green, blue, alpha).endVertex();
        renderer.vertex(x + width, y + height, 0.0).color(red, green, blue, alpha).endVertex();
        renderer.vertex(x + width, y, 0.0).color(red, green, blue, alpha).endVertex();
        BufferUploader.drawWithShader(renderer.end());
    }
}
