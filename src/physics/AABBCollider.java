package physics;

import math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class AABBCollider extends Collider {
    public Vector2 size = new Vector2(16, 16);
    public Vector2 anchor = new Vector2();

    public List<Vector2> GetCorners() {
        List<Vector2> corners = new ArrayList<Vector2>();
        Vector2 anchorPos = gameObject.Position().Add(anchor);
        corners.add(anchorPos);
        corners.add(anchorPos.Add(new Vector2(0, size.y)));
        corners.add(anchorPos.Add(size));
        corners.add(anchorPos.Add(new Vector2(size.x, 0)));
        return corners;
    }
}
