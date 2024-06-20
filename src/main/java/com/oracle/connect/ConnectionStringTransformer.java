package com.oracle.connect;

import com.oracle.connect.util.ConnectionString;
import com.oracle.connect.util.EasyConnectLexer;
import com.oracle.connect.util.EasyConnectParser;
import com.oracle.connect.util.HostPort;
import com.oracle.connect.util.Parameter;
import com.oracle.connect.util.TNSNamesOraLexer;
import com.oracle.connect.util.TNSNamesOraParser;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;

public class ConnectionStringTransformer {

	public static void main(String[] args) {
		//transformIntoTNSNamesOra("tcps://localhost:1522,hello/freepdb1.fr.oracle.com?ssl_server_cert_dn=\"C=US\"");
		transformIntoTNSNamesOra("tcps://adb.eu-paris-1.oraclecloud.com:1522/g04882e973a17cf_atps_tp.adb.oraclecloud.com?retry_count=20&retry_delay=3&ssl_server_dn_match=yes");
		transformIntoEasyConnect("(description=(retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=adb.eu-paris-1.oraclecloud.com))(connect_data=(service_name=g04882e973a17cf_atps_tp.adb.oraclecloud.com))(security=(ssl_server_dn_match=yes)))");
	}
	public static String transformIntoTNSNamesOra(final String easyConnect) {

		final EasyConnectLexer lexer = new EasyConnectLexer(CharStreams.fromString(easyConnect));
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		EasyConnectParser parser = new EasyConnectParser(tokens);
		BailErrorStrategy errorHandler = new BailErrorStrategy();
		parser.setErrorHandler(errorHandler);
		EasyConnectParser.MainContext tree = parser.main();

		final ConnectionString c = tree.connect_string().result;
		System.out.println(c);

		final String tnsNameSOra = String.format("(description=%s)", formatTNSNamesOra(c));

		System.out.println(tnsNameSOra);

		return tnsNameSOra;
	}

	private static String formatTNSNamesOra(final ConnectionString c) {
		final StringBuilder s = new StringBuilder();

		// front parameters
		for(Parameter p :c.parameters()) {
			switch(p.getName()) {
				case "retry_count":
				case "retry_delay":
					s.append('(').append(p.getName()).append('=').append(p.getValue()).append(')');
					break;
			}
		}

		// (description=(retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=adb.eu-paris-1.oraclecloud.com))(connect_data=(service_name=g04882e973a17cf_atps_tp.adb.oraclecloud.com))(security=(ssl_server_dn_match=yes)))

		// address
		s.append("(address=");
		for(HostPort hp:c.hosts()) {
			s.append("(protocol=").append(c.protocol()).append(")(port=").append(hp.port()).append(")(host=").append(hp.host()).append(')');
		}
		s.append(')');

		// connect data
		s.append("(connect_data=");
		if(c.serviceName() != null) {
			s.append("(service_name=").append(c.serviceName()).append(')');
		}
		s.append(')');

		// security
		s.append("(security=");
		for(Parameter p :c.parameters()) {
			switch(p.getName()) {
				case "ssl_server_dn_match":
					s.append('(').append(p.getName()).append('=').append(p.getValue()).append(')');
					break;
			}
		}
		s.append(')');


		return s.toString();
	}

	public static String transformIntoEasyConnect(final String tnsNamesOra) {

		final TNSNamesOraLexer lexer = new TNSNamesOraLexer(CharStreams.fromString(tnsNamesOra));
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		TNSNamesOraParser parser = new TNSNamesOraParser(tokens);
		BailErrorStrategy errorHandler = new BailErrorStrategy();
		parser.setErrorHandler(errorHandler);
		TNSNamesOraParser.MainContext tree = parser.main();

		final ConnectionString c = tree.connect_string().result;
		System.out.println(c);

		final String easyConnect = String.format("%s//%s/%s%s",
				c.protocol() == null ? "" : c.protocol()+":",
				toEasyConnectHosts(c.hosts()),
				c.serviceName()== null ? "" : c.serviceName(),
				toEasyConnectParameters(c.parameters())
				);

		System.out.println(easyConnect);

		return easyConnect;
	}

	private static String toEasyConnectParameters(final List<Parameter> parameters) {
		final StringBuilder s = new StringBuilder();

		if(!parameters.isEmpty()) {
			int i = 0;

			for(Parameter p:parameters) {
				if (i == 0) s.append('?'); else s.append('&');

				s.append(p.getName()).append('=').append(p.getValue());
				i++;
			}
		}

		return s.toString();
	}

	private static String toEasyConnectHosts(final List<HostPort> hosts) {
		final StringBuilder s = new StringBuilder();

		int i = 0;
		for(HostPort hp:hosts) {
			if(i>0) s.append(',');

			s.append(hp.host() == null ? "" : hp.host());

			if(hp.port() != 1521) {
				s.append(':').append(hp.port());
			}

			i++;
		}

		return s.toString();
	}
}
