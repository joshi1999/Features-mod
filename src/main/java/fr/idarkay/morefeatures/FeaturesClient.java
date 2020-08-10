package fr.idarkay.morefeatures;

import fr.idarkay.morefeatures.options.FeaturesGameOptions;
import fr.idarkay.morefeatures.options.FeaturesOptionsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.io.File;

@Environment(EnvType.CLIENT)
public class FeaturesClient implements ClientModInitializer
{

	private static FeaturesGameOptions CONFIG;

	//sound
	public static final Identifier BREAK_SAFE_ID = new Identifier("more_features_id:break_safe");
	public static final SoundEvent BREAK_SAFE_EVENT = new SoundEvent(BREAK_SAFE_ID);
	public static long LOCAL_TIME = 12000;




	@Override
	public void onInitializeClient()
	{
		Registry.register(Registry.SOUND_EVENT, FeaturesClient.BREAK_SAFE_ID, BREAK_SAFE_EVENT);
		KeyBindings.init();
	}

	public static FeaturesGameOptions options() {
		if (CONFIG == null) {
			CONFIG = loadConfig();
		}

		return CONFIG;
	}

	private static FeaturesGameOptions loadConfig() {
		FeaturesGameOptions config = FeaturesGameOptions.load(new File("config/more_features_id.json"));

		return config;
	}

}
