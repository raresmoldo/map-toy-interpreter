package interpreter.repository;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.MyException;
import interpreter.exceptions.RepositoryException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RepositoryTest {
    @TempDir
    Path dir;

    private PrgState state;
    private Path log;
    private IRepository repo;

    @BeforeEach
    void setUp() {
        state = TestUtil.stateFor(new NopStmt());
        log = dir.resolve("log.txt");
        repo = new Repository(state, log.toString());
    }

    @Test
    void getCrtPrgReturnsTheStateGivenAtConstruction() {
        assertSame(state, repo.getCrtPrg());
    }

    @Test
    void logWritesTheCurrentState() throws Exception {
        repo.logPrgStateExec();
        assertEquals(state.toLogString() + "\n", Files.readString(log));
    }

    @Test
    void logAppendsToTheExistingFile() throws Exception {
        repo.logPrgStateExec();
        state.getOut().add(new IntValue(1));
        repo.logPrgStateExec();
        String content = Files.readString(log);
        assertEquals(2, content.split("ExeStack:", -1).length - 1);
        assertTrue(content.contains("Out:\n1\n"));
    }

    @Test
    void anUnwritableLogFileThrowsARepositoryException() {
        // a folder cannot be opened as a file
        IRepository bad = new Repository(state, dir.toString());
        var ex = assertThrows(RepositoryException.class, bad::logPrgStateExec);
        assertTrue(ex.getMessage().startsWith("cannot write the log file"));
    }

    @Test
    void repositoryExceptionIsAMyException() {
        assertTrue(MyException.class.isAssignableFrom(RepositoryException.class));
    }
}