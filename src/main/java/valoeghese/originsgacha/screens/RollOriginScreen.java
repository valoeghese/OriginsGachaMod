package valoeghese.originsgacha.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.screen.OriginDisplayScreen;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import valoeghese.originsgacha.OriginsGacha;
import valoeghese.originsgacha.capabilities.IUnlockedOrigins;
import valoeghese.originsgacha.network.NetworkManager;
import valoeghese.originsgacha.network.packet.C2SRollOriginPacket;
import valoeghese.originsgacha.screens.components.ButtonBuilder;
import valoeghese.originsgacha.screens.components.GachaWheel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

/**
 * Screen for rolling the next origin to unlock.
 * Note: the client already knows which origin is going to be picked when rolling.
 */
// TODO copy gacha wheel data across resizes so it doesn't just stop the roll
public class RollOriginScreen extends Screen {
	public RollOriginScreen(IntSupplier orbsOfOrigin, int requiredOrbs) {
		super(Component.translatable("screens.origins_gacha.roll_origin"));

		this.orbsOfOrigin = orbsOfOrigin;
		this.requiredOrbs = requiredOrbs;
	}

	private final IntSupplier orbsOfOrigin;
	private final int requiredOrbs;
	private int currentOrbs;

	private GachaWheel wheel;
	private Button cancelButton;
	private Component renderedText = Component.empty();

	@Override
	protected void init() {
		final int wheelCentrePos = (2 * this.width / 3 - 100) / 2;

		// Create Wheel
		GachaWheel wheel = new GachaWheel(this.itemRenderer, wheelCentrePos - 20, 0, 40, this.height);

		if (this.wheel != null && this.wheel.isRolling()) {
			wheel.copyFrom(this.wheel);
		}

		this.wheel = this.addRenderableOnly(wheel);

		// if rolling, data has been copied
		// if not, initialise the data.
		if (!this.wheel.isRolling()) {
			this.initWheel();
		}

		this.currentOrbs = this.orbsOfOrigin.getAsInt();

		// add roll button if no text to render
		if (this.renderedText.getContents() == ComponentContents.EMPTY) {
			this.addRenderableWidget(
					new ButtonBuilder()
							.position(2 * this.width / 3, this.height / 2 - 12)
							.message(Component.translatable("buttons.origins_gacha.roll"))
							.centered()
							.setupIf(this.currentOrbs < this.requiredOrbs, builder -> builder
									.tooltip(Component.translatable("buttons.origins_gacha.roll.not_enough", this.requiredOrbs - this.currentOrbs, this.currentOrbs, this.requiredOrbs))
									.disable()
							)
							.setupIf(this.wheel.isRolling(), ButtonBuilder::disable)
							.action(bn -> {
								// disable buttons
								bn.active = false;
								this.cancelButton.active = false;

								// send roll packet to server
								NetworkManager.sendToServer(new C2SRollOriginPacket());
							})
							.build()
			);
		}

		this.cancelButton = this.addRenderableWidget(
				new ButtonBuilder()
						.position(2 * this.width / 3, this.height / 2 + 12)
						.message(CommonComponents.GUI_CANCEL)
						.setupIf(this.wheel.isRolling(), ButtonBuilder::disable)
						.action(this::onClose)
						.centered()
						.build()
		);
	}

	/**
	 * Play the unlock origin rolling animation and land on the given origin.
	 * @param origin the origin to land on.
	 */
	public void rollOrigin(ResourceKey<Origin> origin) {
		assert this.minecraft != null : "We must be initialised";

		// play rolling origin sound
		this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(OriginsGacha.SOUND_ROLL_ORIGIN, 1.0F));

		// roll the wheel to land on the given origin
		Origin originInstance = OriginsAPI.getOriginsRegistry().get(origin);

		if (originInstance == null) {
			// add chat message saying that error: the origin was not found
			this.minecraft.gui.getChat().addMessage(Component.literal("Error: unlocked origin not found!"));
			this.onClose();
			return;
		}

		ItemStack stack = originInstance.getIcon();

		if (!this.wheel.roll(stack, () -> this.afterRoll(origin))) {
			// add chat message saying that error: the wheel does not contain the unlocked origin
			this.minecraft.gui.getChat().addMessage(Component.literal("Error: wheel does not contain the unlocked origin!"));
			this.onClose();
		}
	}

	private void afterRoll(ResourceKey<Origin> origin) {
		assert this.minecraft != null : "We must be initialised";

		// play unlocked origin sound
		this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(OriginsGacha.SOUND_UNLOCK_ORIGIN, 1.0F));

		NewOriginScreen screen = new NewOriginScreen(
				OriginsAPI.getOriginsRegistry().getHolderOrThrow(origin),
				OriginsAPI.getLayersRegistry().getHolderOrThrow(OriginsGacha.ORIGIN_LAYER)
		);

		this.minecraft.setScreen(screen);
	}

	/**
	 * Initialise the elements within the wheel.
	 */
	private void initWheel() {
		assert this.minecraft != null; // "i know what i am doing"
		assert this.minecraft.player != null;

		IUnlockedOrigins origins = IUnlockedOrigins.getUnlockedOrigins(this.minecraft.player);

		// add all not-yet-unlocked origins to the wheel
		OriginLayer layer = OriginsAPI.getLayersRegistry().get(OriginsGacha.ORIGIN_LAYER);

		if (layer == null) {
			this.minecraft.setScreen(new ErrorScreen(
					Component.translatable("screens.origins_gacha.no_origins_layer"),
					Component.translatable("screens.origins_gacha.no_origins_layer.description")
			));

			return;
		}

		List<Holder<Origin>> enabledOrigins = new ArrayList<>();

		layer.origins().forEach(origin -> {
			if (origin.get().isChoosable()) {
				enabledOrigins.add(origin);

				if (!origins.hasOrigin(origin.unwrapKey().orElseThrow(() -> new IllegalStateException("Origin has no key!")))) {
					this.wheel.addElement(origin.get().getIcon());
				}
			}
		});

		// if the wheel is empty, add all origins, and set the text to "You have unlocked all origins!"
		if (this.wheel.isEmpty()) {
			enabledOrigins.forEach(origin -> this.wheel.addElement(origin.get().getIcon()));
			this.renderedText = Component.translatable("screens.origins_gacha.roll_origin.all_origins_unlocked");
		}
	}

	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		this.renderDirtBackground(0);
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

		drawCenteredString(pPoseStack, this.font, this.renderedText, 2 * this.width / 3,this.height / 2 - 12, 0xDFDFDF);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return this.cancelButton == null || this.cancelButton.active;
	}
}
