package fr.idarkay.morefeatures.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.idarkay.morefeatures.FeaturesClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * File <b>InGameHudMixin</b> located on fr.idarkay.morefeature.mixin
 * InGameHudMixin is a part of featurs-mod.
 * <p>
 * Copyright (c) 2020 features-mod.
 * <p>
 *
 * @author Alois. B. (IDarKay),
 * Created the 28/07/2020 at 19:54
 */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int scaledWidth;

//    @Shadow private int scaledHeight;
//
//    @Shadow public abstract TextRenderer getFontRenderer();
//
//    @Shadow protected abstract void drawTextBackground(MatrixStack matrixStack, TextRenderer textRenderer, int i, int j, int k);

    @Shadow
    @Final
    private static Identifier PUMPKIN_BLUR;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    private void renderOverlay(Identifier texture, float opacity, CallbackInfo ci) {
        if (FeaturesClient.options().hidePumpkin && texture == PUMPKIN_BLUR) {
            ci.cancel();
        }
    }

//    @ModifyVariable(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
//    private Runnable modifyListRunnable(Runnable prev)
//    {
//        this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, finalF);
//        drawSprite(matrixStack, finalK + 3, finalL + 3, this.getZOffset(), 18, 18, sprite);
//
//        String time = StatusEffectUtil.durationToString(statusEffectInstance, 1.0F);
//        textRenderer.drawWithShadow(matrixStack, time, finalK, finalL + 25, 8355711);
//    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderStatusEffectOverlay(MatrixStack matrixStack, CallbackInfo ci) {
        if (!FeaturesClient.options().effectTime) return;
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (!collection.isEmpty()) {
            TextRenderer textRenderer = this.getTextRenderer();
            RenderSystem.enableBlend();
            int i = 0;
            int j = 0;
            StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
            RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
            Iterator var7 = Ordering.natural().reverse().sortedCopy(collection).iterator();

            while (var7.hasNext()) {
                StatusEffectInstance statusEffectInstance = (StatusEffectInstance) var7.next();
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                if (statusEffectInstance.shouldShowIcon()) {
                    int k = this.scaledWidth;
                    int l = 1;
                    if (this.client.isDemo()) {
                        l += 15;
                    }

                    if (statusEffect.isBeneficial()) {
                        ++i;
                        k -= 27 * i;
                    } else {
                        ++j;
                        k -= 27 * j;
                        l += 36;
                    }

                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    float f = 1.0F;
                    if (statusEffectInstance.isAmbient()) {
                        this.drawTexture(matrixStack, k, l, 165, 166, 24, 24);
                    } else {
                        this.drawTexture(matrixStack, k, l, 141, 166, 24, 24);
                        if (statusEffectInstance.getDuration() <= 200) {
                            int m = 10 - statusEffectInstance.getDuration() / 20;
                            f = MathHelper.clamp(
                                    (float) statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
                                    + MathHelper.cos((float) statusEffectInstance.getDuration() * 3.1415927F / 5.0F)
                                    * MathHelper.clamp((float) m / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }

                    Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);

                    final float finalF = f;
                    final int finalL = l;
                    final int finalK = k;
                    list.add(() -> {
                        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, finalF);
                        drawSprite(matrixStack, finalK + 3, finalL + 3, this.getZOffset(), 18, 18, sprite);

                        String time = StatusEffectUtil.durationToString(statusEffectInstance, 1.0F);
                        textRenderer.drawWithShadow(matrixStack, time, finalK, finalL + 25, 8355711);
                    });
                }
            }

            list.forEach(Runnable::run);
        }
        ci.cancel();
    }

}
