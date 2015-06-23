package net.sourcedestination.sai.task;

import net.sourcedestination.sai.reporting.Log;

public class RepeatTask implements TaskInitiator {
	
	private TaskInitiator ti;
	private int iterations;
	

	/** creates a new task which repeats a given task a fixed number of times.
	 * 
	 * @param ti creates the task to be repeatedly executed
	 * @param iterations the number of times to repeat the task
	 */
	public RepeatTask(TaskInitiator ti, int iterations) {
		this.ti = ti;
		this.iterations = iterations;
	}


	@Override
	public Task startTask() {

		
		return new Task() {

			private Task currentTask;
			private int i;
			
			@Override
			public Log call() throws Exception {
				Log log = null;
				for(i=0; i<iterations; i++) {
					currentTask = ti.startTask();
					if(log == null)
						log = currentTask.call();
					else log.include(currentTask.call());
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
