package com.spartanlaboratories.engine.structure;

public class ObsoleteCommandException extends Exception {
	public ObsoleteCommandException(Engine engine, String message){
		engine.tracker.printAndLog("An Obsolete Command Exception has occured");
		engine.tracker.printAndLog(message);
	}
}
