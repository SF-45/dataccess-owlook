package space.sadfox.dataccess.command;

import space.sadfox.dataccess.command.cmd.ExecCommand;

public enum CommandTypes {
	EXEC((e) -> new ExecCommand(e));
	
	private CommandCreator commandCreator;

	private CommandTypes(CommandCreator commandCreator) {
		this.commandCreator = commandCreator;
	}
	
	public Command createCommand(CommandEntity commandEntity) {
		return commandCreator.createCommand(commandEntity);
	}
	
	
}
