package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.repository.IRepository;
import java.io.PrintStream;

public class Controller {
    private final IRepository repo;
    private final boolean displayFlag;
    private final PrintStream output;

    public Controller(IRepository repo, boolean displayFlag) {
        this(repo, displayFlag, System.out);
    }

    public Controller(IRepository repo, boolean displayFlag, PrintStream output) {
        this.repo = repo;
        this.displayFlag = displayFlag;
        this.output = output;
    }

    /** Rule S7: executes the statement on top of the ExeStack (one of S1-S6). */
    public PrgState oneStep(PrgState state) throws MyException {
        var stack = state.getExeStack();
        if (stack.isEmpty()) throw new StatementException("execution stack is empty");
        IStmt crtStmt = stack.pop();
        return crtStmt.execute(state);
    }

    public void allStep() throws MyException {
        PrgState prg = repo.getCrtPrg();
        display(prg);
        while (!prg.getExeStack().isEmpty()) {
            oneStep(prg);
            display(prg);
        }
    }

    private void display(PrgState state) {
        if (displayFlag) {
            output.println(state);
            output.println();
        }
    }
}