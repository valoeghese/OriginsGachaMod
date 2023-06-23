package valoeghese.originsgacha;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import valoeghese.originsgacha.capabilities.UnlockedOrigins;

@Mod.EventBusSubscriber
public class RegistryEvents {
	@SubscribeEvent
	public static void onCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player player) {
			event.addCapability(UnlockedOrigins.ID, new UnlockedOrigins(player));
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryModEvents {
		@SubscribeEvent
		public static void onCapabilityRegister(RegisterCapabilitiesEvent event) {
			event.register(UnlockedOrigins.class);
		}
	}
}
