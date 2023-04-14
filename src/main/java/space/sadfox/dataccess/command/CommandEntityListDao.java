package space.sadfox.dataccess.command;

import java.util.List;
import java.util.stream.Collectors;


public class CommandEntityListDao {

	private CommandEntityList commandEntityList;

	public CommandEntityListDao(CommandEntityList commandEntityList) {
		this.commandEntityList = commandEntityList;
	}

	public List<Command> getCommands() {
		return commandEntityList.getCommands().stream().map(ent -> ent.getCommandType().createCommand(ent))
				.collect(Collectors.toList());
	}

	public static Command getCommand(CommandEntity commandEntity) {
		return commandEntity.getCommandType().createCommand(commandEntity);
	}

	public CommandEntity createCommandEntity(String name, CommandTypes type) {
		CommandEntity commandEntity = new CommandEntity();
		commandEntity.setName(name);
		commandEntity.setCommandType(type);
		commandEntityList.getCommands().add(commandEntity);
		return commandEntity;
	}

	public CommandEntityList getCommandEntityList() {
		return commandEntityList;
	}
	
	

}
