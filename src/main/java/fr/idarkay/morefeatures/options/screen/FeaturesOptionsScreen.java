package fr.idarkay.morefeatures.options.screen;

import fr.idarkay.morefeatures.FeaturesClient;
import fr.idarkay.morefeatures.options.FeaturesGameOptions;
import fr.idarkay.morefeatures.options.Option;
import fr.idarkay.morefeatures.options.Options;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

/**
 * File <b>FeatursOptionsScreen</b> located on fr.idarkay.breaksafe.options
 * FeatursOptionsScreen is a part of fabric-example-mod.
 * <p>
 * Copyright (c) 2020 fabric-example-mod.
 * <p>
 *
 * @author Alois. B. (IDarKay),
 * Created the 27/07/2020 at 16:46
 */
@Environment(EnvType.CLIENT)
public class FeaturesOptionsScreen extends FeaturesScreen {
    private static final MenuButton[] SUB_MENU_BUTTONS = new MenuButton[]{MenuButtons.BREAKAGE_PROTECTION,
            MenuButtons.LIGHT_ITEM, MenuButtons.AUTO_FARM};
    private static final Option[] OPTIONS = new Option[]{Options.FOG_LAVA, Options.AMBIENT_FOG, Options.HIDE_FIRE,
            Options.HIDE_FIRE_ONLY_ON_RESISTANCE, Options.HIDE_PUMPKIN, Options.SHOW_EFFECT_TIME,
            Options.LOCAL_IME, Options.RENDER_BEACON_BEAM};

    public FeaturesOptionsScreen(@Nullable Screen parent) {
        this(parent, FeaturesClient.options());
    }

    public FeaturesOptionsScreen(@Nullable Screen parent, FeaturesGameOptions featuresGameOptions) {
        super(MenuButtons.FEATURES_TEXT, parent, featuresGameOptions, OPTIONS, SUB_MENU_BUTTONS);
    }
}
