package valoeghese.originsgacha;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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

	/**
	 * The keybind for opening the origin select screen.
	 */
	public static final KeyMapping SELECT_ORIGIN = new KeyMapping(
			"key.origins_gacha.select",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_GRAVE_ACCENT,
			"category.origins"
	);

	@SubscribeEvent
	public static void onClientTick(final TickEvent.ClientTickEvent event) {
		if (OriginsGacha.FeatureFlags.ORIGIN_GACHA.isEnabled())
		{
			if (event.phase == TickEvent.Phase.END) {
				while (SELECT_ORIGIN.consumeClick()) {
					if (!(Minecraft.getInstance().screen instanceof OriginSelectScreen)) {
						OriginSelectScreen originSelectScreen = new OriginSelectScreen();

						if (originSelectScreen.canDisplay()) {
							Minecraft.getInstance().setScreen(originSelectScreen);
						}
					}
				}
			}
		}
	}

	// run last as an inspector
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingTick(final RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
		if (OriginsGacha.FeatureFlags.EASTER_EGGS.isEnabled())
		{
			if (!event.isCanceled()) {
				String name = ChatFormatting.stripFormatting(event.getEntity().getName().getString());

				if ("dan".equals(name) || "Dan".equals(name)) {
					event.getPoseStack().pushPose();
					event.getPoseStack().scale(0.1f, 0.1f, 0.1f);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingTick(final RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
		if (OriginsGacha.FeatureFlags.EASTER_EGGS.isEnabled())
		{
			String name = ChatFormatting.stripFormatting(event.getEntity().getName().getString());

			if ("dan".equals(name) || "Dan".equals(name)) {
				event.getPoseStack().popPose();
			}
		}
	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
			LOGGER.info("Registering Origins Gacha Keybinds");

			if (OriginsGacha.FeatureFlags.ORIGIN_GACHA.isEnabled())
			{
				event.register(SELECT_ORIGIN);
			}
		}
	}
}
