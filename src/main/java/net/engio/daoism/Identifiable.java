package net.engio.daoism;

import java.io.Serializable;

public interface Identifiable<K extends Serializable> {

	/**
	 * Get the primary key of the object
	 * 
	 * @return
	 */
	public K getId();

}
