package net.sourcedestination.sai.task;

import java.util.function.Supplier;

public class RepeatTask implements Supplier<Task> {
	
	private final Supplier<Task> ti;
	private final int iterations;
	

	/** creates a new task which repeats a given task a fixed number of times.
	 * 
	 * @param ti creates the task to be repeatedly executed
	 * @param iterations the number of times to repeat the task
	 */
	public RepeatTask(Supplier<Task> ti, int iterations) {
		this.ti = ti;
		this.iterations = iterations;
	}


	@Override
	public Task get() {

		return new Task() {
			private Task currentTask;
			private int i;
			
			@Override
			public Object get() {
				for(i=0; i<iterations; i++) {
					currentTask = ti.get();
				}
				return null;
			}
			
			public double getPercentageDone() {
				return (double)i / (double)iterations;
			}
			
			public int getProgressUnits() {
				return i;
			}
		};
	}

}
