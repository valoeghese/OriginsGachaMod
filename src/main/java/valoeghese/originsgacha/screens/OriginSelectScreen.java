package valoeghese.originsgacha.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import valoeghese.originsgacha.ClientEvents;
import valoeghese.originsgacha.OriginsGacha;
import valoeghese.originsgacha.screens.util.VertexFormats;
import valoeghese.originsgacha.util.Division;

public class OriginSelectScreen extends Screen {
	public OriginSelectScreen() {
		super(Component.translatable("screens.origins_gacha.select"));

		Player player = Minecraft.getInstance().player;
		assert player != null; // appease the static code analysis

		// Get the player's current origin.
		IOriginContainer originContainer = player.getCapability(OriginsAPI.ORIGIN_CONTAINER).resolve().orElseThrow(
				() -> new IllegalStateException("Player does not have origin container?!")
		);

		ResourceKey<Origin> currentOrigin = originContainer.getOrigin(OriginsGacha.ORIGIN_LAYER);
		Origin origin = OriginsAPI.getOriginsRegistry().get(currentOrigin);

		if (origin == null) {
			throw new IllegalStateException("Unknown Origin: " + currentOrigin.location());
		}

		LOGGER.info(origin.getName().getString());
		// Get all unlocked origins.
	}

	private double scaleFactor = 0.05;
	private long lastScaleTime = System.currentTimeMillis();

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		super.render(stack, mouseX, mouseY, partialTick);
		///this.itemRenderer.renderGuiItem();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		double centreX = this.width / 2.0;
		double centreY = this.height / 2.0;
		double size = this.scaleFactor * this.height / 2.5;
		double innerButtonSize = size * 0.2;
		double innerEdgeSize = size * 0.25;

		float[] mousePosPolar = rect2polar(mouseX - (float)centreX, mouseY - (float)centreY);
		int selectedSector = -1;

		if (mousePosPolar[0] < innerButtonSize) {
			selectedSector = 8;
		} else if (mousePosPolar[0] >= innerEdgeSize && mousePosPolar[0] < size) {
			selectedSector = SECTORS.get(mousePosPolar[1]);
		}

		this.drawCircles(centreX, centreY, size, innerButtonSize, innerEdgeSize, selectedSector);

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

	private void drawCircles(double centreX, double centreY, double size, double innerButtonSize, double innerEdgeSize,
							 int highlightedSector) {
		final int nSectors = 64;
		final double theta = 2.0 * Math.PI / nSectors;

		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		try (VertexFormats.PositionColour builder = VertexFormats.drawPositionColour(VertexFormat.Mode.TRIANGLES)) {
			float shade = highlightedSector == 8 ? 1 : 0.2f;

			// Inner Circle
			for (int i = 0; i < nSectors; i++) {
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
			for (int i = 0; i < nSectors; i++) {
				double angle = theta * i;
				final int sector = SECTORS.get(angle);

				shade = sector == highlightedSector ? 1 : ((sector & 1) == 0 ? 0.2f : 0.4f);

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

	private static final Logger LOGGER = LogUtils.getLogger();

	private static final Division<Integer> SECTORS = new Division<Integer>()
			.addSection(2 * Math.PI * (6.0/8.0), 0)
			.addSection(2 * Math.PI * (7.0/8.0), 1)
			.addSection(2 * Math.PI * (8.0/8.0), 2)
			.addSection(0, 2)
			.addSection(2 * Math.PI * (1.0/8.0), 3)
			.addSection(2 * Math.PI * (2.0/8.0), 4)
			.addSection(2 * Math.PI * (3.0/8.0), 5)
			.addSection(2 * Math.PI * (4.0/8.0), 6)
			.addSection(2 * Math.PI * (5.0/8.0), 7);

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

	/**
	 * Converts the given coordinates from rectangular (cartesian) to polar space.
	 * @param x the x coordinate in cartesian coordinates.
	 * @param y the y coordinate in cartesian coordinates.
	 * @return a size-2 array containing [r, theta], where r is the distance from the origin, and theta is the angle
	 * clockwise from the horizontal, in radians. If the case that r is 0, theta is also 0.
	 */
	private static float[] rect2polar(float x, float y) {
		float r = (float) Math.sqrt(x * x + y * y);
		float theta = (float) Math.atan2(y, x);

		if (theta < 0) {
			theta += 2 * Math.PI; // Adjusted to ensure theta is between 0 and 2 pi
		}

		return new float[] {r, theta};
	}
}
