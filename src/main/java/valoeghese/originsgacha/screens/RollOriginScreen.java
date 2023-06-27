package valoeghese.originsgacha.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import valoeghese.originsgacha.screens.components.ButtonBuilder;
import valoeghese.originsgacha.screens.components.GachaWheel;

/**
 * Screen for rolling the next origin to unlock.
 * Note: the client already knows which origin is going to be picked when rolling.
 */
public class RollOriginScreen extends Screen {
	public RollOriginScreen() {
		super(Component.translatable("screens.origins_gacha.roll_origin"));
	}

	private GachaWheel wheel;

	@Override
	protected void init() {
		final int wheelHalfWidth = this.width / 12;
		this.wheel = this.addRenderableOnly(new GachaWheel(this.width / 3 - wheelHalfWidth, 0, wheelHalfWidth * 2, this.height));

		this.addRenderableWidget(
				new ButtonBuilder()
						.position(2 * this.width / 3, this.height / 2 - 12)
						.message(Component.translatable("buttons.origins_gacha.roll"))
						.centered()
						.build()
		);

		this.addRenderableWidget(
				new ButtonBuilder()
						.position(2 * this.width / 3, this.height / 2 + 12)
						.message(CommonComponents.GUI_CANCEL)
						.action(this::onClose)
						.centered()
						.build()
		);
	}

	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		this.renderDirtBackground(0);
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
	}
}
