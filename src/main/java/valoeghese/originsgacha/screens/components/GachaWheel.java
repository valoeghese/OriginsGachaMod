package valoeghese.originsgacha.screens.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.renderer.GameRenderer;
import valoeghese.originsgacha.util.VertexFormats;

/**
 * A gacha wheel that spins vertically and lands on a target element.
 */
public class GachaWheel implements Widget {
	public GachaWheel(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		int numElementsNoOffset = (height - width) / (width + SPACING);
		int usedHeightNoOffset = (width + SPACING) * numElementsNoOffset + width;
		this.offset = this.defaultOffset = (height - usedHeightNoOffset) / 2;
	}

	private final int x;
	private final int y;
	private final int width;
	private final int height;

	private final int defaultOffset;
	private int offset;

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		try (VertexFormats.PositionColour builder = VertexFormats.drawPositionColour(VertexFormat.Mode.QUADS)) {
			// The 'rail' upon which the wheel rolls. With left highlight and right shadow.
			this.drawRect(builder, this.x, this.y, 1, this.height, HIGHLIGHT_SHADE);
			this.drawRect(builder, this.x + 1, this.y, this.width - 2, this.height, OUTER_SHADE);
			this.drawRect(builder, this.x + this.width - 1, this.y, 1, this.height, INNER_SHADE);

			// white squares down the height.
			int startY = this.offset;

			if (startY > SPACING) {
				startY -= this.width + SPACING;
			}

			final float leftBoxEdge = this.x + 2;
			final float rightBoxEdge = this.x + this.width - 2;
			final float boxWidth = rightBoxEdge - leftBoxEdge;

			for(; startY < this.height; startY += this.width + SPACING) {
				final float topBoxEdge = startY + 2;
				final float bottomBoxEdge = startY + this.width - 2;

				// shading
				this.drawRect(builder, leftBoxEdge, topBoxEdge, boxWidth, 1, SHADOW_SHADE);
				this.drawRect(builder, leftBoxEdge, topBoxEdge + 1, 1, boxWidth - 1, SHADOW_SHADE);
				// highlighting
				this.drawRect(builder, leftBoxEdge + 1, bottomBoxEdge - 1, boxWidth - 1, 1, HIGHLIGHT_SHADE);
				this.drawRect(builder, rightBoxEdge - 1, topBoxEdge + 1, 1, boxWidth - 2, HIGHLIGHT_SHADE);

				// inner
				this.drawRect(builder, leftBoxEdge + 1, topBoxEdge + 1, boxWidth - 2, boxWidth - 2, INNER_SHADE);
			}
		}
	}

	private void drawRect(VertexFormats.PositionColour builder, float x, float y, float w, float h, float shade) {
		// draw anti-clockwise due to how minecraft culls faces.
		builder.position(x, y).colour(shade, shade, shade).endVertex()
				.position(x, y + h).colour(shade, shade, shade).endVertex()
				.position(x + w, y + h).colour(shade, shade, shade).endVertex()
				.position(x + w, y).colour(shade, shade, shade).endVertex();
	}

	private static final float SHADOW_SHADE = 0.14f;
	private static final float HIGHLIGHT_SHADE = 1.0f;
	private static final float INNER_SHADE = 0.545f;
	private static final float OUTER_SHADE = 0.776f;

	private static final int SPACING = 2;
}
