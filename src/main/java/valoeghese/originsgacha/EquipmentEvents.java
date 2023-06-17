package valoeghese.originsgacha;

import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import valoeghese.originsgacha.event.PlayerTryEquipEvent;

@Mod.EventBusSubscriber
public class EquipmentEvents {
	@SubscribeEvent
	public static void onEquip(PlayerTryEquipEvent event) {
		if (event.getStack().getItem() == Items.ELYTRA) {
			event.setCanceled(true);
		}
	}
}
