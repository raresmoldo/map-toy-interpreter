package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OpenRFileStmtTest {
    @TempDir
    Path dir; // a new empty folder for every test

    private PrgState state;
    private StringValue fileName;
    private IExp file;

    @BeforeEach
    void setUp() throws Exception {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "7\n");
        fileName = new StringValue(f.toString());
        file = new ValueExp(fileName);
        state = TestUtil.emptyState();
    }

    @AfterEach
    void tearDown() throws Exception {
        TestUtil.closeFiles(state);
    }

    @Test
    void addsTheFileToTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().isDefined(fileName));
    }

    @Test
    void theFileNameCanComeFromAVariable() throws MyException {
        state.getSymTable().put("f", fileName);
        new OpenRFileStmt(new VarExp("f")).execute(state);
        assertTrue(state.getFileTable().isDefined(fileName));
    }

    @Test
    void nonStringExpressionThrows() {
        assertThrows(StatementException.class,
                () -> new OpenRFileStmt(new ValueExp(new IntValue(1))).execute(state));
    }

    @Test
    void openingTheSameFileTwiceThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        var ex = assertThrows(StatementException.class,
                () -> new OpenRFileStmt(file).execute(state));
        assertTrue(ex.getMessage().contains("already open"));
    }

    @Test
    void missingFileThrowsAndLeavesTheFileTableEmpty() {
        IExp missing = new ValueExp(new StringValue(dir.resolve("nothing.in").toString()));
        var ex = assertThrows(StatementException.class,
                () -> new OpenRFileStmt(missing).execute(state));
        assertTrue(ex.getMessage().startsWith("openRFile: cannot open"));
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void toStringShowsTheExpression() {
        assertEquals("openRFile(f)", new OpenRFileStmt(new VarExp("f")).toString());
    }
}