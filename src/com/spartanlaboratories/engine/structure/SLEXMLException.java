package com.spartanlaboratories.engine.structure;

public class SLEXMLException extends Exception {
	static Engine engine;
	public SLEXMLException(String string){
		engine.tracker.printAndLog(string);
	}
}
