package com.spartanlaboratories.engine.structure;
/**
 * The old school way for the console to process commands, the new way is much more advanced that this one,
 * therefore this is deprecated.
 * However it does not contain all of the commands that exist in this version, they are being created but
 * in the meantime this will have to do.
 * @deprecated as of version A
 * @version Pre-A 
 */
final class OldCommand {
	String commandName;
	int numParameters;
	double[] numericParameters;
	String[] alphabeticParameters;
	int[] parameterLocations;
	ParameterType[] parameterTypes;
	Console owner;
	char[] input;
	/**
	 * Creates a new Command object by taking in its parent console and a string of input from said console.
	 * It then processes the string into several small strings and places those strings into appropriately 
	 * named fields inside this object.
	 * 
	 * @param console the parent of this command class
	 * @param originalString a string of input that is supposed to be a console command.
	 */
	OldCommand(Console console, String originalString){
		owner = console;
		input = originalString.toCharArray();
		commandName = getCommandName(originalString);
		owner.out("Recognized command name: " + commandName);
		sortParameters();
		if(numParameters > 0)placeParameters();
	}
	/**
	 * Describes whether a parameter is numeric or alphabetic.
	 * @author spart_000
	 */ 
	enum ParameterType{
		NUMERIC, ALPHABETIC,;
	}
	/**
	 * Gets the command name from the raw input string as a separate string and assigns it 
	 * to the commandName variable
	 * 
	 * @param
	 * string the raw string
	 */
	private String getCommandName(String string){
		String commandString = "";
		char[] stringAsCharArray = string.toCharArray();
		int i;
		for(i = 0; i < string.length(); i++){
			if(Character.isAlphabetic(stringAsCharArray[i]))commandString += stringAsCharArray[i];
		else break;
		}
		string = "";
		for(i += 1; i < stringAsCharArray.length - i; i++){
			string += stringAsCharArray[i];
		}
		return commandString;
	}
	private int getNumberOfParameters(){
		int n = 0;
		for(int i = 0; i < input.length; i++){
			if(Character.isWhitespace(input[i]))
				n++;
		}
		return n;
	}
	private void sortParameters(){
		numParameters = getNumberOfParameters();
		parameterLocations = new int[numParameters];
		parameterTypes = new ParameterType[numParameters];
		owner.out("The number of parameters is: " + numParameters);
		int n = 0;
		for(int i = 0; i < input.length; i++){
			if(Character.isWhitespace(input[i])){
				parameterLocations[n] = i + 1;
				owner.out("Parameter number: " + n + " is located at " + parameterLocations[n]);
				if(Character.isLetter(input[i+1]))parameterTypes[n++] = ParameterType.ALPHABETIC;
				else parameterTypes[n++] = ParameterType.NUMERIC;
				owner.out("And its parameter type is: " + parameterTypes[n-1]);
			}
		}
		int numNumericParameters = 0, numAlphabeticParameters = 0;
		for(ParameterType pt: parameterTypes)if(pt == ParameterType.ALPHABETIC)numAlphabeticParameters++;else numNumericParameters++;
		numericParameters = new double[numNumericParameters]; alphabeticParameters = new String[numAlphabeticParameters];
		owner.out("The number of numeric parameters is: " + numNumericParameters);
		owner.out("The number of alphabetic parameters is: " + numAlphabeticParameters);
	}
	private char[] getParameterCharArray(){
		String parameterString = "";
		boolean start = false;
		for(int i = 0; i < input.length; i++){
			if(start)parameterString += input[i];
			if(Character.isWhitespace(input[i]))start = true;
		}
		return parameterString.toCharArray();
	}
	private void placeParameters(){
		int a = 0, n = 0;
		String parameter = "";
		char[] parsedArray = (String.valueOf(input) + " ").toCharArray();
		for(int i = parameterLocations[0]; i < parsedArray.length; i++)
			if(!Character.isWhitespace(parsedArray[i]))parameter += input[i];
			else{
				if(parameterTypes[getArgumentEndsAt(i)] == ParameterType.ALPHABETIC)alphabeticParameters[a++] = parameter;
				else numericParameters[n++] = Integer.parseInt(parameter);
				parameter = "";
			}
	}
	private int getArgumentEndsAt(int i){
		int argNumber = 0;
		int argLocation = 0;
		if(i-- == input.length)argNumber = numParameters;
		else while(i > argLocation)argLocation = parameterLocations[++argNumber] - 2;
		return argNumber - 1;
	}
}
