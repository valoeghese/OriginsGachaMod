package valoeghese.originsgacha;

import com.mojang.logging.LogUtils;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import valoeghese.originsgacha.network.NetworkManager;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("origins_gacha")
public class OriginsGacha
{
    public OriginsGacha() {
        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OriginsGachaConfig.CONFIG_SPEC);

        // Register core events.
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initialising Origins Gacha (Common)");

        if (FeatureFlags.DISABLED_ELYTRA.isEnabled())
        {
            DispenserBlock.registerBehavior(Items.ELYTRA, new DefaultDispenseItemBehavior());
        }

        if (FeatureFlags.ORIGIN_GACHA.isEnabled())
        {
            NetworkManager.setup();
        }
    }

    /**
     * The resource key for the origins:origin layer (the default origin layer). OriginsGacha (at least currently)
     * only works with this layer.
     */
    public static final ResourceKey<OriginLayer> ORIGIN_LAYER = ResourceKey.create(
            OriginsDynamicRegistries.LAYERS_REGISTRY,
            new ResourceLocation("origins", "origin")
    );

    /**
     * The sound for switching origin.
     */
    public static final SoundEvent SOUND_SWITCH_ORIGIN = new SoundEvent(new ResourceLocation("origins_gacha", "actions.switch_origin"));
    public static final SoundEvent SOUND_ROLL_ORIGIN = new SoundEvent(new ResourceLocation("origins_gacha", "actions.rolling_origin"));
    public static final SoundEvent SOUND_UNLOCK_ORIGIN = new SoundEvent(new ResourceLocation("origins_gacha", "events.receive_rolled_origin"));

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Feature flags for Origins Gacha. Edit these to enable or disable features of the mod.
     */
    public enum FeatureFlags {
        /**
         * Whether preventing players from equipping the elytra should be enabled.
         */
        DISABLED_ELYTRA(true),
        /**
         * Whether easter eggs should be enabled.
         */
        EASTER_EGGS(true),
        /**
         * Whether the main system of unlocking and switching origins should be enabled.
         */
        ORIGIN_GACHA(true);

        FeatureFlags(boolean enabled) {
            this.enabled = enabled;
        }

        private final boolean enabled;

        /**
         * Get whether this feature flag is enabled.
         * @return whether this feature flag is enabled.
         */
        public boolean isEnabled() {
            return this.enabled;
        }
    }
}
