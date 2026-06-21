package network;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private Command command;
    private Object data;

    public Request(Command command, Object data) {
        this.command = command;
        this.data = data;
    }

    public Command getCommand() {
        return command;
    }

    public Object getData() {
        return data;
    }
}