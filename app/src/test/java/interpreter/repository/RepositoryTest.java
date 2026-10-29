package interpreter.repository;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import org.junit.jupiter.api.Test;

class RepositoryTest {
    @Test
    void getCrtPrgReturnsTheStateGivenAtConstruction() {
        PrgState state = TestUtil.stateFor(new NopStmt());
        IRepository repo = new Repository(state);
        assertSame(state, repo.getCrtPrg());
    }
}