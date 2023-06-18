package valoeghese.originsgacha;

import com.mojang.logging.LogUtils;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("origins_gacha")
public class OriginsGacha
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public OriginsGacha()
    {
        // Register core events.
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("Initialising Origins Gacha (Common)");

        DispenserBlock.registerBehavior(Items.ELYTRA, new DefaultDispenseItemBehavior());
    }
}
