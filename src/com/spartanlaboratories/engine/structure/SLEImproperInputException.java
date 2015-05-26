package com.spartanlaboratories.engine.structure;

/**
 * Thrown if there is some sort of incorrect input given to engine elements while using the engine's graphical user interface.
 * @author spart_000
 *
 */
@SuppressWarnings("serial")
public class SLEImproperInputException extends Exception{
	SLEImproperInputException(Tracker tracker, String string){
		tracker.printAndLog(string);
	}
}
