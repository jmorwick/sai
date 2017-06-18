package net.sourcedestination.sai.task;

import java.util.function.Supplier;

import net.sourcedestination.sai.reporting.Log;

public class RepeatTask implements Supplier<Task<Log>> {
	
	private final Supplier<Task<Log>> ti;
	private final int iterations;
	

	/** creates a new task which repeats a given task a fixed number of times.
	 * 
	 * @param ti creates the task to be repeatedly executed
	 * @param iterations the number of times to repeat the task
	 */
	public RepeatTask(Supplier<Task<Log>> ti, int iterations) {
		this.ti = ti;
		this.iterations = iterations;
	}


	@Override
	public Task<Log> get() {

		
		return new Task<Log>() {

			private Task<Log> currentTask;
			private int i;
			
			@Override
			public Log get() {
				Log log = null;
				for(i=0; i<iterations; i++) {
					currentTask = ti.get();
					if(log == null)
						log = currentTask.get();
					else log.include(currentTask.get());
				}
				return log;
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
