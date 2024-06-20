package com.oracle.connect.util;

public class StringParameter extends Parameter {
	private final String value;

	public StringParameter(String name, String value) {
		super(name);
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
}
