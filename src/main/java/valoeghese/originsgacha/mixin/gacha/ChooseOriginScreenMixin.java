package valoeghese.originsgacha.mixin.gacha;

import io.github.apace100.origins.screen.ChooseOriginScreen;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import valoeghese.originsgacha.OriginsGacha;
import valoeghese.originsgacha.capabilities.IUnlockedOrigins;

import java.util.List;
import java.util.Objects;

/**
 * Edit the choose origin screen to only have the randomly selected initial unlocked origins.
 */
@Mixin(value = ChooseOriginScreen.class, remap = false)
public abstract class ChooseOriginScreenMixin extends Screen {
	protected ChooseOriginScreenMixin(Component pTitle) {
		super(pTitle);
	}

	@Shadow @Final private List<Holder<Origin>> originSelection;

	@Shadow @Final private List<Holder<OriginLayer>> layerList;
	@Shadow @Final private int currentLayerIndex;

	private boolean origins_gacha_modifiedChooseOriginScreen;

	@Inject(at = @At("HEAD"), method = "getCurrentOriginInternal")
	private void onGetCurrentOrigin(CallbackInfoReturnable<Holder<Origin>> cir) {
		if (OriginsGacha.FeatureFlags.ORIGIN_GACHA.isEnabled() && !this.origins_gacha_modifiedChooseOriginScreen)
		{
			// Holder.is(ResourceKey) in Holder.Reference uses reference comparison so we need to compare resource location
			if (this.layerList.get(this.currentLayerIndex).is(OriginsGacha.ORIGIN_LAYER.location())) {
				System.out.println("Modifying Origin Layer Selection."); //debug
				assert this.minecraft != null; // hm yes very safety
				assert this.minecraft.player != null; // the player should never be null in game. Why tf would it be null?
				IUnlockedOrigins unlockedOrigins = IUnlockedOrigins.getUnlockedOrigins(this.minecraft.player);

				// remove origins if origin is not unlocked yet
				this.originSelection.removeIf(originHolder -> !unlockedOrigins.hasOrigin(
						Objects.requireNonNull(originHolder.unwrapKey().orElse(null), "Missing Origin Key")
				));

				// TODO stop cheesing on server side
				// TODO if origin list is empty wait until server sends us the data
			}

			this.origins_gacha_modifiedChooseOriginScreen = true;
		}
	}
}
