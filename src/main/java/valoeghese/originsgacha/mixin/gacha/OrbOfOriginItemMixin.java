package valoeghese.originsgacha.mixin.gacha;

import io.github.apace100.origins.content.OrbOfOriginItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import valoeghese.originsgacha.OriginsGacha;

/**
 * Mixin to disable normal orb of origin use in favour of the gacha system.
 */
@Mixin(OrbOfOriginItem.class)
public class OrbOfOriginItemMixin {
	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	private void onUse(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand,
					   CallbackInfoReturnable<@NotNull InteractionResultHolder<ItemStack>> info) {
		if (OriginsGacha.FeatureFlags.ORIGIN_GACHA.isEnabled())
		{
			ItemStack stack = player.getItemInHand(hand);
			info.setReturnValue(InteractionResultHolder.fail(stack));
		}
	}
}
