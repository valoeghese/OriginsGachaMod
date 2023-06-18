package valoeghese.originsgacha.screens.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.function.Function;

/**
 * A collection of vertex formats that can be used for drawing.
 */
public final class VertexFormats {
	/**
	 * Create and start a position-colour drawing instance for the specified mode.
	 * @param mode the mode to draw in.
	 * @return an {@link AutoCloseable autocloseable} instance of a position-colour vertex format for building.
	 */
	public static PositionColour drawPositionColour(VertexFormat.Mode mode) {
		return draw(mode, PositionColour::new);
	}

	/**
	 * Create and start a position-colour-texture-lightmap drawing instance for the specified mode.
	 * @param mode the mode to draw in.
	 * @return an {@link AutoCloseable autocloseable} instance of a position-colour-texture-lightmap vertex format for building.
	 */
	public static PositionColourTexLightmap drawPositionColourTexLightmap(VertexFormat.Mode mode) {
		return draw(mode, PositionColourTexLightmap::new);
	}

	/**
	 * Create and start a drawing instance for the specified mode and vertex format.
	 * @param mode the mode to draw in.
	 * @param ctr the constructor for the vertex format to use.
	 * @param <T> the type of vertex format to use.
	 * @return an {@link AutoCloseable autocloseable} instance of the vertex format for building.
	 */
	private static <T extends VertexBuilderNode<T> & VertexBuilderNode.Root> T draw(
			VertexFormat.Mode mode,
			Function<BufferBuilder, T> ctr
	) {
		// Get vanilla drawing instances.
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder builder = tesselator.getBuilder();

		// Create the vertex format from the vanilla builder
		T format = ctr.apply(builder);
		builder.begin(mode, format.format());

		// Return the format.
		return format;
	}

	/**
	 * The {@linkplain DefaultVertexFormat#POSITION_COLOR}, as a restricted builder via a chain of nodes.
	 */
	public static class PositionColour
			extends VertexBuilderNode.Position<
				VertexBuilderNode.Colour<
					VertexBuilderNode.Terminal<PositionColour>,
					PositionColour
				>,
				PositionColour
			>
			implements VertexBuilderNode.Root
	{
		PositionColour(BufferBuilder builder) {
			super(new Colour<>(new Terminal<PositionColour>(builder), builder), builder);
			this.updateRoot(this);
		}

		@Override
		public VertexFormat format() {
			return DefaultVertexFormat.POSITION_COLOR;
		}

		@Override
		public void close() {
			Tesselator.getInstance().end();
		}
	}

	/**
	 * The {@linkplain DefaultVertexFormat#POSITION_COLOR_TEX_LIGHTMAP}, as a restricted builder via a chain of nodes.
	 */
	public static class PositionColourTexLightmap
			extends VertexBuilderNode.Position<
				VertexBuilderNode.Colour<
					VertexBuilderNode.Texture<
						VertexBuilderNode.Lightmap<
							VertexBuilderNode.Terminal<PositionColourTexLightmap>,
							PositionColourTexLightmap
						>,
						PositionColourTexLightmap
					>,
					PositionColourTexLightmap
				>,
				PositionColourTexLightmap
			>
			implements VertexBuilderNode.Root
	{
		PositionColourTexLightmap(BufferBuilder builder) {
			super(new Colour<>(new Texture<>(new Lightmap<>(new Terminal<PositionColourTexLightmap>(builder), builder), builder), builder), builder);
			this.updateRoot(this);
		}

		@Override
		public VertexFormat format() {
			return DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP;
		}

		@Override
		public void close() {
			Tesselator.getInstance().end();
		}
	}
}
