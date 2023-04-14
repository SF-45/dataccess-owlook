package space.sadfox.dataccess.command;

@FunctionalInterface
public interface CommandCreator {
	Command createCommand(CommandEntity commandEntity);
}
