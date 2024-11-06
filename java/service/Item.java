public class Entity {

    private final String id;

    public Entity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Entity{id='" + id + "'}";
    }
}
