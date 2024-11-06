import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    public void saveToDatabase(Entity e) {
        try {
            logger.info("Saving to database: {}", e);
            // Simulate saving to a database (could be a DB call here)
            if (e.getId().equals("error")) {  // Simulate an error condition
                throw new DatabaseException("Error while saving entity to the database");
            }
        } catch (Exception ex) {
            logger.error("Error saving entity to database: {}", e, ex);
            throw new DatabaseException("Error saving entity to the database", ex);
        }
    }

    public void removeFromDatabase(Entity e) {
        try {
            logger.info("Removing from database: {}", e);
            // Simulate database removal (could be a DB call here)
            if (e.getId().equals("error")) {  // Simulate an error condition
                throw new DatabaseException("Error while removing entity from the database");
            }
        } catch (Exception ex) {
            logger.error("Error removing entity from database: {}", e, ex);
            throw new DatabaseException("Error removing entity from the database", ex);
        }
    }

    public Entity getFromDatabase(String id) {
        try {
            logger.info("Fetching from database for id: {}", id);
            // Simulate a database fetch
            if (id.equals("error")) {
                throw new DatabaseException("Error while fetching entity from the database");
            }
            return new Entity(id);
        } catch (Exception ex) {
            logger.error("Error fetching entity from database with id: {}", id, ex);
            throw new DatabaseException("Error fetching entity from the database", ex);
        }
    }
}


