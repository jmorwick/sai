package net.sourcedestination.sai.reporting;

//TODO: do I need this? Why not Supplier<SAITask> instead?
public interface TaskInitiator {
	public SAITask startTask();
}
