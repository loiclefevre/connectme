package com.oracle.connect;

import com.oracle.connect.gui.fx.BilinearGradientEffect;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.TimerTask;

import static org.apache.commons.math3.stat.StatUtils.mean;
import static org.apache.commons.math3.stat.StatUtils.sum;

// see also: https://edencoding.com/javafx-charts/
public class PingPoller extends TimerTask {
	public static final int MAX_VALUES = 121;

	private double[] durations = new double[MAX_VALUES];

	private final OracleDataSource ods;
	private final LineChart<Number, Number> pingChart;
	private final Label pingChartInformation;
	private int x;
	private long minDuration = Long.MAX_VALUE;
	private long maxDuration = Long.MIN_VALUE;
	private StandardDeviation stddev = new StandardDeviation(true);

	private Connection c;
	private PreparedStatement p;


	public PingPoller(final OracleDataSource ods, final String password, LineChart<Number, Number> pingChart, Label pingChartInformation) throws SQLException {
		this.ods = new OracleDataSource();
		this.ods.setURL(ods.getURL());
		this.ods.setUser(ods.getUser());
		this.ods.setPassword(password);
		this.ods.setLoginTimeout(ods.getLoginTimeout());
		this.pingChart = pingChart;
		this.pingChartInformation = pingChartInformation;
		Platform.runLater(() -> {
			pingChart.getData().clear();
			pingChartInformation.setText("");
		});
		this.x = -1;
		this.c = ods.getConnection();
		p = c.prepareStatement("select 1 from dual");
	}

	@Override
	public void run() {
		try {
			if (!c.isValid(0)) {
				c = ods.getConnection();
				p = c.prepareStatement("select 1 from dual");
			}

			final long start = System.nanoTime();
			p.executeQuery();
			final long end = System.nanoTime();


			if (x == -1) {
				x++;
				Platform.runLater(() -> {
					pingChart.getData().add(new XYChart.Series<>());
					//((NumberAxis)pingChart.getXAxis()).setLowerBound(System.currentTimeMillis()+990);
					//pingChart.getData().getFirst().getData().add(new XYChart.Data<>(System.currentTimeMillis(), (double) (end - start) / 1000000d));
					pingChart.requestLayout();
				});
			}
			else {
				final long duration = end-start;
				minDuration = Math.min(minDuration, duration);
				maxDuration = Math.max(maxDuration, duration);

				Platform.runLater(() -> {
					try {
						final ObservableList<XYChart.Data<Number, Number>> list = pingChart.getData().getFirst().getData();
						final double durationInMS = (double) duration / 1000000d;
						//System.out.println("Ping: " + durationInMS);

						double mean = 0;
						double _stddev = 0;

						if(x >= MAX_VALUES) {
							System.arraycopy(durations, 1, durations, 0, MAX_VALUES-1);
							durations[MAX_VALUES-1] = durationInMS;

							mean = mean(durations,0,durations.length);
							_stddev = stddev.evaluate(durations, 0, durations.length);
						} else {
							durations[x] = durationInMS;
							if(x > 0) {
								mean = mean(durations,0,x+1);
								_stddev = stddev.evaluate(durations, 0, x+1);
							}
						}

						pingChartInformation.setText(String.format("mean=%.3fms, stddev=%.3fms, min=%.3fms, max=%.3fms",
								mean,
								_stddev,
								(double)minDuration/1000000d, (double)maxDuration/1000000d));

						list.add(new XYChart.Data<>(x++, durationInMS));

						if (list.size() > MAX_VALUES) { // 2 minutes
							list.remove(0);
							((NumberAxis)pingChart.getXAxis()).setLowerBound( x-MAX_VALUES );
							((NumberAxis)pingChart.getXAxis()).setUpperBound( x-1 );
						}

						pingChart.requestLayout();
					}
					catch (Exception ignored) {
						ignored.printStackTrace();
					}
				});
			}

		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	@Override
	public boolean cancel() {
		if (p != null) {
			try {
				p.close();
			}
			catch (SQLException ignored) {
			}
		}
		if (c != null) {
			try {
				c.close();
			}
			catch (SQLException ignored) {
			}
		}
		return super.cancel();
	}
}
