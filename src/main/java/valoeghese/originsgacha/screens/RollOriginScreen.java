package valoeghese.originsgacha.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import valoeghese.originsgacha.screens.components.ButtonBuilder;

/**
 * Screen for rolling the next origin to unlock.
 * Note: the client already knows which origin is going to be picked when rolling.
 */
public class RollOriginScreen extends Screen {
	public RollOriginScreen() {
		super(Component.translatable("screens.origins_gacha.roll_origin"));
	}

	@Override
	protected void init() {
		this.addWidget(
				new ButtonBuilder()
						.position(2 * this.width / 3, this.height / 2 - 12)
						.message(Component.translatable("buttons.origin_gacha.roll"))
						.build()
		);

		this.addWidget(
				new ButtonBuilder()
						.position(2 * this.width / 3, this.height / 2 + 12)
						.message(CommonComponents.GUI_CANCEL)
						.action(this::onClose)
						.build()
		);
	}

	@Override
	public void renderBackground(PoseStack pPoseStack, int pVOffset) {
		this.renderDirtBackground(pVOffset);
	}
}
