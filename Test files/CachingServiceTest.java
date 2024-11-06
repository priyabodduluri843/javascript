import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CachingServiceTest {

    private DatabaseService databaseService;
    private CachingService cachingService;

    @BeforeEach
    void setUp() {
        databaseService = mock(DatabaseService.class);
        cachingService = new CachingService(2, databaseService);
    }

    @Test
    void testAddEntity() {
        Entity e1 = new Entity("1");
        cachingService.add(e1);
        assertEquals(e1, cachingService.get(e1));
        verify(databaseService, never()).saveToDatabase(any());
    }

    @Test
    void testRemoveAllEntities() {
        Entity e1 = new Entity("1");
        Entity e2 = new Entity("2");
        cachingService.add(e1);
        cachingService.add(e2);
        cachingService.removeAll();

        assertNull(cachingService.get(e1));
        assertNull(cachingService.get(e2));
        verify(databaseService).removeFromDatabase(e1);
        verify(databaseService).removeFromDatabase(e2);
    }

    @Test
    void testGetEntityFromCache() {
        Entity e1 = new Entity("1");
        cachingService.add(e1);
        assertEquals(e1, cachingService.get(e1));
        verify(databaseService, never()).getFromDatabase(any());
    }
}
