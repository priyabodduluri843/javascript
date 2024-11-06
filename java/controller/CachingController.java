import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
public class CachingController {

    private final CachingService cachingService;

    @Autowired
    public CachingController(CachingService cachingService) {
        this.cachingService = cachingService;
    }

    @PostMapping("/add")
    public void addEntity(@RequestBody Entity entity) {
        cachingService.add(entity);
    }

    @GetMapping("/get/{id}")
    public Entity getEntity(@PathVariable String id) {
        return cachingService.get(new Entity(id));
    }

    @DeleteMapping("/remove/{id}")
    public void removeEntity(@PathVariable String id) {
        cachingService.remove(new Entity(id));
    }

    @DeleteMapping("/removeAll")
    public void removeAllEntities() {
        cachingService.removeAll();
    }

    @DeleteMapping("/clear")
    public void clearCache() {
        cachingService.clear();
    }
}


