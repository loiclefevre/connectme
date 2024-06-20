package com.oracle.connect.util;

public class IntegerParameter extends Parameter {
	public final int value;

	public IntegerParameter(String name, int value) {
		super(name);
		this.value = value;
	}

	public IntegerParameter(String name, String value) {
		this(name, Integer.parseInt(value));
	}

	@Override
	public String getValue() {
		return String.valueOf(value);
	}

}
