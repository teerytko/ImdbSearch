package com.example.firstandroid;

import java.util.HashMap;

public class ImdbItem extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String NAME_KEY = "title";

	@Override
	public String toString() {
		return this.get(ImdbItem.NAME_KEY);
	}
	
}
