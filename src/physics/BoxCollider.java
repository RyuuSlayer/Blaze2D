package physics;

public class BoxCollider extends Collider {
    public boolean isTrigger = false;

    public BoxCollider() {
        Collider.colliders.add(this);
    }
}
