package fr.idarkay.morefeatures;

import fr.idarkay.morefeatures.options.FeaturesGameOptions;
import fr.idarkay.morefeatures.options.Options;
import fr.idarkay.morefeatures.options.screen.AutoFarmScreen;
import fr.idarkay.morefeatures.options.screen.FeaturesOptionsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.message.MessageType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FeaturesClient implements ClientModInitializer {
    private static final String MOD_ID = FeaturesMod.MOD_ID;
    private static FeaturesGameOptions CONFIG;

    //sound
    public static final Identifier BREAK_SAFE_ID = new Identifier("more_features_id:break_safe");
    public static final SoundEvent BREAK_SAFE_EVENT = new SoundEvent(BREAK_SAFE_ID);
    public static long LOCAL_TIME = 12000;
    public static boolean isEating = false;

    private long lastInput = 0;
    private int countDown = 0;
    private long lastShown = 0;


    @Override
    public void onInitializeClient() {
        Registry.register(Registry.SOUND_EVENT, FeaturesClient.BREAK_SAFE_ID, BREAK_SAFE_EVENT);
        KeyBindings.init();

        startClientTickEvents();
    }

    private void startClientTickEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.isPaused()) return;
            if (KeyBindings.OPEN_OPTIONS_KEYS.isPressed()) {
                client.setScreen(new FeaturesOptionsScreen(null, FeaturesClient.options()));
            } else if (FeaturesClient.options().localTime
                    && KeyBindings.ADD_LOCAL_TIME_KEYS.isPressed()) {
                FeaturesClient.LOCAL_TIME += 500;
            } else if (FeaturesClient.options().localTime
                    && KeyBindings.REMOVE_LOCAL_TIME_KEYS.isPressed()) {
                    FeaturesClient.LOCAL_TIME -= 500;
            } else if (System.currentTimeMillis() - lastInput > 250) {
                if (KeyBindings.ACTIVE_LOCAL_TIME.isPressed()) {
                    lastInput = System.currentTimeMillis();
                    Options.LOCAL_IME.set(FeaturesClient.options());
                } else if (KeyBindings.ATTACK_START_KEY.isPressed()) {
                    lastInput = System.currentTimeMillis();
                    boolean active = options().switchAttackActive();
                    String text = "message." + MOD_ID + ".attack" + (active ? "On" : "Off");
                    displayInHud(client, text);
                } else if (KeyBindings.MINE_START_KEY.isPressed()) {
                    lastInput = System.currentTimeMillis();
                    boolean active = options().switchMineActive();
                    client.options.attackKey.setPressed(false);
                    String text = "message." + MOD_ID + ".mine" + (active ? "On" : "Off");
                    displayInHud(client, text);
                } else if (KeyBindings.CLICK_START_KEY.isPressed()) {
                    lastInput = System.currentTimeMillis();
                    boolean active = options().switchClickActive();
                    client.options.attackKey.setPressed(false);
                    String text = "message." + MOD_ID + ".click" + (active ? "On" : "Off");
                    displayInHud(client, text);
                } else if (KeyBindings.AUTO_FARM_OPTIONS_KEY.isPressed()) {
                    lastInput = System.currentTimeMillis();
                    client.setScreen(new AutoFarmScreen(null, options()));
                } else if (KeyBindings.TOGGLE_BREAK_PROTECTION.isPressed()) {
                    lastInput = System.currentTimeMillis();
                    options().breakSafe = !options().breakSafe;
                    lastShown = System.currentTimeMillis();
                    String text = "message." + MOD_ID + ".break_protection" + (options().breakSafe ? "On" : "Off");
                    displayInHud(client, text);
                }
            }
            if (System.currentTimeMillis() - lastShown > 3000) {
                if (options().breakSafeWarning) {
                    if (!options().breakSafe) {
                        if (client != null && client.player != null && client.player.getMainHandStack() != null) {
                            ItemStack mainHand = client.player.getMainHandStack();
                            if (mainHand.isDamageable()) {
                                lastShown = System.currentTimeMillis();
                                String text = "message." + MOD_ID + ".noBreakProtection";
                                displayInHud(client, text);
                            }
                        }
                    }
                }
            }
            if (options().autoAttackActivated) {
                countDown--;
                if (countDown > 0) {
                    countDown = (int) options().attackCoolDown;
                    if (client instanceof PublicMinecraftClientEditor) {
                        ((PublicMinecraftClientEditor) client).publicDoAttack();
                    } else {
                        errorMessage(client);
                        return;
                    }
                }
                doEat(client);
            } else if (options().autoMineActivated) {
                client.options.attackKey.setPressed(true);
                doEat(client);
            } else if (options().autoClickActivated) {
                countDown--;
                if (countDown <= 0) {
                    countDown = (int) options().useCoolDown;
                    if (client instanceof PublicMinecraftClientEditor) {
                        ((PublicMinecraftClientEditor) client).publicDoItemUse();
                    } else {
                        errorMessage(client);
                        isEating = false;
                    }
                }
            }
        });
    }

    private void displayInHud(MinecraftClient client, String text) {
        MutableText mutableText = Text.translatable(text);
        client.inGameHud.onGameMessage(new MessageType(Optional.empty(), Optional.of(MessageType.DisplayRule.of()), Optional.empty()), mutableText); //Util.NIL_UUID
    }

    private void doEat(MinecraftClient client) {
        if (options().eatOn && !client.player.isSpectator() && !client.player.isCreative()) {
            if (client.currentScreen == null || client.currentScreen.passEvents) {
                if (client.player.getOffHandStack().getItem().isFood()) {
                    if (client.player.getHungerManager().getFoodLevel() <= options().eatLvlLimit) {
                        if (!client.player.isUsingItem()) {
                            if (client instanceof PublicMinecraftClientEditor) {
                                ((PublicMinecraftClientEditor) client).publicDoItemUse();
                            } else {
                                errorMessage(client);
                                isEating = false;
                                return;
                            }
                        }
                        isEating = true;
                    } else isEating = false;
                } else {
                    String text = "message." + MOD_ID + ".noFood";
                    displayInHud(client, text);
                    isEating = false;
                }
            } else isEating = false;
        } else isEating = false;
    }

    private void errorMessage(MinecraftClient client) {
        options().autoAttackActivated = false;
        String text = "message." + MOD_ID + ".error";
        displayInHud(client, text);
    }

    public static FeaturesGameOptions options() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }

    private static FeaturesGameOptions loadConfig() {

        return FeaturesGameOptions.load(new File("config/more_features_id.json"));
    }

}
