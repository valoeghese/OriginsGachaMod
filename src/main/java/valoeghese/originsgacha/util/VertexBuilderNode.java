package valoeghese.originsgacha.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

public abstract class VertexBuilderNode<T extends VertexBuilderNode<T> & VertexBuilderNode.Root> {
	/**
	 * Construct a generic vertex builder node. The base constructor upon which all nodes build.
	 * @param builder the buffer builder to be built upon.
	 */
	VertexBuilderNode(BufferBuilder builder) {
		this.builder = builder;
	}

	protected T root;
	protected final BufferBuilder builder;

	/**
	 * Set the root of this node and its children.
	 * @param root the root of the chain.
	 */
	void updateRoot(T root) {
		this.root = root;
	}

	/**
	 * Class representing the terminal part of the chain, leading into the next lot.
	 * @param <T> the type of root to return to.
	 */
	public static class Terminal<T extends VertexBuilderNode<T> & VertexBuilderNode.Root> extends VertexBuilderNode<T> {
		Terminal(BufferBuilder builder) {
			super(builder);
		}

		/**
		 * End the current vertex and proceed to the next.
		 * @return the root of the chain, to start anew.
		 */
		public T endVertex() {
			this.builder.endVertex();
			return this.root;
		}
	}

	/**
	 * Class representing a central part of the chain with a next element to populate.
	 * @param <N> the type of node to have next.
	 * @param <T> the vertex builder at the root.
	 */
	abstract static class Chain<N extends VertexBuilderNode<T>, T extends VertexBuilderNode<T> & VertexBuilderNode.Root> extends VertexBuilderNode<T> {
		Chain(N next, BufferBuilder builder) {
			super(builder);
			this.next = next;
		}

		protected final N next;

		@Override
		void updateRoot(T root) {
			super.updateRoot(root);
			this.next.updateRoot(root);
		}
	}

	/**
	 * Represents a node in the chain where position must be specified.
	 * @param <N> the type of node to have next.
	 * @param <T> the vertex builder at the root.
	 */
	public static class Position<N extends VertexBuilderNode<T>, T extends VertexBuilderNode<T> & VertexBuilderNode.Root> extends Chain<N, T> {
		Position(N next, BufferBuilder builder) {
			super(next, builder);
		}

		/**
		 * Set the position of the current vertex.
		 * @param x the x position.
		 * @param y the y position.
		 * @return the next node in the chain.
		 */
		public N position(double x, double y) {
			return this.position(x, y, 0.0);
		}

		/**
		 * Set the position of the current vertex.
		 * @param x the x position.
		 * @param y the y position.
		 * @param z the z position.
		 * @return the next node in the chain.
		 */
		public N position(double x, double y, double z) {
			this.builder.vertex(x, y, z);
			return this.next;
		}

		/**
		 * Set the position of the current vertex with a transformation matrix.
		 * @param matrix the matrix to apply to the position.
		 * @param x the x position.
		 * @param y the y position.
		 * @return the next node in the chain.
		 */
		public N position(Matrix4f matrix, float x, float y) {
			return this.position(matrix, x, y, 0.0f);
		}

		/**
		 * Set the position of the current vertex with a transformation matrix.
		 * @param matrix the matrix to apply to the position.
		 * @param x the x position.
		 * @param y the y position.
		 * @param z the z position.
		 * @return the next node in the chain.
		 */
		public N position(Matrix4f matrix, float x, float y, float z) {
			this.builder.vertex(matrix, x, y, z);
			return this.next;
		}
	}

	/**
	 * Represents a node in the chain where colour must be specified.
	 * @param <N> the type of node to have next.
	 * @param <T> the vertex builder at the root.
	 */
	public static class Colour<N extends VertexBuilderNode<T>, T extends VertexBuilderNode<T> & VertexBuilderNode.Root> extends Chain<N, T> {
		Colour(N next, BufferBuilder builder) {
			super(next, builder);
		}

		/**
		 * Set the colour of the current vertex.
		 * @param r the red component.
		 * @param g the green component.
		 * @param b the blue component.
		 * @return the next node in the chain.
		 */
		public N colour(float r, float g, float b) {
			return this.colour(r, g, b, 1.0f);
		}

		/**
		 * Set the colour of the current vertex.
		 * @param r the red component.
		 * @param g the green component.
		 * @param b the blue component.
		 * @param alpha the alpha component.
		 * @return the next node in the chain.
		 */
		public N colour(float r, float g, float b, float alpha) {
			this.builder.color(r, g, b, alpha);
			return this.next;
		}
	}

	/**
	 * Represents a node in the chain where texture UVs must be specified.
	 * @param <N> the type of node to have next.
	 * @param <T> the vertex builder at the root.
	 */
	public static class Texture<N extends VertexBuilderNode<T>, T extends VertexBuilderNode<T> & VertexBuilderNode.Root> extends Chain<N, T> {
		Texture(N next, BufferBuilder builder) {
			super(next, builder);
		}

		/**
		 * Set the UV coordinates of the current vertex.
		 * @param u the u component.
		 * @param v the v component.
		 * @return the next node in the chain.
		 */
		public N uv(float u, float v) {
			this.builder.uv(u, v);
			return this.next;
		}
	}

	/**
	 * Represents a node in the chain where lightmap UVs must be specified.
	 * @param <N> the type of node to have next.
	 * @param <T> the vertex builder at the root.
	 */
	public static class Lightmap<N extends VertexBuilderNode<T>, T extends VertexBuilderNode<T> & VertexBuilderNode.Root> extends Chain<N, T> {
		Lightmap(N next, BufferBuilder builder) {
			super(next, builder);
		}

		/**
		 * Set the lightmap UV coordinates of the current vertex.
		 * @param u the u component.
		 * @param v the v component.
		 * @return the next node in the chain.
		 */
		public N uv(int u, int v) {
			this.builder.uv2(u, v);
			return this.next;
		}

		/**
		 * Set the lightmap UV coordinates of the current vertex.
		 * @param lightmap the packed lightmap coordinates.
		 * @return the next node in the chain.
		 */
		public N uv(int lightmap) {
			this.builder.uv2(lightmap);
			return this.next;
		}
	}

	/**
	 * The root of a chain.
	 */
	public interface Root extends AutoCloseable {
		/**
		 * Get the vertex format this chain represents.
		 * @return the vertex format represented by the chain.
		 */
		VertexFormat format();
	}
}
