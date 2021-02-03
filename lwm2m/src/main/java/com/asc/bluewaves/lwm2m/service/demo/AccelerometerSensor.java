package com.asc.bluewaves.lwm2m.service.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.servers.ServerIdentity;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.util.NamedThreadFactory;
import org.eclipse.leshan.server.Destroyable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccelerometerSensor extends BaseInstanceEnabler implements Destroyable {

	// Accelerometer data
	private static final String UNIT_DEGREE = "degree";
	private static final int SENSOR_UNITS = 5701;
	private static final int X_VALUE = 5702;
	private static final int Y_VALUE = 5703;
	private static final int Z_VALUE = 5704;
	private static final int MIN_RANGE_VALUE = 5603;
	private static final int MAX_RANGE_VALUE = 5604;

	private static final List<Integer> supportedResources = Arrays.asList(SENSOR_UNITS, X_VALUE, Y_VALUE, Z_VALUE, MIN_RANGE_VALUE,
			MAX_RANGE_VALUE);

	private double x = 0, y = 0, z = 0;
	private double minRangeValue = 0, maxRangeValue = 0;

	private final ScheduledExecutorService scheduler;
	private final Random rng = new Random();

	public AccelerometerSensor() {
		this.scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Accelerometer Sensor"));
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				adjustAccelerometer();
			}
		}, 2, 2, TimeUnit.SECONDS);
	}

	@Override
	public synchronized ReadResponse read(ServerIdentity identity, int resourceId) {
		log.info("Read on Accelerometer resource /{}/{}/{}", getModel().id, getId(), resourceId);
		switch (resourceId) {
		case MIN_RANGE_VALUE:
			return ReadResponse.success(resourceId, getTwoDigitValue(minRangeValue));
		case MAX_RANGE_VALUE:
			return ReadResponse.success(resourceId, getTwoDigitValue(maxRangeValue));
		case X_VALUE:
			return ReadResponse.success(resourceId, getTwoDigitValue(x));
		case Y_VALUE:
			return ReadResponse.success(resourceId, getTwoDigitValue(y));
		case Z_VALUE:
			return ReadResponse.success(resourceId, getTwoDigitValue(z));
		case SENSOR_UNITS:
			return ReadResponse.success(resourceId, UNIT_DEGREE);
		default:
			return super.read(identity, resourceId);
		}
	}

	private double getTwoDigitValue(double value) {
		BigDecimal toBeTruncated = BigDecimal.valueOf(value);
		return toBeTruncated.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	private void adjustAccelerometer() {
		double xDelta = (rng.nextInt(20) - 10) / 10d;
		double yDelta = (rng.nextInt(20) - 10) / 10d;
		double zDelta = (rng.nextInt(20) - 10) / 10d;

		x += xDelta;
		y += yDelta;
		z += zDelta;

		double newMax = Collections.max(Arrays.asList(x, y, z, maxRangeValue));
		double newMin = Collections.min(Arrays.asList(x, y, z, minRangeValue));

		if (newMax != maxRangeValue && newMin != minRangeValue) {
			maxRangeValue = newMax;
			minRangeValue = newMin;
			fireResourcesChange(X_VALUE, Y_VALUE, Z_VALUE, MIN_RANGE_VALUE, MAX_RANGE_VALUE);

		} else if (newMax != maxRangeValue) {
			maxRangeValue = newMax;
			fireResourcesChange(X_VALUE, Y_VALUE, Z_VALUE, MAX_RANGE_VALUE);

		} else if (newMin != minRangeValue) {
			minRangeValue = newMin;
			fireResourcesChange(X_VALUE, Y_VALUE, Z_VALUE, MIN_RANGE_VALUE);

		} else {
			fireResourcesChange(X_VALUE, Y_VALUE, Z_VALUE);
		}
	}

	@Override
	public List<Integer> getAvailableResourceIds(ObjectModel model) {
		return supportedResources;
	}

	@Override
	public void destroy() {
		scheduler.shutdown();
	}

}
