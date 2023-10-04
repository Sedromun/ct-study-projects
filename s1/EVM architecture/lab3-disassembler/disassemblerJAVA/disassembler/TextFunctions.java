package disassembler;

import java.util.LinkedList;
import java.util.List;

public class TextFunctions {
    private final List<Command> commands;

    protected TextFunctions() {
        this.commands = new LinkedList<>();
    }

    protected void addCommand(Command command) {
        commands.add(command);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(".text");
        for(Command command : commands) {
            sb.append(command.toString());
        }
        return sb.toString();
    }

}
