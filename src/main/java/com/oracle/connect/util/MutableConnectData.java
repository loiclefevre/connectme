package com.oracle.connect.util;

public class MutableConnectData {
	private String serviceName;
	private String instanceName;
	private String serviceHandlerType;
	public MutableConnectData() {
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getServiceHandlerType() {
		return serviceHandlerType;
	}

	public void setServiceHandlerType(String serviceHandlerType) {
		this.serviceHandlerType = serviceHandlerType;
	}
}
