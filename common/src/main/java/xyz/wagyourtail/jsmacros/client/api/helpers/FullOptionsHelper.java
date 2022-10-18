package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.AoMode;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Arm;
import net.minecraft.world.Difficulty;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.ArrayUtils;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinSimpleOption;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FullOptionsHelper extends BaseHelper<GameOptions> {

    private static final Map<String, SoundCategory> SOUND_CATEGORY_MAP = Arrays.stream(SoundCategory.values()).collect(Collectors.toMap(SoundCategory::getName, Function.identity()));
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final ResourcePackManager rpm = mc.getResourcePackManager();

    public final SkinOptionsHelper skin = new SkinOptionsHelper(this);
    public final VideoOptionsHelper video = new VideoOptionsHelper(this);
    public final MusicOptionsHelper music = new MusicOptionsHelper(this);
    public final ControlOptionsHelper control = new ControlOptionsHelper(this);
    public final ChatOptionsHelper chat = new ChatOptionsHelper(this);
    public final AccessibilityOptionsHelper accessibility = new AccessibilityOptionsHelper(this);

    public FullOptionsHelper(GameOptions options) {
        super(options);
    }

    /**
     * @return a helper for the skin options.
     *
     * @since 1.8.4
     */
    public SkinOptionsHelper getSkinOptions() {
        return skin;
    }

    /**
     * @return a helper for the video options.
     *
     * @since 1.8.4
     */
    public VideoOptionsHelper getVideoOptions() {
        return video;
    }

    /**
     * @return a helper for the music options.
     *
     * @since 1.8.4
     */
    public MusicOptionsHelper getMusicOptions() {
        return music;
    }

    /**
     * @return a helper for the control options.
     *
     * @since 1.8.4
     */
    public ControlOptionsHelper getControlOptions() {
        return control;
    }

    /**
     * @return a helper for the chat options.
     *
     * @since 1.8.4
     */
    public ChatOptionsHelper getChatOptions() {
        return chat;
    }

    /**
     * @return a helper for the accessibility options.
     *
     * @since 1.8.4
     */
    public AccessibilityOptionsHelper getAccessibilityOptions() {
        return accessibility;
    }

    /**
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public FullOptionsHelper saveOptions() {
        base.write();
        return this;
    }

    /**
     * @return list of names of resource packs.
     *
     * @since 1.1.7
     */
    public List<String> getResourcePacks() {
        return new ArrayList<>(rpm.getNames());
    }

    /**
     * @return list of names of enabled resource packs.
     *
     * @since 1.2.0
     */
    public List<String> getEnabledResourcePacks() {
        return new ArrayList<>(rpm.getEnabledNames());
    }

    /**
     * Set the enabled resource packs to the provided list.
     *
     * @param enabled
     * @return self for chaining.
     *
     * @since 1.2.0
     */
    public FullOptionsHelper setEnabledResourcePacks(String[] enabled) {
        Collection<String> en = Arrays.stream(enabled).distinct().toList();
        List<String> currentRP = ImmutableList.copyOf(base.resourcePacks);
        rpm.setEnabledProfiles(en);
        base.resourcePacks.clear();
        base.incompatibleResourcePacks.clear();
        for (ResourcePackProfile p : rpm.getEnabledProfiles()) {
            if (!p.isPinned()) {
                base.resourcePacks.add(p.getName());
                if (!p.getCompatibility().isCompatible()) {
                    base.incompatibleResourcePacks.add(p.getName());
                }
            }
        }
        base.write();
        List<String> newRP = ImmutableList.copyOf(base.resourcePacks);
        if (!currentRP.equals(newRP)) {
            mc.reloadResources();
        }
        return this;
    }

    /**
     * @return the current fov value.
     *
     * @since 1.1.7
     */
    public int getFov() {
        return base.getFov().getValue();
    }

    /**
     * @param fov the new fov value
     * @return self for chaining.
     *
     * @since 1.1.7
     */
    public FullOptionsHelper setFov(int fov) {
        getBase(base.getFov()).forceSetValue(fov);
        return this;
    }

    /**
     * @return the current view distance.
     *
     * @since 1.8.4
     */
    public int getViewDistance() {
        return base.getViewDistance().getValue();
    }

    /**
     * @return the active language.
     *
     * @since 1.8.4
     */
    public String getLanguage() {
        return base.language;
    }

    /**
     * @param languageCode the language to change to
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public FullOptionsHelper setLanguage(String languageCode) {
        LanguageDefinition language = MinecraftClient.getInstance().getLanguageManager().getLanguage(languageCode);
        base.language = language.getCode();
        MinecraftClient.getInstance().reloadResources();
        base.write();
        return this;
    }

    /**
     * @return the active difficulty.
     *
     * @since 1.8.4
     */
    public String getDifficulty() {
        return mc.world.getDifficulty().getName();
    }

    /**
     * The name be either "peaceful", "easy", "normal", or "hard".
     *
     * @param name the name of the difficulty to change to
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public FullOptionsHelper setDifficulty(String name) {
        if (mc.isIntegratedServerRunning()) {
            mc.getServer().setDifficulty(Difficulty.byName(name), true);
        }
        return this;
    }

    /**
     * @return {@code true} if the difficulty is locked, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isDifficultyLocked() {
        return MinecraftClient.getInstance().world.getLevelProperties().isDifficultyLocked();
    }

    /**
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public FullOptionsHelper lockDifficulty() {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(true));
        return this;
    }

    /**
     * Unlocks the difficulty of the world. This can't be done in an unmodified client.
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public FullOptionsHelper unlockDifficulty() {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(false));
        return this;
    }

    private MixinSimpleOption getBase(SimpleOption<?> option) {
        return (MixinSimpleOption) (Object) option;
    }

    public class SkinOptionsHelper {

        public final FullOptionsHelper parent;

        public SkinOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        /**
         * @return the parent options helper.
         *
         * @since 1.8.4
         */
        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return {@code true} if the player's cape should be shown, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isCapeActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.CAPE);
        }

        /**
         * @return {@code true} if the player's jacket should be shown, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isJacketActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.JACKET);
        }

        /**
         * @return {@code true} if the player's left sleeve should be shown, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isLeftSleeveActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.LEFT_SLEEVE);
        }

        /**
         * @return {@code true} if the player's right sleeve should be shown, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRightSleeveActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_SLEEVE);
        }

        /**
         * @return {@code true} if the player's left pants should be shown, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isLeftPantsActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.LEFT_PANTS_LEG);
        }

        /**
         * @return {@code true} if the player's right pants should be shown, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRightPantsActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_PANTS_LEG);
        }

        /**
         * @return {@code true} if the player's hat should be shown, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isHatActivated() {
            return base.isPlayerModelPartEnabled(PlayerModelPart.HAT);
        }

        /**
         * @return {@code true} if the player's main hand is the right one, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRightHand() {
            return base.getMainArm().getValue() == Arm.RIGHT;
        }

        /**
         * @param val whether the cape should be shown or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleCape(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.CAPE, val);
            return this;
        }

        /**
         * @param val whether the jacket should be shown or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleJacket(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.JACKET, val);
            return this;
        }

        /**
         * @param val whether the left sleeve should be shown or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleLeftSleeve(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.LEFT_SLEEVE, val);
            return this;
        }

        /**
         * @param val whether the right sleeve should be shown or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleRightSleeve(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.RIGHT_SLEEVE, val);
            return this;
        }

        /**
         * @param val whether the left pants should be shown or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleLeftPants(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.LEFT_PANTS_LEG, val);
            return this;
        }

        /**
         * @param val whether the right pants should be shown or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleRightPants(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.RIGHT_PANTS_LEG, val);
            return this;
        }

        /**
         * @param val whether the hat should be shown or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleHat(boolean val) {
            base.togglePlayerModelPart(PlayerModelPart.HAT, val);
            return this;
        }

        /**
         * The hand must be either "left" or "right".
         *
         * @param hand the hand to set as main hand
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public SkinOptionsHelper toggleMainHand(String hand) {
            base.getMainArm().setValue(hand.equals("left") ? Arm.LEFT : Arm.RIGHT);
            return this;
        }

    }

    public class VideoOptionsHelper {

        public final FullOptionsHelper parent;

        public VideoOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        /**
         * @return the parent options helper.
         *
         * @since 1.8.4
         */
        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return the full screen resolution as a string.
         *
         * @since 1.8.4
         */
        public String getFullscreenResolution() {
            return base.fullscreenResolution;
        }

        /**
         * @return the current biome blend radius.
         *
         * @since 1.8.4
         */
        public int getBiomeBlendRadius() {
            return base.getBiomeBlendRadius().getValue();
        }

        /**
         * @param radius the new biome blend radius
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setBiomeBlendRadius(int radius) {
            getBase(base.getBiomeBlendRadius()).forceSetValue(radius);
            return this;
        }

        /**
         * @return the selected graphics mode.
         *
         * @since 1.8.4
         */
        public String getGraphicsMode() {
            return switch (base.getGraphicsMode().getValue()) {
                case FAST -> "fast";
                case FANCY -> "fancy";
                case FABULOUS -> "fabulous";
            };
        }

        /**
         * @param mode the graphics mode to select. Must be either "fast", "fancy" or "fabulous"
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setGraphicsMode(String mode) {
            base.getGraphicsMode().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "FAST" -> GraphicsMode.FAST;
                case "FANCY" -> GraphicsMode.FANCY;
                case "FABULOUS" -> GraphicsMode.FABULOUS;
                default -> base.getGraphicsMode().getValue();
            });
            return this;
        }

        /**
         * @return the selected chunk builder mode.
         *
         * @since 1.8.4
         */
        public String getChunkBuilderMode() {
            return switch (base.getChunkBuilderMode().getValue()) {
                case NONE -> "none";
                case NEARBY -> "nearby";
                case PLAYER_AFFECTED -> "player_affected";
            };
        }

        /**
         * @param mode the chunk builder mode to select. Must be either "none", "nearby" or
         *             "player_affected"
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setChunkBuilderMode(String mode) {
            base.getChunkBuilderMode().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "NONE" -> ChunkBuilderMode.NONE;
                case "NEARBY" -> ChunkBuilderMode.NEARBY;
                case "PLAYER_AFFECTED" -> ChunkBuilderMode.PLAYER_AFFECTED;
                default -> base.getChunkBuilderMode().getValue();
            });
            return this;
        }

        /**
         * @return the selected smooth lightning mode.
         *
         * @since 1.8.4
         */
        public String getSmoothLightningMode() {
            return switch (base.getAo().getValue()) {
                case OFF -> "off";
                case MIN -> "min";
                case MAX -> "max";
            };
        }

        /**
         * @param mode the smooth lightning mode to select. Must be either "off", "min" or "max"
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setSmoothLightningMode(String mode) {
            base.getAo().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF" -> AoMode.OFF;
                case "MIN" -> AoMode.MIN;
                case "MAX" -> AoMode.MAX;
                default -> base.getAo().getValue();
            });
            return this;
        }

        /**
         * @return the current render distance in chunks.
         *
         * @since 1.8.4
         */
        public int getRenderDistance() {
            return base.getViewDistance().getValue();
        }

        /**
         * @param radius the new render distance in chunks
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setRenderDistance(int radius) {
            base.getViewDistance().setValue(radius);
            return this;
        }

        /**
         * @return the current simulation distance in chunks.
         *
         * @since 1.8.4
         */
        public int getSimulationDistance() {
            return base.getSimulationDistance().getValue();
        }

        /**
         * @param radius the new simulation distance in chunks
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setSimulationDistance(int radius) {
            base.getSimulationDistance().setValue(radius);
            return this;
        }

        /**
         * @return the current upper fps limit.
         *
         * @since 1.8.4
         */
        public int getMaxFps() {
            return base.getMaxFps().getValue();
        }

        /**
         * @param maxFps the new maximum fps limit
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setMaxFps(int maxFps) {
            base.getMaxFps().setValue(maxFps);
            return this;
        }

        /**
         * @return {@code true} if vsync is enabled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isVsyncEnabled() {
            return base.getEnableVsync().getValue();
        }

        /**
         * @param val whether to enable vsync or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper enableVsync(boolean val) {
            base.getEnableVsync().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if view bobbing is enabled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isViewBobbingEnabled() {
            return base.getBobView().getValue();
        }

        /**
         * @param val whether to enable view bobbing or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper enableViewBobbing(boolean val) {
            base.getBobView().setValue(val);
            return this;
        }

        /**
         * @return the current gui scale.
         *
         * @since 1.8.4
         */
        public int getGuiScale() {
            return base.getGuiScale().getValue();
        }

        /**
         * @param scale the gui scale to set. Must be 1, 2, 3 or 4
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setGuiScale(int scale) {
            base.getGuiScale().setValue(scale);
            mc.execute(mc::onResolutionChanged);
            return this;
        }

        /**
         * @return the current attack indicator type.
         *
         * @since 1.8.4
         */
        public String getAttackIndicatorType() {
            return switch (base.getAttackIndicator().getValue()) {
                case OFF -> "off";
                case CROSSHAIR -> "crosshair";
                case HOTBAR -> "hotbar";
            };
        }

        /**
         * @param type the attack indicator type. Must be either "off", "crosshair", or "hotbar"
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setAttackIndicatorType(String type) {
            base.getAttackIndicator().setValue(switch (type.toUpperCase(Locale.ROOT)) {
                case "OFF" -> AttackIndicator.OFF;
                case "CROSSHAIR" -> AttackIndicator.CROSSHAIR;
                case "HOTBAR" -> AttackIndicator.HOTBAR;
                default -> base.getAttackIndicator().getValue();
            });
            return this;
        }

        /**
         * @return the current gamma value.
         *
         * @since 1.8.4
         */
        public double getGamma() {
            return getBrightness();
        }

        /**
         * @param gamma the new gamma value
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setGamma(double gamma) {
            return setBrightness(gamma);
        }

        /**
         * @return the current brightness value.
         *
         * @since 1.8.4
         */
        public double getBrightness() {
            return base.getGamma().getValue();
        }

        /**
         * @param gamma the new brightness value
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setBrightness(double gamma) {
            getBase(base.getGamma()).forceSetValue(gamma);
            return this;
        }

        /**
         * @return the current cloud rendering mode.
         *
         * @since 1.8.4
         */
        public String getCloudsMode() {
            return switch (base.getCloudRenderMode().getValue()) {
                case OFF -> "off";
                case FAST -> "fast";
                case FANCY -> "fancy";
            };
        }

        /**
         * @param mode the cloud rendering mode to select. Must be either "off", "fast" or "fancy"
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setCloudsMode(String mode) {
            base.getCloudRenderMode().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF" -> CloudRenderMode.OFF;
                case "FAST" -> CloudRenderMode.FAST;
                case "FANCY" -> CloudRenderMode.FANCY;
                default -> base.getCloudRenderMode().getValue();
            });
            return this;
        }

        /**
         * @return {@code true} if the game is running in fullscreen mode, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isFullscreen() {
            return base.getFullscreen().getValue();
        }

        /**
         * @param fullscreen whether to enable fullscreen mode or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setFullScreen(boolean fullscreen) {
            base.getFullscreen().setValue(fullscreen);
            return this;
        }

        /**
         * @return the current particle rendering mode.
         *
         * @since 1.8.4
         */
        public String getParticleMode() {
            return switch (base.getParticles().getValue()) {
                case MINIMAL -> "minimal";
                case DECREASED -> "decreased";
                case ALL -> "all";
            };
        }

        /**
         * @param mode the particle rendering mode to select. Must be either "minimal", "decreased"
         *             or "all"
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setParticleMode(String mode) {
            base.getParticles().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "MINIMAL" -> ParticlesMode.MINIMAL;
                case "DECREASED" -> ParticlesMode.DECREASED;
                case "ALL" -> ParticlesMode.ALL;
                default -> base.getParticles().getValue();
            });
            return this;
        }

        /**
         * @return the current mip map level.
         *
         * @since 1.8.4
         */
        public int getMipMapLevels() {
            return base.getMipmapLevels().getValue();
        }

        /**
         * @param val the new mip map level
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setMipMapLevels(int val) {
            base.getMipmapLevels().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if entity shadows should be rendered, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean areEntityShadowsEnabled() {
            return base.getEntityShadows().getValue();
        }

        /**
         * @param val whether to enable entity shadows or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper enableEntityShadows(boolean val) {
            base.getEntityShadows().setValue(val);
            return this;
        }

        /**
         * @return the current distortion effect scale.
         *
         * @since 1.8.4
         */
        public double getDistortionEffect() {
            return base.getDistortionEffectScale().getValue();
        }

        /**
         * @param val the new distortion effect scale
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setDistortionEffects(double val) {
            base.getDistortionEffectScale().setValue(val);
            return this;
        }

        /**
         * @return the current entity render distance.
         *
         * @since 1.8.4
         */
        public double getEntityDistance() {
            return base.getEntityDistanceScaling().getValue();
        }

        /**
         * @param val the new entity render distance
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setEntityDistance(double val) {
            base.getEntityDistanceScaling().setValue(val);
            return this;
        }

        /**
         * @return the current fov value.
         *
         * @since 1.8.4
         */
        public double getFovEffects() {
            return base.getFovEffectScale().getValue();
        }

        /**
         * @param val the new fov value
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper setFovEffects(double val) {
            getBase(base.getFovEffectScale()).forceSetValue(val);
            return this;
        }

        /**
         * @return {@code true} if the autosave indicator is enabled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isAutosaveIndicatorEnabled() {
            return base.getShowAutosaveIndicator().getValue();
        }

        /**
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public VideoOptionsHelper enableAutosaveIndicator(boolean val) {
            base.getShowAutosaveIndicator().setValue(val);
            return this;
        }

    }

    public class MusicOptionsHelper {

        public final FullOptionsHelper parent;

        public MusicOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        /**
         * @return the parent options helper.
         *
         * @since 1.8.4
         */
        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return the current master volume.
         *
         * @since 1.8.4
         */
        public float getMasterVolume() {
            return base.getSoundVolume(SoundCategory.MASTER);
        }

        /**
         * @param volume the new master volume
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setMasterVolume(double volume) {
            base.setSoundVolume(SoundCategory.MASTER, (float) volume);
            return this;
        }

        /**
         * @return the current music volume.
         *
         * @since 1.8.4
         */
        public float getMusicVolume() {
            return base.getSoundVolume(SoundCategory.MUSIC);
        }

        /**
         * @param volume the new music volume
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setMusicVolume(double volume) {
            base.setSoundVolume(SoundCategory.MUSIC, (float) volume);
            return this;
        }

        /**
         * @return the current value of played recods.
         *
         * @since 1.8.4
         */
        public float getRecordsVolume() {
            return base.getSoundVolume(SoundCategory.RECORDS);
        }

        /**
         * @param volume the new volume for playing records
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setRecordsVolume(double volume) {
            base.setSoundVolume(SoundCategory.RECORDS, (float) volume);
            return this;
        }

        /**
         * @return the current volume of the weather.
         *
         * @since 1.8.4
         */
        public float getWeatherVolume() {
            return base.getSoundVolume(SoundCategory.WEATHER);
        }

        /**
         * @param volume the new volume for the weather
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setWeatherVolume(double volume) {
            base.setSoundVolume(SoundCategory.WEATHER, (float) volume);
            return this;
        }

        /**
         * @return the current volume of block related sounds.
         *
         * @since 1.8.4
         */
        public float getBlocksVolume() {
            return base.getSoundVolume(SoundCategory.BLOCKS);
        }

        /**
         * @param volume the new volume for block sounds
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setBlocksVolume(double volume) {
            base.setSoundVolume(SoundCategory.BLOCKS, (float) volume);
            return this;
        }

        /**
         * @return the current volume of hostile mobs.
         *
         * @since 1.8.4
         */
        public float getHostileVolume() {
            return base.getSoundVolume(SoundCategory.HOSTILE);
        }

        /**
         * @param volume the new volume for hostile mobs
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setHostileVolume(double volume) {
            base.setSoundVolume(SoundCategory.HOSTILE, (float) volume);
            return this;
        }

        /**
         * @return the current volume of neutral mobs.
         *
         * @since 1.8.4
         */
        public float getNeutralVolume() {
            return base.getSoundVolume(SoundCategory.NEUTRAL);
        }

        /**
         * @param volume the new volume for neutral mobs
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setNeutralVolume(double volume) {
            base.setSoundVolume(SoundCategory.NEUTRAL, (float) volume);
            return this;
        }

        /**
         * @return the current player volume.
         *
         * @since 1.8.4
         */
        public float getPlayerVolume() {
            return base.getSoundVolume(SoundCategory.PLAYERS);
        }

        /**
         * @param volume the new player volume
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setPlayerVolume(double volume) {
            base.setSoundVolume(SoundCategory.PLAYERS, (float) volume);
            return this;
        }

        /**
         * @return the current ambient volume.
         *
         * @since 1.8.4
         */
        public float getAmbientVolume() {
            return base.getSoundVolume(SoundCategory.AMBIENT);
        }

        /**
         * @param volume the new ambient volume
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setAmbientVolume(double volume) {
            base.setSoundVolume(SoundCategory.AMBIENT, (float) volume);
            return this;
        }

        /**
         * @return the current voice volume.
         *
         * @since 1.8.4
         */
        public float getVoiceVolume() {
            return base.getSoundVolume(SoundCategory.VOICE);
        }

        /**
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setVoiceVolume(double volume) {
            base.setSoundVolume(SoundCategory.VOICE, (float) volume);
            return this;
        }

        /**
         * @param category the category to get the volume of
         * @return the volume of the given sound category.
         *
         * @since 1.8.4
         */
        public float getVolume(String category) {
            return base.getSoundVolume(SOUND_CATEGORY_MAP.get(category));
        }

        /**
         * @return a map of all sound categories and their volumes.
         *
         * @since 1.8.4
         */
        public Map<String, Float> getVolumes() {
            Map<String, Float> volumes = new HashMap<>();
            for (SoundCategory category : SoundCategory.values()) {
                volumes.put(category.getName(), base.getSoundVolume(category));
            }
            return volumes;
        }

        /**
         * @param category the category to set the volume for
         * @param volume   the new volume
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setVolume(String category, double volume) {
            base.setSoundVolume(SOUND_CATEGORY_MAP.get(category), (float) volume);
            return this;
        }

        /**
         * @return the currently selected sound device.
         *
         * @since 1.8.4
         */
        public String getSoundDevice() {
            return base.getSoundDevice().getValue();
        }

        /**
         * @param audioDevice the audio device to use
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper setSoundDevice(String audioDevice) {
            List<String> audioDevices = getAudioDevices();
            if (!audioDevices.contains(audioDevice)) {
                audioDevice = "";
            }
            base.getSoundDevice().setValue(audioDevice);
            SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
            soundManager.reloadSounds();
            return this;
        }

        /**
         * @return a list of all connected audio devices.
         *
         * @since 1.8.4
         */
        public List<String> getAudioDevices() {
            return Stream.concat(Stream.of(""), MinecraftClient.getInstance().getSoundManager().getSoundDevices().stream()).toList();
        }

        /**
         * @return {@code true} if subtitles should be shown, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean areSubtitlesShown() {
            return base.getShowSubtitles().getValue();
        }

        /**
         * @param val whether subtitles should be shown or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public MusicOptionsHelper showSubtitles(boolean val) {
            base.getShowSubtitles().setValue(val);
            return this;
        }

    }

    public class ControlOptionsHelper {

        public final FullOptionsHelper parent;

        public ControlOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        /**
         * @return the parent options helper.
         *
         * @since 1.8.4
         */
        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return the current mouse sensitivity.
         *
         * @since 1.8.4
         */
        public double getMouseSensitivity() {
            return base.getMouseSensitivity().getValue();
        }

        /**
         * @param val the new mouse sensitivity
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper setMouseSensitivity(double val) {
            getBase(base.getMouseSensitivity()).forceSetValue(val);
            return this;
        }

        /**
         * @return {@code true} if the mouse direction should be inverted.
         *
         * @since 1.8.4
         */
        public boolean isMouseInverted() {
            return base.getInvertYMouse().getValue();
        }

        /**
         * @param val whether to invert the mouse direction or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper invertMouse(boolean val) {
            base.getInvertYMouse().setValue(val);
            return this;
        }

        /**
         * @return the current mouse wheel sensitivity.
         *
         * @since 1.8.4
         */
        public double getMouseWheelSensitivity() {
            return base.getMouseWheelSensitivity().getValue();
        }

        /**
         * @param val the new mouse wheel sensitivity
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper setMouseWheelSensitivity(double val) {
            getBase(base.getMouseWheelSensitivity()).forceSetValue(val);
            return this;
        }

        /**
         * This option was introduced due to a bug on some systems where the mouse wheel would
         * scroll too fast.
         *
         * @return {@code true} if discrete scrolling is enabled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isDiscreteScrollingEnabled() {
            return base.getDiscreteMouseScroll().getValue();
        }

        /**
         * @param val whether to enable discrete scrolling or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper enableDiscreteScrolling(boolean val) {
            base.getDiscreteMouseScroll().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if touchscreen mode is enabled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isTouchscreenEnabled() {
            return base.getTouchscreen().getValue();
        }

        /**
         * @param val whether to enable touchscreen mode or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper enableTouchscreen(boolean val) {
            base.getTouchscreen().setValue(val);
            return this;
        }

        /**
         * Raw input is directly reading the mouse data, without any adjustments due to other
         * programs or the operating system.
         *
         * @return {@code true} if raw mouse input is enabled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRawMouseInputEnabled() {
            return base.getRawMouseInput().getValue();
        }

        /**
         * @param val whether to enable raw mouse input or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper enableRawMouseInput(boolean val) {
            base.getRawMouseInput().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if auto jump is enabled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isAutoJumpEnabled() {
            return base.getAutoJump().getValue();
        }

        /**
         * @param val whether to enable auto jump or not or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper enableAutoJump(boolean val) {
            base.getAutoJump().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if the toggle functionality for sneaking is enabled, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isSneakTogglingEnabled() {
            return base.getSneakToggled().getValue();
        }

        /**
         * @param val whether to enable or disable the toggle functionality for sneaking
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper toggleSneak(boolean val) {
            base.getSneakToggled().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if the toggle functionality for sprinting is enabled, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isSprintTogglingEnabled() {
            return base.getSprintToggled().getValue();
        }

        /**
         * @param val whether to enable or disable the toggle functionality for sprinting
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ControlOptionsHelper toggleSprint(boolean val) {
            base.getSprintToggled().setValue(val);
            return this;
        }

        /**
         * @return an array of all raw minecraft keybindings.
         *
         * @since 1.8.4
         */
        public KeyBinding[] getRawKeys() {
            return ArrayUtils.clone(base.allKeys);
        }

        /**
         * @return a list of all keybinding catehories.
         *
         * @since 1.8.4
         */
        public List<String> getCategories() {
            return Arrays.stream(base.allKeys).map(KeyBinding::getCategory).distinct().toList();
        }

        /**
         * @return a list of all key names.
         *
         * @since 1.8.4
         */
        public List<String> getKeys() {
            return Arrays.stream(base.allKeys).map(KeyBinding::getTranslationKey).toList();
        }

        /**
         * @return a map of all keybindings and their bound key.
         *
         * @since 1.8.4
         */
        public Map<String, String> getKeyBinds() {
            Map<String, String> keyBinds = new HashMap<>(base.allKeys.length);

            for (KeyBinding key : base.allKeys) {
                keyBinds.put(Text.translatable(key.getTranslationKey()).getString(), key.getBoundKeyLocalizedText().getString());
            }
            return keyBinds;
        }

        /**
         * @param category the category to get keybindings from
         * @return a map of all keybindings and their bound key in the specified category.
         *
         * @since 1.8.4
         */
        public Map<String, String> getKeyBindsByCategory(String category) {
            return getKeyBindsByCategory().get(category);
        }

        /**
         * @return a map of all keybinding categories, containing a map of all keybindings in that
         *         category and their bound key.
         *
         * @since 1.8.4
         */
        public Map<String, Map<String, String>> getKeyBindsByCategory() {
            Map<String, Map<String, String>> entries = new HashMap<>(MinecraftClient.getInstance().options.allKeys.length);

            for (KeyBinding key : MinecraftClient.getInstance().options.allKeys) {
                Map<String, String> categoryMap;
                String category = key.getCategory();
                if (!entries.containsKey(category)) {
                    categoryMap = new HashMap<>();
                    entries.put(category, categoryMap);
                } else {
                    categoryMap = entries.get(category);
                }
                categoryMap.put(Text.translatable(key.getTranslationKey()).getString(), key.getBoundKeyLocalizedText().getString());
            }
            return entries;
        }

    }

    public class ChatOptionsHelper {

        public final FullOptionsHelper parent;

        public ChatOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        /**
         * @return the parent options helper.
         *
         * @since 1.8.4
         */
        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return the current chat visibility mode.
         *
         * @since 1.8.4
         */
        public String getChatVisibility() {
            String chatVisibilityKey = base.getChatVisibility().getValue().getTranslationKey();
            return chatVisibilityKey.substring(chatVisibilityKey.lastIndexOf('.'));
        }

        /**
         * @param mode the new chat visibility mode. Must be "FULL", "SYSTEM" or "HIDDEN
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatVisibility(String mode) {
            base.getChatVisibility().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "FULL" -> ChatVisibility.FULL;
                case "SYSTEM" -> ChatVisibility.SYSTEM;
                case "HIDDEN" -> ChatVisibility.HIDDEN;
                default -> base.getChatVisibility().getValue();
            });
            return this;
        }

        /**
         * @return {@code true} if messages can use color codes, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean areColorsShown() {
            return base.getChatColors().getValue();
        }

        /**
         * @param val whether to allow color codes in messages or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setShowColors(boolean val) {
            base.getChatColors().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if it's allowed to open web links from chat, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean areWebLinksEnabled() {
            return base.getChatLinks().getValue();
        }

        /**
         * @param val whether to allow opening web links from chat or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper enableWebLinks(boolean val) {
            base.getChatLinks().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if a warning prompt before opening links should be shown,
         *         {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isWebLinkPromptEnabled() {
            return base.getChatLinksPrompt().getValue();
        }

        /**
         * @param val whether to show warning prompts before opening links or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper enableWebLinkPrompt(boolean val) {
            base.getChatLinksPrompt().setValue(val);
            return this;
        }

        /**
         * @return the current chat opacity.
         *
         * @since 1.8.4
         */
        public double getChatOpacity() {
            return base.getChatOpacity().getValue();
        }

        /**
         * @param val the new chat opacity
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatOpacity(double val) {
            base.getChatOpacity().setValue(val);
            return this;
        }

        /**
         * @param val the new background opacity for text
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setTextBackgroundOpacity(double val) {
            getBase(base.getTextBackgroundOpacity()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current background opacity of text.
         *
         * @since 1.8.4
         */
        public double getTextBackgroundOpacity() {
            return base.getTextBackgroundOpacity().getValue();
        }

        /**
         * @return the current text size.
         *
         * @since 1.8.4
         */
        public double getTextSize() {
            return base.getChatScale().getValue();
        }

        /**
         * @param val the new text size
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setTextSize(double val) {
            getBase(base.getChatScale()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current chat line spacing.
         *
         * @since 1.8.4
         */
        public double getChatLineSpacing() {
            return base.getChatLineSpacing().getValue();
        }

        /**
         * @param val the new chat line spacing
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatLineSpacing(double val) {
            getBase(base.getChatLineSpacing()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current chat delay in seconds.
         *
         * @since 1.8.4
         */
        public double getChatDelay() {
            return base.getChatDelay().getValue();
        }

        /**
         * @param val the new chat delay in seconds
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatDelay(double val) {
            base.getChatDelay().setValue(val);
            return this;
        }

        /**
         * @return the current chat width.
         *
         * @since 1.8.4
         */
        public double getChatWidth() {
            return base.getChatWidth().getValue();
        }

        /**
         * @param val the new chat width
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatWidth(double val) {
            base.getChatWidth().setValue(val);
            return this;
        }

        /**
         * @return the focused chat height.
         *
         * @since 1.8.4
         */
        public double getChatFocusedHeight() {
            return base.getChatHeightFocused().getValue();
        }

        /**
         * @param val the new focused chat height
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatFocusedHeight(double val) {
            base.getChatHeightFocused().setValue(val);
            return this;
        }

        /**
         * @return the unfocused chat height.
         *
         * @since 1.8.4
         */
        public double getChatUnfocusedHeight() {
            return base.getChatHeightUnfocused().getValue();
        }

        /**
         * @param val the new unfocused chat height
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setChatUnfocusedHeight(double val) {
            getBase(base.getChatHeightUnfocused()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current narrator mode.
         *
         * @since 1.8.4
         */
        public String getNarratorMode() {
            String narratorKey = ((TranslatableTextContent) (base.getNarrator().getValue().getName().getContent())).getKey();
            return narratorKey.substring(narratorKey.lastIndexOf('.'));
        }

        /**
         * @param mode the mode to set the narrator to. Must be either "OFF", "ALL", "CHAT", or
         *             "SYSTEM"
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper setNarratorMode(String mode) {
            base.getNarrator().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF" -> NarratorMode.OFF;
                case "ALL" -> NarratorMode.ALL;
                case "CHAT" -> NarratorMode.CHAT;
                case "SYSTEM" -> NarratorMode.SYSTEM;
                default -> base.getNarrator().getValue();
            });
            return this;
        }

        /**
         * @return {@code true} if command suggestions are enabled
         *
         * @since 1.8.4
         */
        public boolean areCommandSuggestionsEnabled() {
            return base.getAutoSuggestions().getValue();
        }

        /**
         * @param val whether to enable command suggestions or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper enableCommandSuggestions(boolean val) {
            base.getAutoSuggestions().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if messages from blocked users are hidden.
         *
         * @since 1.8.4
         */
        public boolean areMatchedNamesHidden() {
            return base.getHideMatchedNames().getValue();
        }

        /**
         * @param val whether to hide messages of blocked users or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper enableHideMatchedNames(boolean val) {
            base.getHideMatchedNames().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if reduced debug info is enabled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isDebugInfoReduced() {
            return base.getReducedDebugInfo().getValue();
        }

        /**
         * @param val whether to enable reduced debug info or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ChatOptionsHelper reduceDebugInfo(boolean val) {
            base.getReducedDebugInfo().setValue(val);
            return this;
        }

    }

    public class AccessibilityOptionsHelper {

        public final FullOptionsHelper parent;

        public AccessibilityOptionsHelper(FullOptionsHelper FullOptionsHelper) {
            parent = FullOptionsHelper;
        }

        /**
         * @return the parent options helper.
         *
         * @since 1.8.4
         */
        public FullOptionsHelper getParent() {
            return parent;
        }

        /**
         * @return the current narrator mode.
         *
         * @since 1.8.4
         */
        public String getNarratorMode() {
            String narratorKey = ((TranslatableTextContent) (base.getNarrator().getValue().getName().getContent())).getKey();
            return narratorKey.substring(narratorKey.lastIndexOf('.'));
        }

        /**
         * @param mode the mode to set the narrator to. Must be either "OFF", "ALL", "CHAT", or
         *             "SYSTEM"
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setNarratorMode(String mode) {
            base.getNarrator().setValue(switch (mode.toUpperCase(Locale.ROOT)) {
                case "OFF" -> NarratorMode.OFF;
                case "ALL" -> NarratorMode.ALL;
                case "CHAT" -> NarratorMode.CHAT;
                case "SYSTEM" -> NarratorMode.SYSTEM;
                default -> base.getNarrator().getValue();
            });
            return this;
        }

        /**
         * @return {@code true} if subtitles are enabled.
         *
         * @since 1.8.4
         */
        public boolean areSubtitlesShown() {
            return base.getShowSubtitles().getValue();
        }

        /**
         * @param val whether to show subtitles or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper showSubtitles(boolean val) {
            base.getShowSubtitles().setValue(val);
            return this;
        }

        /**
         * @param val the new opacity for the text background
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setTextBackgroundOpacity(double val) {
            base.getTextBackgroundOpacity().setValue(val);
            return this;
        }

        /**
         * @return the opacity of the text background.
         *
         * @since 1.8.4
         */
        public double getTextBackgroundOpacity() {
            return base.getTextBackgroundOpacity().getValue();
        }

        /**
         * @return
         *
         * @since 1.8.4
         */
        public boolean isBackgroundForChatOnly() {
            return base.getBackgroundForChatOnly().getValue();
        }

        /**
         * @param val
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper enableBackgroundForChatOnly(boolean val) {
            base.getBackgroundForChatOnly().setValue(val);
            return this;
        }

        /**
         * @return the current chat opacity.
         *
         * @since 1.8.4
         */
        public double getChatOpacity() {
            return base.getChatOpacity().getValue();
        }

        /**
         * @param val the new chat opacity
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setChatOpacity(double val) {
            base.getChatOpacity().setValue(val);
            return this;
        }

        /**
         * @return the current chat line spacing.
         *
         * @since 1.8.4
         */
        public double getChatLineSpacing() {
            return base.getChatLineSpacing().getValue();
        }

        /**
         * @param val the new chat line spacing
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setChatLineSpacing(double val) {
            getBase(base.getChatLineSpacing()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current chat delay in seconds.
         *
         * @since 1.8.4
         */
        public double getChatDelay() {
            return base.getChatDelay().getValue();
        }

        /**
         * @param val the new chat delay in seconds
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setChatDelay(double val) {
            getBase(base.getChatDelay()).forceSetValue(val);
            return this;
        }

        /**
         * @return {@code true} if auto jump is enabled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isAutoJumpEnabled() {
            return base.getAutoJump().getValue();
        }

        /**
         * @param val whether to enable auto jump or not or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper enableAutoJump(boolean val) {
            base.getAutoJump().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if the toggle functionality for sneaking is enabled, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isSneakTogglingEnabled() {
            return base.getSneakToggled().getValue();
        }

        /**
         * @param val whether to enable or disable the toggle functionality for sneaking
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper toggleSneak(boolean val) {
            base.getSneakToggled().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if the toggle functionality for sprinting is enabled, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isSprintTogglingEnabled() {
            return base.getSprintToggled().getValue();
        }

        /**
         * @param val whether to enable or disable the toggle functionality for sprinting
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper toggleSprint(boolean val) {
            base.getSprintToggled().setValue(val);
            return this;
        }

        /**
         * @return the current distortion effect scale.
         *
         * @since 1.8.4
         */
        public double getDistortionEffect() {
            return base.getDistortionEffectScale().getValue();
        }

        /**
         * @param val the new distortion effect scale
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setDistortionEffect(double val) {
            getBase(base.getDistortionEffectScale()).forceSetValue(val);
            return this;
        }

        /**
         * @return the current fov effect scale.
         *
         * @since 1.8.4
         */
        public double getFovEffect() {
            return base.getFovEffectScale().getValue();
        }

        /**
         * @param val the new fov effect scale
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setFovEffect(double val) {
            base.getFovEffectScale().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if the monochrome logo is enabled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isMonochromeLogoEnabled() {
            return base.getMonochromeLogo().getValue();
        }

        /**
         * @param val whether to enable the monochrome logo or not
         * @return the current helper instance for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper enableMonochromeLogo(boolean val) {
            base.getMonochromeLogo().setValue(val);
            return this;
        }

        /**
         * @return {@code true} if lighting flashes are hidden, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean areLightningFlashesHidden() {
            return base.getHideLightningFlashes().getValue();
        }

        /**
         * @param val the new fov value
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public AccessibilityOptionsHelper setFovEffect(boolean val) {
            getBase(base.getHideLightningFlashes()).forceSetValue(val);
            return this;
        }

    }

}
