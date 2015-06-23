package net.sourcedestination.sai.task;


//TODO: do I need this? Why not Supplier<SAITask> instead?
public interface TaskInitiator {
	public Task startTask();
}
