package com.spartanlaboratories.engine.structure;

import java.util.ArrayList;
import java.util.HashMap;

import com.spartanlaboratories.engine.game.Ability;
import com.spartanlaboratories.engine.game.Actor;
import com.spartanlaboratories.engine.game.GameObject;
import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.util.Location;

public class Console extends StructureObject{
	public boolean parallelPrinting;
	public Executer executer;
	String[] commandNames;
	Human owner;
	Parser parser;
	Actor self, underMouse, considered, actionReceiver;
	ArrayList<Actor> inMind = new ArrayList<Actor>();
	protected int execution;
	public Console(Engine engine, Human player){
		super(engine);
		parser = new Parser(this);
		executer = new Executer(this);
		initializeCommandNames();
		owner = player;
		execution = 1;
	}
	public void takeCommand(String inputText) {
		try {
			parser.parseString(inputText + " ");
			if(parser.masterCommand != null)
				executer.execute(parser.masterCommand.readReadyCommand);
			for(Parser.Command c: parser.commands)
				executer.execute(c.readReadyCommand);
		} catch (com.spartanlaboratories.engine.structure.Console.Parser.IdentifierException 
				| com.spartanlaboratories.engine.structure.Console.Executer.CommandExecutionException e) {
			out(e.getLocalizedMessage());
		}finally{
			parser.commands.clear();
		}
	}
	/**
	 * Prints the passed in location.
	 * 
	 * @param location - The Location that is going to be shown.
	 */
	public void showLocation(Location location){
		out("(" + location.x +"," + location.y + ")");
	}
	/**
	 * Prints the location of the passed in {@link GameObject}
	 * 
	 * @param g - The {@link GameObject} the location of which is to be printed.
	 */
	public void showLocationOf(GameObject g){
		showLocation(g.getLocation());
	}
	/**
	 * Outputs the given string. 
	 * @param string - The String that is to be printed.
	 */
	public void out(String string){
		System.out.println(string);
	}
	/**
	 * Adds the passed in command to the list of command names that this Console's Command Parser recognises. 
	 * @param commandName The name of the new command
	 * @param numberOfParameters The number of parameters that the new command has
	 */
	public void addCommand(String commandName, int numberOfParameters){
		parser.numParams.put(commandName, numberOfParameters);
	}
	private void initializeCommandNames(){
		commandNames = new String[7];
		commandNames[0] = "help";
		commandNames[1] = "resetHeroLocation";
		commandNames[2] = "setHeroLocation";
		commandNames[3] = "changeAbilityLevel";
		commandNames[4] = "changeHeroStat";
		commandNames[5] = "respawn";
		commandNames[6] = "using";
	}
	
	/**
	 * Obsolete, deprecated, likely to throw, and even more likely to cause an error.
	 * @param string - The raw command string
	 * @throws ObsoleteCommandException - If the command is no longer supported
	 */
	final protected void oldTakeCommand(String string) throws ObsoleteCommandException{
		try{
			OldCommand command = new OldCommand(this, string);
			switch(command.commandName){
			case "help":
				owner.gui.out("Executing command: " + command.commandName);
				for(String s: commandNames)owner.gui.out(s);
				break;
			case "resetHeroLocation":
				owner.gui.out("Executing command: " + command.commandName);
				owner.controlledUnits.get(0).setLocation(0,0);
				break;	
			case "setHeroLocation":
				owner.gui.out("Executing command: " + command.commandName);
				owner.gui.out("Setting hero location to: " + command.numericParameters[0] + ", " + command.numericParameters[1]);
				owner.controlledUnits.get(0).setLocation(new Location(command.numericParameters[0], command.numericParameters[1]));
				owner.cameras.get(0).worldLocation = new Location(command.numericParameters[0], command.numericParameters[1]);
				break;
			case "changeAbilityLevel":
				owner.gui.out("Executing command: " + command.commandName);
				((Ability)owner.hero.abilities.get((int) command.numericParameters[0])).level += command.numericParameters[1];
				break;
			case "changeHeroStat":
				owner.gui.out("Executing command: " + command.commandName);
				if(command.parameterTypes[0] == OldCommand.ParameterType.NUMERIC){
					owner.gui.out("The stat: " + command.numericParameters[0] + " was changed from " + owner.hero.getStat((int)command.numericParameters[0]) + " to ");
					owner.hero.changeStat((int)command.numericParameters[0], command.numericParameters[1]);
					owner.gui.out(String.valueOf(owner.hero.getStat((int)command.numericParameters[0])));
				}
				else{
					owner.gui.out("With the alphabetic parameter: " + command.alphabeticParameters[0]);
					if(Constants.convertString(command.alphabeticParameters[0]) == -1){
						owner.gui.out("Invalid parameter: " + command.alphabeticParameters[0]);
						owner.gui.out("Parameter: " + command.alphabeticParameters[0] + " either does not exist or has not been accounted for.");
					}
					owner.gui.out("The stat: " + command.alphabeticParameters[0] + " was changed from "
					+ owner.hero.getStat(Constants.convertString(command.alphabeticParameters[0])) + " to ");
					owner.hero.changeStat(Constants.convertString(command.alphabeticParameters[0]), command.numericParameters[0]);
					owner.gui.out(String.valueOf(owner.hero.getStat(Constants.convertString(command.alphabeticParameters[0]))));
				}
				break;
			case "respawn":
				owner.respawn();
				break;
			case "using":
				owner.gui.out("Executing command: " + command.commandName);
				if(command.alphabeticParameters[0].equals("system_old")){
					execution = 0;
					owner.gui.out("Changed the command execution system to default.");
				}
				else if(command.alphabeticParameters[0].equals("system_complex")){
					execution = 1;
					owner.gui.out("Changed the command execution system to complex.");
				}
				else owner.gui.out("Invalid parameter");
				break;
			default:
				owner.gui.out("Command: " + command.commandName + " does not exist");
				return;
			}
		}catch(Throwable t){
			throw new ObsoleteCommandException(engine, "Function was not able to be excecuted");
		}
	}
	private class Parser{
		class IdentifierException extends Throwable{
			String type;
			IdentifierException(String string){
				type = string;
			}
			public String getLocalizedMessage(){
				return type;
			}
		}
		private class Command {
			boolean master;
			String string;
			Parser owner;
			String[] params;
			int myLocation;
			int numParams;
			String[] readReadyCommand;
			Command(Parser owner, boolean master, String string, int myLocation)throws IdentifierException{
				this.owner = owner;
				this.master = master;
				this.string = string;
				this.myLocation = myLocation;
				numParams = owner.numParams.get(string) != null ? owner.numParams.get(string): -1;
				if(numParams == -1)throw new IdentifierException("command name not recognized");
				owner.owner.out("A command was read as: " + string + 
						"\nThe number of parameters that it has is: " + numParams);
				params = new String[numParams];
				considerParams();
				considerCommand();
			}
			private void considerParams(){
				int atLocation = myLocation + string.length() + 1;
				owner.owner.out("Starting to look for parameters at character number: " + atLocation);
				for(int i = 0; i < params.length;i++){
					params[i] = owner.readUntilBlank(atLocation);
					owner.owner.out("Parameter number " + (i+1) + " was read as " + params[i] + " and its size is: " + params[i].length());
					atLocation += params[i].length() + 1;
				}
			}
			private void considerCommand(){
				readReadyCommand = new String[numParams+1];
				readReadyCommand[0] = string;
				for(int i = 0; i < numParams; i++)
					readReadyCommand[i+1] = params[i];
			}
		}
		final char[]  identifiers = {'!','@', '>', '~', '*', '|'};
		HashMap<String, Integer> numParams = new HashMap<String, Integer>();
		Console owner;
		String string;
		int[] identifierLocations;
		ArrayList<Command> commands = new ArrayList<Command>();
		Command masterCommand;
		Parser(Console owner){
			this.owner = owner;
			numParams.put("consider", 1);
			numParams.put("using", 1);
			numParams.put("move", 2);
			numParams.put("dumpVOs", 0);
		}
		void parseString(String string) throws IdentifierException{
			this.string = string;
			identifierLocations = preParse(string);
			readIdentifiers(identifierLocations);
		}
		int[] preParse(String string) throws IdentifierException{
			int[] ia;
			char[] saca = string.toCharArray();
			int numIdentifiers = 0;
			for(char c: saca)for(char d: identifiers)if(c == d)numIdentifiers++;
			if(numIdentifiers == 0)throw new IdentifierException("No identifiers were found");
			owner.out("The number of identifiers that were found is: " + numIdentifiers);
			ia = new int[numIdentifiers];
			int i = 0;
			for(int j = 0; j < saca.length; j++)for(char d: identifiers)if(saca[j] == d)ia[i++] = j;
			return ia;
		}
		void readIdentifiers(int[] identifierLocations)throws IdentifierException{
			for(int i: identifierLocations){
				switch(string.toCharArray()[i]){
				case '!':
					owner.out("Command operator on character #" + i);
					if(string.toCharArray()[i+1] == '>'){
						owner.out("This is a master command");
						masterCommand = new Command(this, true, readUntilBlank(i + 2), i+2);
					} 
					else if(Character.isAlphabetic(string.toCharArray()[i + 1]))
						commands.add(new Command(this, true, readUntilBlank(i+1), i + 1));
					else throw new IdentifierException("Incorrect command input. Alphabetic command or "
							+ "the master command operator '>' expected");
					break;
				}
			}
		}
		String readUntilBlank(int startingLocation){
			String r = "";
			while(!Character.isWhitespace(string.toCharArray()[startingLocation]))
				r += string.toCharArray()[startingLocation++];
			return r;
		}
	}
	public class Executer{
		public class CommandExecutionException extends Throwable{
			String type;
			CommandExecutionException(String type){
				this.type = "A command execution exception has occured with the following message:\n" + type;
			}
			public String getLocalizedMessage(){
				return type;
			}
		}
		protected Console owner;
		public Executer(Console owner){
			this.owner = owner;
		}
		public boolean execute(String[] readReadyCommand) throws CommandExecutionException{
			owner.out("about to execute a command named: " + readReadyCommand[0]);
			switch(readReadyCommand[0]){
			case "consider":
				Actor actor = new Actor(owner.engine);
				if(readReadyCommand[1].equals("self"))
					actor = owner.self;
				else throw new CommandExecutionException("unknown consideration target: " + readReadyCommand[1]);
				owner.out("Considering " + readReadyCommand[1]);
				inMind.add(actor);
				break;
			case "using":
				if(readReadyCommand[1].equals("system_old")){
					owner.owner.gui.out("Changed the command execution system to default");
					owner.owner.gui.out("The usage of this command system is not recommended as it "
							+ "is no longer supported and is likely to cause an error");
					owner.owner.gui.out("type 'using system_complex' to return");
					execution = 0;
				}
				else if(readReadyCommand[1].equals("system_complex")){
					owner.owner.gui.out("Changed the command execution system to complex");
					execution = 1;
				}
				else throw new CommandExecutionException("Invalid parameter: " + readReadyCommand[1]);
			break;
			case "move":
				for(Actor a: inMind){
					double x = 0,y = 0;
					try{
						if(readReadyCommand[1] == null || readReadyCommand[2] == null)
							throw new CommandExecutionException("Missing parameter: " + 
						readReadyCommand[1] == null ? "x":"y");
						x = Double.parseDouble(readReadyCommand[1]);
						y = Double.parseDouble(readReadyCommand[2]);
					}catch(NumberFormatException e){
						throw new CommandExecutionException("Incorrect parameter input! "
								+ "Parameters must be numeric.");
					}
					out("Performing one movement action in the direction of the given coordinates");
					a.goTo(new Location(x,y));
					a.move();
				}
				break;
			case "dumpVOs":
				for(VisibleObject vo: owner.engine.visibleObjects)
					owner.engine.tracker.log(vo.toString());
				break;
			default:
				throw new CommandExecutionException("Executer did not recognize the command");
			}
			return true;
		}
	}
	public void out(Location location) {
		out(location.toString());
	}
}
