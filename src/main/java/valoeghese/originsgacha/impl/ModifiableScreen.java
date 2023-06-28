package valoeghese.originsgacha.impl;

/**
 * A screen that has a 'modified' flag, for use in third parties modifying screens via mixins.
 */
public interface ModifiableScreen {
	/**
	 * Whether this screen has been modified by a third party.
	 * @return whether this screen has been modified by a third party.
	 */
	boolean isModified();

	/**
	 * Set the modified flag.
	 * @param modified whether this screen has been modified by a third party.
	 */
	void setModified(boolean modified);
}
