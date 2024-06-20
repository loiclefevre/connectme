package com.oracle.connect.gui;

import com.oracle.connect.ConnectSyntax;
import com.oracle.connect.ConnectionStringTransformer;
import com.oracle.connect.EffectChanger;
import com.oracle.connect.LoginAttempt;
import com.oracle.connect.PingPoller;
import com.oracle.connect.gui.fx.BilinearGradientEffect;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import oracle.jdbc.driver.BuildInfo;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;

import java.awt.*;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.oracle.connect.PingPoller.MAX_VALUES;

/**
 * GUI controller for widgets.
 *
 * @author Loïc Lefèvre
 */
public class Controller {
	private boolean needsInitialization = true;

	private Timer timer;
	private PingPoller pingPoller;
	private EffectChanger effectChanger;

	@FXML
	private NumberAxis pingChartXAxis;

	@FXML
	private Pane img;

	@FXML
	private Label driverVersion;

	@FXML
	private Label databaseVersion;

	@FXML
	private Label databaseHost;

	@FXML
	private Label databaseName;

	@FXML
	private Label databaseRole;

	@FXML
	private Label databaseDomain;

	@FXML
	private Label drainingStatus;

	@FXML
	private Label physicalConnectionInfo;

	@FXML
	private Label pingChartInformation;

	@FXML
	private Label connectedIn;

	@FXML
	private Label errorLink;

	@FXML
	private LineChart<Number, Number> pingChart;

	private BilinearGradientEffect effect;

	public void initialize() {
		if (needsInitialization) {
			//System.out.println(PrismSettings.isVsyncEnabled );

			timer = new Timer(true);

			pingChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
			pingChart.setCreateSymbols(false);
			pingChart.getStyleClass().add("thick-chart");
			pingChart.setStyle("CHART_COLOR_1: #00ff00ff;");
			pingChartXAxis.setForceZeroInRange(false);
			pingChartXAxis.setAutoRanging(false);
			((NumberAxis) pingChart.getXAxis()).setLowerBound(0);
			((NumberAxis) pingChart.getXAxis()).setUpperBound(MAX_VALUES - 1);

			pingChartXAxis.setTickLabelsVisible(false);


			pingChart.setVisible(false);
			pingChartInformation.setVisible(false);

			executor = Executors.newVirtualThreadPerTaskExecutor();

			driverVersion.setText("Driver version: " + BuildInfo.getDriverVersion()+ " ("+BuildInfo.getJDBCVersion()+")");

			analyseConnectionString();

			effect=new BilinearGradientEffect(1, img.getWidth(), img.getHeight());
			effect.setStrength(0d);
			connectLine.setEffect(effect.getFXEffect());

			needsInitialization = false;
		}
	}

	@FXML
	private TextField connectionString;

	private ConnectSyntax connectSyntax = ConnectSyntax.UNKNOWN;

	@FXML
	private javafx.scene.control.Button connectionStringFormat;

	@FXML
	private TextField userSchema;

	@FXML
	private PasswordField password;

	private ExecutorService executor;

	@FXML
	private ImageView connectLine;

	@FXML
	private ImageView databaseImage;

	@FXML
	private ImageView autonomousDatabaseImage;

	private volatile long lastLoginDecision;

	@FXML
	private void analyseConnectionString() {
		if (userSchema.getText().isEmpty() || password.getText().isEmpty() || connectionString.getText().isEmpty()) {
			if (connectionString.getText().trim().isEmpty()) {
				connectSyntax = ConnectSyntax.UNKNOWN;
				Platform.runLater(() -> errorLink.setText("Connection string is empty"));
				connectionStringFormat.setVisible(false);
			}
			else {
				defineConnectionStringFormat(connectionString.getText().trim().toLowerCase());
			}
			if (password.getText().isEmpty()) {
				Platform.runLater(() -> errorLink.setText("Password is empty"));
			}
			if (userSchema.getText().isEmpty()) {
				Platform.runLater(() -> errorLink.setText("User schema is empty"));
			}
			errorLink.setVisible(true);
			errorLink.setCursor(Cursor.DEFAULT);
			errorLink.setUnderline(false);
			errorLink.setOnMouseClicked(null);

			((GaussianBlur) img.getEffect()).setRadius(50d);
			((GaussianBlur) databaseImage.getEffect()).setRadius(50d);
			((GaussianBlur) autonomousDatabaseImage.getEffect()).setRadius(50d);
			databaseImage.setVisible(true);
			autonomousDatabaseImage.setVisible(false);
			databaseVersion.setVisible(false);
			databaseHost.setVisible(false);
			databaseDomain.setVisible(false);
			drainingStatus.setVisible(false);
			databaseName.setVisible(false);
			databaseRole.setVisible(false);
			physicalConnectionInfo.setVisible(false);
			connectLine.setVisible(false);
			connectedIn.setVisible(false);

			if (pingPoller != null) {
				pingPoller.cancel();
				effectChanger.cancel();
			}

			pingChart.setVisible(false);
			pingChartInformation.setVisible(false);

			return;
		}

		lastLoginDecision = System.nanoTime();

		defineConnectionStringFormat(connectionString.getText().trim().toLowerCase());

		executor.execute(new LoginAttempt(lastLoginDecision) {
			@Override
			public void run() {
				try {
					final OracleDataSource ods = new OracleDataSource();
					ods.setUser(userSchema.getText());
					final String userPassword = password.getText();
					ods.setPassword(userPassword);
					ods.setURL("jdbc:oracle:thin:@" + connectionString.getText());
					ods.setLoginTimeout(1);

					final long startTime = System.nanoTime();
					try (Connection c = ods.getConnection()) {
						final long endTime = System.nanoTime();
						if (this.loginDecisionTime < lastLoginDecision) return;

						connectionStringFormat.getStyleClass().add("button-valid");
						errorLink.setVisible(false);
						connectLine.setVisible(true);
						pingChart.setVisible(true);
						pingChartInformation.setVisible(true);

						if (pingPoller != null) {
							pingPoller.cancel();
							effectChanger.cancel();
						}

						pingPoller = new PingPoller(ods, userPassword, pingChart, pingChartInformation);
						effectChanger = new EffectChanger(effect);

						timer.scheduleAtFixedRate(pingPoller, 0, 1000L);
						timer.scheduleAtFixedRate(effectChanger, 0, 16L);

						final OracleConnection realConnection = (OracleConnection) c;

						System.out.println(String.format("YES CONNECTED in %.3f ms", (double) (endTime - startTime) / 1000000d));
						System.out.println(realConnection.getProperties());


						try (Statement s = c.createStatement()) {
							// retrieve database version
							// https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/SYS_CONTEXT.html
							try (ResultSet r = s.executeQuery("select version_full, SYS_CONTEXT ('USERENV', 'HOST'), SYS_CONTEXT ('USERENV', 'NETWORK_PROTOCOL'), SYS_CONTEXT ('USERENV', 'CDB_NAME'), SYS_CONTEXT ('USERENV', 'DB_NAME'), SYS_CONTEXT ('USERENV', 'DATABASE_ROLE'), SYS_CONTEXT ('USERENV', 'INSTANCE'), nvl(SYS_CONTEXT ('USERENV', 'DB_DOMAIN'),'N/A'), SYS_CONTEXT ('USERENV', 'DRAIN_STATUS'), sys_context('USERENV','CLOUD_SERVICE') from product_component_version")) {
								if (r.next()) {
									final String version = r.getString(1);
									final String host = r.getString(2);
									final String protocol = r.getString(3);
									final String cdbName = r.getString(4);
									final String pdbName = r.getString(5);
									final String dbRole = r.getString(6);
									final int dbInstance = r.getInt(7);
									final String dbDomain = r.getString(8);
									final String sessionDrainingStatus = r.getString(9);
									final String cloudService = r.getString(10);

									Platform.runLater(() -> {
										if(cloudService != null) {
											switch (cloudService) {
												case "JSON":
												case "OLTP":
												case "DWCS":
													databaseImage.setVisible(false);
													autonomousDatabaseImage.setVisible(true);
													break;
												default:
													databaseImage.setVisible(true);
													autonomousDatabaseImage.setVisible(false);
													break;
											}
										}

										physicalConnectionInfo.setText(String.format("Protocol %s, network compression levels %s (threshold: %s)", protocol,
												realConnection.getProperties().getProperty("oracle.net.networkCompressionLevels"),
												realConnection.getProperties().getProperty("oracle.net.networkCompressionThreshold")
										));
										databaseVersion.setText("Version: " + version);
										databaseHost.setText("Host: " + host);
										if (cdbName.equals(pdbName)) {
											databaseName.setText("Database: " + cdbName + (dbInstance > 1 ? " (RAC #" + dbInstance + ")" : ""));
										}
										else {
											databaseName.setText((cloudService != null ? cloudService+" ": "")+"PDB: " + pdbName + (dbInstance > 1 ? " (RAC #" + dbInstance + ")" : ""));
										}
										databaseRole.setText("Role: " + dbRole);
										databaseDomain.setText("Domain: " + dbDomain);
										drainingStatus.setText("Session draining status: " + sessionDrainingStatus);
										connectedIn.setText(String.format("Connected in %.3f ms", (double) (endTime - startTime) / 1000000d));
									});
									databaseHost.setVisible(true);
									databaseDomain.setVisible(true);
									drainingStatus.setVisible(true);
									databaseName.setVisible(true);
									databaseRole.setVisible(true);
									physicalConnectionInfo.setVisible(true);
									databaseVersion.setVisible(true);
									connectedIn.setVisible(false);
									connectedIn.setVisible(true);
									((GaussianBlur) img.getEffect()).setRadius(10d);
									((GaussianBlur) databaseImage.getEffect()).setRadius(0d);
									((GaussianBlur) autonomousDatabaseImage.getEffect()).setRadius(0d);
								}
							}
						}
					}
				}
				catch (SQLException e) {
					if (this.loginDecisionTime < lastLoginDecision) return;

					((GaussianBlur) img.getEffect()).setRadius(50d);
					((GaussianBlur) databaseImage.getEffect()).setRadius(50d);
					((GaussianBlur) autonomousDatabaseImage.getEffect()).setRadius(50d);
					databaseImage.setVisible(true);
					autonomousDatabaseImage.setVisible(false);
					databaseVersion.setVisible(false);
					databaseHost.setVisible(false);
					databaseDomain.setVisible(false);
					drainingStatus.setVisible(false);
					databaseName.setVisible(false);
					databaseRole.setVisible(false);
					physicalConnectionInfo.setVisible(false);
					connectLine.setVisible(false);
					connectedIn.setVisible(false);
					pingChart.setVisible(false);
					pingChartInformation.setVisible(false);
					connectionStringFormat.getStyleClass().remove("button-valid");
					Platform.runLater(() -> {
						errorLink.setText(e.getMessage().split("\n")[0]);
						final String url = findURL(e.getMessage());
						if (url != null) {
							errorLink.setUnderline(true);
							errorLink.setCursor(Cursor.HAND);
							errorLink.setOnMouseClicked(mouseEvent -> {
								try {
									Desktop.getDesktop().browse(new URI(url));
								}
								catch (Exception ignored) {
								}
							});
						}
						else {
							errorLink.setCursor(Cursor.DEFAULT);
							errorLink.setUnderline(false);
							errorLink.setOnMouseClicked(null);
						}
						errorLink.setVisible(true);
					});

					System.out.println(e.getMessage());
				}
			}
		});
	}

	private void defineConnectionStringFormat(final String connectionString) {
		if (connectionString.startsWith("//") || connectionString.startsWith("tcp://") || connectionString.startsWith("tcps://")) {
			connectSyntax = ConnectSyntax.EASY_CONNECT;
			Platform.runLater(() -> {connectionStringFormat.setText("Easy Connect");
			connectionStringFormat.setVisible(true);});
		}
		else if (isTNSNamesOraSyntax(connectionString)) {
			connectSyntax = ConnectSyntax.TNSNAMES_ORA;
			Platform.runLater(() -> {connectionStringFormat.setText("tnsnames.ora");
			connectionStringFormat.setVisible(true);});
		}
		else {
			connectSyntax = ConnectSyntax.UNKNOWN;
			Platform.runLater(() -> {connectionStringFormat.setText("");
			connectionStringFormat.setVisible(false);});
		}
	}

	private boolean isTNSNamesOraSyntax(final String s) {
		if (s.startsWith("(")) {
			final int descPos = s.indexOf("description", 1);
			if (descPos != -1) {
				final int equalsPos = s.indexOf('=', descPos + 11);
				if(equalsPos  != -1 ) {
					return s.indexOf("(", equalsPos) != -1 && s.endsWith(")");
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	private String findURL(String message) {
		for (String line : message.split("\n")) {
			if (line.startsWith("https://docs.oracle.com/")) {
				return line;
			}
		}

		return null;
	}

	@FXML
	private void onActionConnectStringFormat() {
		if(connectSyntax == ConnectSyntax.UNKNOWN) return;

		if(connectSyntax == ConnectSyntax.EASY_CONNECT) {
			try {
				final String newConnectionString = ConnectionStringTransformer.transformIntoTNSNamesOra(connectionString.getText().trim());
				connectionString.setText( newConnectionString );
				analyseConnectionString();
//				defineConnectionStringFormat( connectionString.getText().trim().toLowerCase() );
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(connectSyntax == ConnectSyntax.TNSNAMES_ORA) {
			try {
				final String newConnectionString = ConnectionStringTransformer.transformIntoEasyConnect(connectionString.getText().trim());
				connectionString.setText( newConnectionString );
				//defineConnectionStringFormat( connectionString.getText().trim().toLowerCase() );
				analyseConnectionString();
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}