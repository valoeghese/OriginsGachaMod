package valoeghese.originsgacha.impl;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import valoeghese.originsgacha.event.PlayerTryEquipEvent;

/**
 * Implementations of complex mixin logic.
 */
public class PlayerTryEquipCalls {
	/**
	 * Called when the player clicks in an inventory.
	 * @param menu the menu in which the player clicked.
	 * @param player the player.
	 * @param pClickType
	 * @param slotId the id of the slot clicked.
	 * @param info the info allowing this to be cancelled.
	 */
	public static void onInventoryClick(InventoryMenu menu, Player player, ClickType clickType, int slotId, CallbackInfo info) {
		// Check whether this is legal.
		if (slotId < 0) {
			return;
		}

		Slot slot = menu.getSlot(slotId);

		// Equipment slot is an anonymous class in InventoryMenu
		// This slot is characterised by its max stack size of 1.
		boolean isEquipmentSlot = slot.getMaxStackSize() == 1;

		if (isEquipmentSlot) {
			if (MinecraftForge.EVENT_BUS.post(
					new PlayerTryEquipEvent(
							player,
							menu.getCarried(),
							PlayerTryEquipEvent.Reason.PLACE_IN_SLOT
					))) {
				info.cancel();
			}
		}

		// Alternatively, any shift click of an ArmorItem or ElytraItem should trigger.
		if (clickType == ClickType.QUICK_MOVE) {
			// removing armour.
			if (isEquipmentSlot) {
				if (MinecraftForge.EVENT_BUS.post(
						new PlayerTryEquipEvent(
								player,
								ItemStack.EMPTY,
								PlayerTryEquipEvent.Reason.PLACE_IN_SLOT
						))) {
					info.cancel();
				}
			}
			// equipping armour
			else if (MinecraftForge.EVENT_BUS.post(
					new PlayerTryEquipEvent(
							player,
							slot.getItem(),
							PlayerTryEquipEvent.Reason.PLACE_IN_SLOT
					))) {
				info.cancel();
			}
		}
	}
}
