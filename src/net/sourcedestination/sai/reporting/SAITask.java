package net.sourcedestination.sai.reporting;

import java.util.concurrent.Callable;

public interface SAITask extends Callable<Report> {
	
	public default double getPercentageDone() {
		return 0.0;
	}
	
	public default int getProgressUnits() {
		return 0;
	}
}
