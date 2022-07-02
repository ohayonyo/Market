package DAL;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class RepoMock {

    public static void enable() {
        EntityManager em = mock(EntityManager.class);
        EntityTransaction et = mock(EntityTransaction.class);
        doReturn(et).when(em).getTransaction();
        Repo.set(mock(Repo.class), em, et);
    }

    public static void disable() {
        Repo.set(null, null, null);
    }
}
