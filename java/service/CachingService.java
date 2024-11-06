import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import java.util.Map;

@Service
public class CachingService {

    private final int maxSize;
    private final Map<String, Entity> cache;
    private final LinkedList<String> accessOrder;
    private final DatabaseService databaseService;

    @Autowired
    public CachingService(int maxSize, DatabaseService databaseService) {
        this.maxSize = maxSize;
        this.databaseService = databaseService;
        this.cache = new ConcurrentHashMap<>();
        this.accessOrder = new LinkedList<>();
    }

    public void add(Entity e1) {
        if (cache.size() >= maxSize) {
            evict();
        }
        cache.put(e1.getId(), e1);
        accessOrder.addLast(e1.getId());
    }

    public void remove(Entity e1) {
        cache.remove(e1.getId());
        accessOrder.remove(e1.getId());
        databaseService.removeFromDatabase(e1);
    }

    public void removeAll() {
        for (String id : accessOrder) {
            databaseService.removeFromDatabase(cache.get(id));
        }
        cache.clear();
        accessOrder.clear();
    }

    public Entity get(Entity e1) {
        if (cache.containsKey(e1.getId())) {
            accessOrder.remove(e1.getId());
            accessOrder.addLast(e1.getId());
            return cache.get(e1.getId());
        }
        Entity entity = databaseService.getFromDatabase(e1.getId());
        if (entity != null) {
            add(entity);
        }
        return entity;
    }

    public void clear() {
        cache.clear();
        accessOrder.clear();
    }

    private void evict() {
        String oldestKey = accessOrder.removeFirst();
        Entity oldestEntity = cache.remove(oldestKey);
        databaseService.saveToDatabase(oldestEntity);
    }
}
