```java

class Main() {
    public static void main(String[] args) {
        EntityManager manager = new EntityManager();

        Entity zombie = manager.createEntity();
        zombie.addComponent(new Velocity(0, 0, 0));
        zombie.addComponent(new Attack(5));
        zombie.addComponent(new Health(10));
        zombie.addComponent(new Shape(...));
        // ...

        ArrayList<System> systems = new ArrayList<>();
        systems.add(PhysicsSystem.getInstance());
        systems.add(ChunkRenderSystem.getInstance());
        systems.add(GameObjectRenderSystem.getInstance());
        systems.add(RigidBodySystem.getInstance());
        
        // ...
    }
}

//...
public class System {
    public class RenderSystem extends System {
        
        @Override 
        public void update(float dt, EntityManager) {
            
        }
    }
}

```