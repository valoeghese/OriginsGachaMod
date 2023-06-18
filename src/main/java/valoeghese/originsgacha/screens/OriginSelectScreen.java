package valoeghese.originsgacha.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import valoeghese.originsgacha.ClientEvents;
import valoeghese.originsgacha.screens.util.VertexFormats;

public class OriginSelectScreen extends Screen {
	public OriginSelectScreen() {
		super(Component.translatable("screens.origins_gacha.select"));
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		super.render(stack, mouseX, mouseY, partialTick);

		double centreX = this.width / 2.0;
		double centreY = this.height / 2.0;
		double size = this.height / 3.0;
		final int nSegments = 32;
		final double theta = 2.0 * Math.PI / nSegments;

		try (VertexFormats.PositionColour builder = VertexFormats.drawPositionColour(VertexFormat.Mode.TRIANGLES)) {
			for (int i = 0; i < nSegments; i++) {
				double angle = theta * i;

				builder.position(centreX, centreY)
						.colour(0.5f, 0.5f, 0.5f, 0.5f)
						.endVertex();

				builder.position(centreX + size * Math.cos(angle + theta), centreY + size * Math.sin(angle + theta))
						.colour(0.5f, 0.5f, 0.5f, 0.5f)
						.endVertex();

				builder.position(centreX + size * Math.cos(angle), centreY + size * Math.sin(angle))
						.colour(0.5f, 0.5f, 0.5f, 0.5f)
						.endVertex();
			}
		}
	}

	@Override
	public void tick() {
		if (!isDown(ClientEvents.SELECT_ORIGIN)) {
			System.out.println("closing");
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
