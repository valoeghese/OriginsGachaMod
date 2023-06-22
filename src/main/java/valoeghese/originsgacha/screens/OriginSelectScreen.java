package valoeghese.originsgacha.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import valoeghese.originsgacha.ClientEvents;
import valoeghese.originsgacha.screens.util.VertexFormats;

public class OriginSelectScreen extends Screen {
	public OriginSelectScreen() {
		super(Component.translatable("screens.origins_gacha.select"));
	}

	private double scaleFactor = 0.05;
	private long lastScaleTime = System.currentTimeMillis();

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		super.render(stack, mouseX, mouseY, partialTick);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		double centreX = this.width / 2.0;
		double centreY = this.height / 2.0;
		double size = this.scaleFactor * this.height / 2.5;
		double innerButtonSize = size * 0.2;
		double innerEdgeSize = size * 0.25;

		this.drawCircles(centreX, centreY, size, innerButtonSize, innerEdgeSize);

		RenderSystem.disableBlend();

		// scale up
		if (this.scaleFactor < 1) {
			long currentTime = System.currentTimeMillis();
			// each 1 = 1 tick (50ms)
			double diffTime = (currentTime - this.lastScaleTime) / 50.0;
			this.lastScaleTime = currentTime;

			final double baseChangeRate = 0.2;
			double scaleChange = baseChangeRate - 0.01 * baseChangeRate * Math.exp(2 * this.scaleFactor);
			this.scaleFactor += scaleChange * diffTime;

			if (this.scaleFactor > 1) {
				this.scaleFactor = 1;
			}
		}
	}

	private void drawCircles(double centreX, double centreY, double size, double innerButtonSize, double innerEdgeSize) {
		final int nSegments = 64;
		final double theta = 2.0 * Math.PI / nSegments;
		float shade = 0.2f;

		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		try (VertexFormats.PositionColour builder = VertexFormats.drawPositionColour(VertexFormat.Mode.TRIANGLES)) {
			// Inner Circle
			for (int i = 0; i < nSegments; i++) {
				double angle = theta * i;

				builder.position(centreX, centreY)
						.colour(shade, shade, shade, 0.5f)
						.endVertex();

				builder.position(centreX + innerButtonSize * Math.cos(angle + theta), centreY + innerButtonSize * Math.sin(angle + theta))
						.colour(shade, shade, shade, 0.5f)
						.endVertex();

				builder.position(centreX + innerButtonSize * Math.cos(angle), centreY + innerButtonSize * Math.sin(angle))
						.colour(shade, shade, shade, 0.5f)
						.endVertex();
			}

			// Outer Circle
			for (int i = 0; i < nSegments; i++) {
				double angle = theta * i;

				final double cos = Math.cos(angle);
				final double sin = Math.sin(angle);
				final double cosNext = Math.cos(angle + theta);
				final double sinNext = Math.sin(angle + theta);

				// 2, 1, 0

				builder.position(centreX + innerEdgeSize * cos, centreY + innerEdgeSize * sin)
						.colour(shade, shade, shade, 0.5f)
						.endVertex();

				builder.position(centreX + size * cosNext, centreY + size * sinNext)
						.colour(shade, shade, shade, 0.5f)
						.endVertex();

				builder.position(centreX + size * cos, centreY + size * sin)
						.colour(shade, shade, shade, 0.5f)
						.endVertex();

				// 3, 1, 2

				builder.position(centreX + innerEdgeSize * cosNext, centreY + innerEdgeSize * sinNext)
						.colour(shade, shade, shade, 0.5f)
						.endVertex();

				builder.position(centreX + size * cosNext, centreY + size * sinNext)
						.colour(shade, shade, shade, 0.5f)
						.endVertex();

				builder.position(centreX + innerEdgeSize * cos, centreY + innerEdgeSize * sin)
						.colour(shade, shade, shade, 0.5f)
						.endVertex();
			}
		}
	}

	@Override
	public void tick() {
		if (!isDown(ClientEvents.SELECT_ORIGIN)) {
			this.onClose();
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	/**
	 * Check if the given key for the key mapping is down. This is preferred over .isDown() in screens due to isDown
	 * only working is this.minecraft.screen == null.
	 * @param mapping the mapping to check.
	 * @return if the key mapping is down.
	 */
	private static boolean isDown(KeyMapping mapping) {
		if (mapping.isUnbound()) {
			return false;
		}

		InputConstants.Key key = mapping.getKey();
		long window = Minecraft.getInstance().getWindow().getWindow();
		int value = key.getValue();

		if (key.getType() == InputConstants.Type.KEYSYM) {
			return GLFW.glfwGetKey(window, value) != GLFW.GLFW_RELEASE;
		} else if (key.getType() == InputConstants.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(window, value) != GLFW.GLFW_RELEASE;
		}

		return false;
	}
}
