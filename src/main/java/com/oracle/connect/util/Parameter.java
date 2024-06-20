package com.oracle.connect.util;

public abstract class Parameter {
	protected final String name;

	public Parameter(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract String getValue();

	@Override
	public String toString() {
		return String.format("%s=%s",name,getValue());
	}
}
