package valoeghese.originsgacha.mixin.equip;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import valoeghese.originsgacha.impl.PlayerTryEquipCalls;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
	@Inject(at = @At("HEAD"), cancellable = true, method = "doClick")
	private void onDoClick(int pSlotId, int pButton, ClickType pClickType, Player pPlayer, CallbackInfo info) {
		// only inventory menu
		AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;

		if (menu instanceof InventoryMenu iMenu) {
			PlayerTryEquipCalls.onInventoryClick(iMenu, pPlayer, pSlotId, info);
		}
	}
}
