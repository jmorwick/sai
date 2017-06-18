package net.sourcedestination.sai.task;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import net.sourcedestination.sai.reporting.Log;

/** represents a loggable task executing over a SAI database.
 * 
 * @author jmorwick
 *
 */
public interface Task<T> extends Supplier<T> {
	
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
	
	public default String getTaskName() {
		return this.getClass().getCanonicalName();
	}
	
	public default void cancel() {
		
	}
	
	public default boolean running() {
		return getPercentageDone() < 100.0 && getPercentageDone() > 0.0;
	}
}
