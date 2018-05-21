package net.sourcedestination.sai.util;

import java.util.function.Supplier;

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
	default double getPercentageDone() {
		return getTotalProgressUnits() != -1 ?
				(double)getProgressUnits() / getTotalProgressUnits() :
				0;
	}
	
	/** optional method which reports how much work has been done (in nondescript "units").
	 *  By default, this always returns 0.
	 * 
	 * @return number of work units completed by this task
	 */
	default int getProgressUnits() {
		return 0;
	}

	default int getTotalProgressUnits() { return -1; }
	
	default String getTaskName() {
		return this.getClass().getCanonicalName();
	}
	
	default void cancel() {
		
	}
}
