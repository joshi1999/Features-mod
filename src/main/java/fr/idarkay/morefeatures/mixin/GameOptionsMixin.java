package fr.idarkay.morefeatures.mixin;

import fr.idarkay.morefeatures.FeaturesClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.client.option.GameOptions.getGenericValueText;

/**
 * File <b>OptionMixin</b> located on fr.idarkay.morefeatures.mixin
 * OptionMixin is a part of featurs-mod.
 * <p>
 * Copyright (c) 2020 featurs-mod.
 * <p>
 *
 * @author Alois. B. (IDarKay),
 * Created the 08/09/2020 at 23:26
 */
@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {

    private static final SimpleOption<Double> myGamma;
    private static boolean isFirstResponse = true;

    static {
        myGamma = new SimpleOption("options.gamma", SimpleOption.emptyTooltip(), (optionText, value) -> {
            int i = (int) ((Double) value * 100.0);
            if (i == 0) {
                return getGenericValueText(optionText, Text.translatable("options.gamma.min"));
            } else if (i == 50) {
                return getGenericValueText(optionText, Text.translatable("options.gamma.default"));
            } else {
                return i == 1000 ? getGenericValueText(optionText, Text.translatable("options.gamma.max")) : getGenericValueText(optionText, i);
            }
        }, SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier((progress) -> {
            FeaturesClient.options().featuresGamma = progress*10000;
            FeaturesClient.options().writeChanges();
            return progress * 10000;
            } , (value) -> value / 10000) , 1.0, (value) -> { });
    }

    @Inject(method = "getGamma()Lnet/minecraft/client/option/SimpleOption;", at = @At("RETURN"), cancellable = true)
    private void injectedReturn(CallbackInfoReturnable<SimpleOption<Double>> cir) {
        if (isFirstResponse) {
            myGamma.setValue(FeaturesClient.options().featuresGamma);
            isFirstResponse = false;
        }
        cir.setReturnValue(myGamma);
    }
}
