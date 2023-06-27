package valoeghese.originsgacha.screens.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.renderer.GameRenderer;
import valoeghese.originsgacha.util.VertexFormats;

/**
 * The component for a gacha wheel that 'spins' vertically and lands on the element.
 */
public class GachaWheel implements Widget {
	public GachaWheel(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	private final int x;
	private final int y;
	private final int width;
	private final int height;

	private double offset;

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		final int spacing = 2;

		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		try (VertexFormats.PositionColour builder = VertexFormats.drawPositionColour(VertexFormat.Mode.QUADS)) {
			// black rectangle for most of the space
			builder.position(this.x, this.y).colour(0.0f, 0.0f, 0.0f).endVertex()
					.position(this.x, this.y + this.height).colour(0.0f, 0.0f, 0.0f).endVertex()
					.position(this.x + this.width, this.y + this.height).colour(0.0f, 0.0f, 0.0f).endVertex()
					.position(this.x + this.width, this.y).colour(0.0f, 0.0f, 0.0f).endVertex();
		}
	}
}
