package net.sourcedestination.sai.reporting;

import java.util.concurrent.Callable;

/** represents a loggable task executing over a SAI database.
 * 
 * @author jmorwick
 *
 */
public interface SAITask extends Callable<Log> {
	
	/** optional method which reports how much of the task's work is remaining.
	 *  By default, this always returns 0.0.
	 * 
	 * @return percentage (0.0 - 1.0) of the task remaining
	 */
	public default double getPercentageDone() {
		return 0.0;
	}
	
	/** optional method which reports how much work has been done (in nondescript "units").
	 *  By default, this always returns 0.
	 * 
	 * @return number of work units completed by this task
	 */
	public default int getProgressUnits() {
		return 0;
	}
}
