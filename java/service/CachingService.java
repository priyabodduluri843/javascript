import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import java.util.Map;

@Service
public class CachingService {

    private static final Logger logger = LoggerFactory.getLogger(CachingService.class);

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
        try {
            if (cache.size() >= maxSize) {
                evict();
            }
            cache.put(e1.getId(), e1);
            accessOrder.addLast(e1.getId());
            logger.info("Added entity to cache: {}", e1);
        } catch (Exception ex) {
            logger.error("Error adding entity to cache: {}", e1, ex);
            throw new CacheEvictionException("Error adding entity to cache", ex);
        }
    }

    public void remove(Entity e1) {
        try {
            cache.remove(e1.getId());
            accessOrder.remove(e1.getId());
            databaseService.removeFromDatabase(e1);
            logger.info("Removed entity from cache and database: {}", e1);
        } catch (Exception ex) {
            logger.error("Error removing entity from cache and database: {}", e1, ex);
            throw new CacheEvictionException("Error removing entity from cache", ex);
        }
    }

    public void removeAll() {
        try {
            for (String id : accessOrder) {
                Entity entity = cache.get(id);
                if (entity != null) {
                    databaseService.removeFromDatabase(entity);
                    logger.info("Removed entity from cache and database: {}", entity);
                }
            }
            cache.clear();
            accessOrder.clear();
        } catch (Exception ex) {
            logger.error("Error removing all entities from cache", ex);
            throw new CacheEvictionException("Error removing all entities from cache", ex);
        }
    }

    public Entity get(Entity e1) {
        try {
            if (cache.containsKey(e1.getId())) {
                accessOrder.remove(e1.getId());
                accessOrder.addLast(e1.getId());
                logger.info("Fetched entity from cache: {}", e1);
                return cache.get(e1.getId());
            }

            logger.info("Entity not found in cache. Fetching from database: {}", e1);
            Entity entity = databaseService.getFromDatabase(e1.getId());
            if (entity != null) {
                add(entity);
            }
            return entity;
        } catch (Exception ex) {
            logger.error("Error fetching entity: {}", e1, ex);
            throw new CacheEvictionException("Error fetching entity from cache or database", ex);
        }
    }

    public void clear() {
        try {
            cache.clear();
            accessOrder.clear();
            logger.info("Cleared entire cache.");
        } catch (Exception ex) {
            logger.error("Error clearing the cache", ex);
            throw new CacheEvictionException("Error clearing the cache", ex);
        }
    }

    private void evict() {
        try {
            String oldestKey = accessOrder.removeFirst();
            Entity oldestEntity = cache.remove(oldestKey);
            databaseService.saveToDatabase(oldestEntity);
            logger.info("Evicted entity from cache: {}", oldestEntity);
        } catch (Exception ex) {
            logger.error("Error evicting entity from cache", ex);
            throw new CacheEvictionException("Error evicting entity from cache", ex);
        }
    }
}
