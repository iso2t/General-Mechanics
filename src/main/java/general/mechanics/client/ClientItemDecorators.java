package general.mechanics.client;

import general.mechanics.api.item.element.metallic.*;
import general.mechanics.api.item.tools.ToolItem;
import general.mechanics.registries.CoreElements;
import general.mechanics.registries.CoreFluids;
import general.mechanics.registries.CoreItems;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

public class ClientItemDecorators {

    private static final IItemDecorator DURABILITY_GRADIENT;
    private static final IItemDecorator ALLOY_MARKER;
    private static final IItemDecorator ACIDIC_MARKER;
    private static final IItemDecorator BASIC_MARKER;
    private static final IItemDecorator HOT_MARKER;
    private static final IItemDecorator COLD_MARKER;

    public static void registerDecorators(RegisterItemDecorationsEvent event) {
        for (var item : CoreItems.getItems()) {
            if (item.get() instanceof ToolItem toolItem) {
                event.register(toolItem, DURABILITY_GRADIENT);
            }
        }

        // Mark alloy items
        for (var item : CoreElements.getElements()) {
            if (item.get() instanceof ElementItem element && element.getElement().isAlloy()) {
                event.register(element, ALLOY_MARKER);
            } else if (item.get() instanceof ElementRawItem element && element.getParentElement().getElement().isAlloy()) {
                event.register(element, ALLOY_MARKER);
            } else if (item.get() instanceof ElementNuggetItem element && element.getParentElement().getElement().isAlloy()) {
                event.register(element, ALLOY_MARKER);
            } else if (item.get() instanceof ElementDustItem element && element.getParentElement().getElement().isAlloy()) {
                event.register(element, ALLOY_MARKER);
            } else if (item.get() instanceof ElementPlateItem element && element.getParentElement().getElement().isAlloy()) {
                event.register(element, ALLOY_MARKER);
            } else if (item.get() instanceof ElementPileItem element && element.getParentElement().getElement().isAlloy()) {
                event.register(element, ALLOY_MARKER);
            } else if (item.get() instanceof ElementRodItem element && element.getParentElement().getElement().isAlloy()) {
                event.register(element, ALLOY_MARKER);
            }
        }

        for (var fluid : CoreFluids.getFluids()) {
            var base = CoreFluids.getBaseFluid(fluid);
            if (base != null) {
                if (base.isAcidic()) event.register(fluid.block(), ACIDIC_MARKER);
                if (base.isBasic()) event.register(fluid.block(), BASIC_MARKER);
                if (base.getTemp() >= 373) event.register(fluid.block(), HOT_MARKER);
                if (base.getTemp() <= 273) event.register(fluid.block(), COLD_MARKER);
            }
        }
    }

    private static boolean drawTextAt (GuiGraphicsExtractor g, Font font, int x, int y, float posX, float posY, String text) {
        var pose = g.pose();
        pose.pushMatrix();
        pose.translate(x, y); // Move to the item position in GUI

        //RenderSystem.disableDepthTest();
        //RenderSystem.enableBlend();
        //RenderSystem.defaultBlendFunc();

        float scale = 0.5f;
        pose.scale(scale, scale);

        g.textRenderer().accept((int) ((posX) / scale), (int) ((posY) / scale), Component.literal(text));

        //RenderSystem.disableBlend();
        //RenderSystem.enableDepthTest();
        pose.popMatrix();

        return false;
    }

    static {
        DURABILITY_GRADIENT = (g, _, stack, x, y) -> {
            if (!stack.isDamageableItem()) return false;

            final int BAR_W = 13, BAR_H = 1;
            final int barX = x + 2, barY = y + 13;

            final int max = stack.getMaxDamage();
            final int used = stack.getDamageValue();
            final float pct = max > 0 ? (float) (max - used) / max : 0f;
            final int filled = Mth.clamp(Math.round(pct * BAR_W), 0, BAR_W);

            var pose = g.pose();
            pose.pushMatrix();
            pose.translate(0, 0);

            //RenderSystem.disableDepthTest();
            //RenderSystem.enableBlend();
            //RenderSystem.defaultBlendFunc();

            g.fill(barX, barY, barX + BAR_W, barY + BAR_H, 0xFF1A1A1A);

            // gradient
            for (int i = 0; i < filled; i++) {
                float t = i / (float) (BAR_W - 1);
                int rgb = Mth.hsvToRgb(t * (1f / 3f), 1f, 1f);
                g.fill(barX + i, barY, barX + i + 1, barY + BAR_H, 0xFF000000 | rgb);
            }

            if (filled < BAR_W) {
                g.fill(barX + filled, barY, barX + BAR_W, barY + BAR_H, 0xFF2B2B2B);
            }

            //RenderSystem.disableBlend();
            //RenderSystem.enableDepthTest();
            pose.popMatrix();

            return false;
        };

        ALLOY_MARKER = (g, font, stack, x, y) -> drawTextAt(g, font, x, y, 1f, 11f, "A");

        ACIDIC_MARKER = (g, font, stack, x, y) -> drawTextAt(g, font, x, y, 1f, 11f, "Ac");
        BASIC_MARKER = (g, font, stack, x, y) -> drawTextAt(g, font, x, y, 1f, 11f, "B");
        HOT_MARKER = (g, font, stack, x, y) -> drawTextAt(g, font, x, y, 1f, 1f, "§cH");
        COLD_MARKER = (g, font, stack, x, y) -> drawTextAt(g, font, x, y, 1f, 1f, "§9C");
    }

}
