package physics;

public class BoundsCollider extends Collider {
    public BoundsCollider() {
        Collider.colliders.add(this);
    }
}
