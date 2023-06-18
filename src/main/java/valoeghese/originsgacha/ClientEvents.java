package valoeghese.originsgacha;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import valoeghese.originsgacha.screens.OriginSelectScreen;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
	// Directly reference a slf4j logger
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final KeyMapping SELECT_ORIGIN = new KeyMapping(
			"key.origins_gacha.select",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_GRAVE_ACCENT,
			"category.origins"
	);

	@SubscribeEvent
	public static void onClientTick(final TickEvent.ClientTickEvent event) {
		while (SELECT_ORIGIN.consumeClick()) {
			if (!(Minecraft.getInstance().screen instanceof OriginSelectScreen)) {
				Minecraft.getInstance().setScreen(new OriginSelectScreen());
			}
		}
	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void clientSetup(final FMLClientSetupEvent event) {
			LOGGER.info("Initialising Origins Gacha (Client)");
		}

		@SubscribeEvent
		public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
			event.register(SELECT_ORIGIN);
		}
	}
}
