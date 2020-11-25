package physics;

import math.Vector2;

public class CircleCollider extends Collider {
    public float radius = 16;
    public Vector2 anchor = new Vector2();

    public Vector2 Center() {
        return gameObject.Position().Add(anchor);
    }
}
