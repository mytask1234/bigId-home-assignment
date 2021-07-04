package com.bigid.model;

public class WordLocation {

	/**
	 * The line index in a file. The first line has lineOffset=0
	 */
	private int lineOffset;
	
	/**
	 * The char index in a file. The first char has charOffset=0
	 */
	private int charOffset;
	
	public WordLocation(int lineOffset, int charOffset) {
		super();
		this.lineOffset = lineOffset;
		this.charOffset = charOffset;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[lineOffset=");
		builder.append(lineOffset);
		builder.append(", charOffset=");
		builder.append(charOffset);
		builder.append("]");
		return builder.toString();
	}
}
