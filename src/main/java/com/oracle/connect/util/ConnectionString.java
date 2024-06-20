package com.oracle.connect.util;

import java.util.List;

public record ConnectionString(
		String protocol,
		List<HostPort> hosts,
		String serviceName,
		List<Parameter> parameters
) {
}
