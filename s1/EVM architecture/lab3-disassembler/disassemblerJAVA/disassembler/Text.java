package disassembler;

import java.util.LinkedList;
import java.util.List;

public class Text {
    private final List<Command> commands;

    protected Text() {
        this.commands = new LinkedList<>();
    }

    protected void addCommand(Command command) {
        commands.add(command);
    }

    protected void addCommand(int pos, Command command) {
        commands.add(pos, command);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(".text\n");
        for(Command command : commands) {
            sb.append(command.toString());
        }
        return sb.toString();
    }
}
