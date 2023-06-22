package valoeghese.originsgacha.util;

import org.jetbrains.annotations.Nullable;

/**
 * Utility class for mapping by segments, rather than exact key-value.
 * Implemented as a red-black tree.
 */
public final class Division<T> {
	/**
	 * Represents a node in the tree.
	 */
	private class Node {
		private Node(double key, T value) {
			this.key = key;
			this.value = value;
		}

		final double key;
		final T value;

		@Nullable Node leftChild;
		@Nullable Node rightChild;
		/**
		 * Only null if this node is the root.
		 * The root is black, so red nodes can never have a null parent.
		 */
		@Nullable Node parent;
		boolean red;

		private void setLeftChild(@Nullable Node leftChild) {
			this.leftChild = leftChild;
			if (leftChild != null) leftChild.parent = this;
		}

		private void setRightChild(@Nullable Node rightChild) {
			this.rightChild = rightChild;
			if (rightChild != null) rightChild.parent = this;
		}

		/**
		 * Get the sibling of the given child. Behaviour is undefined if the node provided is not a child of this node.
		 * @param child a child of this node.
		 * @return the given node's sibling.
		 */
		private Node siblingOf(Node child) {
			if (child == this.leftChild) {
				return this.rightChild;
			} else {
				return this.leftChild;
			}
		}
	}

	public Division() {

	}

	@Nullable
	private Node root;

	public Division<T> addSection(double minBound, T value) {
		Node node = new Node(minBound, value);
		this.insert(node);
		return this;
	}

	private void insert(Node node) {
		if (this.root == null) {
			this.root = node;
			node.red = false;
		}
		else {
			node.red = true;
			Node parent = this.root;

			// binary insertion
			while (true) {
				if (node.key < parent.key) {
					if (parent.leftChild == null) {
						parent.setLeftChild(node);
						break;
					}

					parent = parent.leftChild;
				}
				else {
					if (parent.rightChild == null) {
						parent.setRightChild(node);
						break;
					}

					parent = parent.rightChild;
				}
			}

			this.fixProperties(node);
		}
	}

	/**
	 * Fix properties of the red-black tree after insertion.
	 * @see <a href="https://www.programiz.com/dsa/red-black-tree.">Programiz: Red-Black Tree</a>
	 * <a href="https://brilliant.org/wiki/red-black-tree/.">Brilliant: Red-Black Tree</a>
	 * @param newNode the newly inserted node.
	 */
	private void fixProperties(Node newNode) {
		// Cannot fix properties for a null root.
		assert this.root != null;

		while (this.isRed(newNode.parent)) {
			System.out.println("Fixing " + newNode.value);
			assert newNode.parent.parent != null; // this should never occur due to the properties of a red-black tree.
			Node grandParent = newNode.parent.parent;
			System.out.println(" << " + newNode.parent.value);
			System.out.println(" << " + grandParent.value);

			@Nullable Node uncle = grandParent.siblingOf(newNode.parent);

			// case 1: parent and uncle are both red.
			if (this.isRed(uncle)) {
				System.out.println("Move black down.");
				// move down the blackness
				grandParent.red = true;
				uncle.red = false;
				newNode.parent.red = false;

				// Check grandparent next
				newNode = grandParent;
			}
			// check cases where the structure is a left child of the grandparent
			else if (newNode.parent == grandParent.leftChild) {
				// case 2: newNode is the right child of its parent (in a < structure), and its uncle is black.
				if (newNode == newNode.parent.rightChild) {
					System.out.println("Rotate Parent Left");
					// rotate the structure left
					// after this, newNode is the 'parent' of the structure and we'll want to act on its old parent
					newNode = newNode.parent;
					// remember rotateLeft takes the parent of the structure as an argument
					// we reassigned pre-emptively so we just pass newNode.
					grandParent.setLeftChild(this.rotateLeft(newNode));
				}
				// case 3: newNode is the left child of its parent (in a / structure), and its uncle is black
				// also will always run immediately after case 2.
				System.out.println("Rotate Grandparent Right");

				// right rotate the grandparent
				@Nullable Node greatGrandParent = grandParent.parent;
				Node newGrandParent = this.rotateRight(grandParent);

				if (greatGrandParent == null) {
					this.root = newGrandParent;
				} else {
					if (greatGrandParent.leftChild == grandParent) {
						greatGrandParent.setLeftChild(newGrandParent);
					} else {
						greatGrandParent.setRightChild(newGrandParent);
					}
				}
			}
			// check cases where the structure is a right child of the grandparent
			// mirror of above cases.
			else {
				// case 2: newNode is the left child of its parent (in a > structure), and its uncle is black.
				if (newNode == newNode.parent.leftChild) {
					System.out.println("Rotate Parent Right");
					// rotate the structure right
					// after this, newNode is the 'parent' of the structure and we'll want to act on its old parent
					newNode = newNode.parent;
					// remember rotateRight takes the parent of the structure as an argument
					// we reassigned pre-emptively so we just pass newNode.
					grandParent.setRightChild(this.rotateRight(newNode));
				}
				// case 3: newNode is the right child of its parent (in a \ structure), and its uncle is black
				// also will always run immediately after case 2.
				System.out.println("Rotate Grandparent Left");

				// left rotate the grandparent
				@Nullable Node greatGrandParent = grandParent.parent;
				Node newGrandParent = this.rotateLeft(grandParent);

				if (greatGrandParent == null) {
					this.root = newGrandParent;
				} else {
					if (greatGrandParent.leftChild == grandParent) {
						greatGrandParent.setLeftChild(newGrandParent);
					} else {
						greatGrandParent.setRightChild(newGrandParent);
					}
				}
			}
		}

		// Root is black.
		this.root.red = false;
	}

	/**
	 * Helper method for checking if a nullable node is red. Use this to handle cases that node == null.
	 * @param node the node to check for redness.
	 * @return whether the given node is red. This also {@linkplain Node#parent guarantees the node has a parent}.
	 */
	private boolean isRed(@Nullable Node node) {
		return node != null && node.red;
	}

	/**
	 * Rotate the given node left and get the new parent.
	 * @param parent the parent of the group to rotate. Its right child must not be null.
	 * @return the new parent.
	 */
	private Node rotateLeft(Node parent) {
		Node swapChild = parent.rightChild;
		assert swapChild != null : "Right child in left rotation cannot be null.";

		// leftGroup and rightGroup remain unmoved relative to their parents after the shift
		@Nullable Node midGroup = swapChild.leftChild;

		parent.setRightChild(midGroup);
		swapChild.setLeftChild(parent);

		// update un-updated parents
		// The parent's parent should be properly updated after calling this method
		// But this should provide at least partial-correctness.
		swapChild.parent = parent.parent;

		// swap colours of parent and new parent (swapChild)
		boolean tempRed = parent.red;
		parent.red = swapChild.red;
		swapChild.red = tempRed;

		return swapChild;
	}

	/**
	 * Rotate the given node right and get the new parent.
	 * @param parent the parent of the group to rotate. Its left child must not be null.
	 * @return the new parent.
	 */
	private Node rotateRight(Node parent) {
		Node swapChild = parent.leftChild;
		assert swapChild != null : "Left child in right rotation cannot be null.";

		// leftGroup and rightGroup remain unmoved relative to their parents after the shift
		@Nullable Node midGroup = swapChild.rightChild;

		parent.setLeftChild(midGroup);
		swapChild.setRightChild(parent);

		// update un-updated parents
		// The parent's parent should be properly updated after calling this method
		// But this should provide at least partial-correctness.
		swapChild.parent = parent.parent;

		// swap colours of parent and new parent (swapChild)
		boolean tempRed = parent.red;
		parent.red = swapChild.red;
		swapChild.red = tempRed;

		return swapChild;
	}

	// Thanks ChatGPT
	// Was originally for printing a binary tree. Edited slightly to fix in a toString and show all properties.

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();

		// Helper function to traverse the binary tree
		this.traverse(root, "", false, output);

		return output.toString();
	}

	private void traverse(Node node, String prefix, boolean isLeft, StringBuilder output) {
		if (node != null) {
			output.append(prefix);
			output.append(isLeft ? "├── " : "└── ");
			output.append(node.key).append(": ").append(node.value);
			output.append(" - ").append(node.red ? "R": "B");
			output.append(" << ").append(node.parent == null ? "null" : node.parent.value).append("\n");

			String indent = prefix + (isLeft ? "│   " : "    ");
			boolean hasLeftChild = node.leftChild != null;
			boolean hasRightChild = node.rightChild != null;

			if (hasLeftChild || hasRightChild) {
				if (hasLeftChild) {
					traverse(node.leftChild, indent, true, output);
				} else {
					output.append(indent).append("├── (empty)\n");
				}

				if (hasRightChild) {
					traverse(node.rightChild, indent, false, output);
				} else {
					output.append(indent).append("└── (empty)\n");
				}
			}
		}
	}
}
