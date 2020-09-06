package physics;

import engine.LogicBehaviour;
import engine.Rect;
import engine.SpriteRenderer;
import math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Collider extends LogicBehaviour {
	private static final List<Collider> movers = new ArrayList<Collider>();
	private static final List<Collider> solids = new ArrayList<Collider>();
	static List<Collider> colliders = new ArrayList<Collider>();

	public static void Clear() {
		colliders.clear();
	}

	public static void ClearFrame() {
		movers.clear();
		solids.clear();
	}

	public static void AddFrameCollider(Collider c) {
		if (c == null || c.gameObject.GetRenderer() == null) return;

		if (c.gameObject.isDirty()) movers.add(c);
		else solids.add(c);
	}

	public static void ResolveCollisions() {
		for (int m = 0; m < movers.size(); m++) {
			Collider mover = movers.get(m);
			for (int s = 0; s < solids.size(); s++) {
				ResolveCollision((BoxCollider) mover, (BoxCollider) solids.get(s));
			}
			mover.gameObject.ResetDirty();
		}
	}

	private static void ResolveCollision(BoxCollider mover, BoxCollider solid) {
		SpriteRenderer moverRenderer = mover.gameObject.GetRenderer();
		SpriteRenderer solidRenderer = solid.gameObject.GetRenderer();
		if (moverRenderer == null || solidRenderer == null) return;
		if (moverRenderer.sprite == null || solidRenderer.sprite == null) return;

		Vector2[] poly1 = moverRenderer.GetCorners();
		Vector2[] poly2 = solidRenderer.GetCorners();

		Rect poly1Bounds = new Rect(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		Rect poly2Bounds = new Rect(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		for (int i = 0; i < 4; i++) {
			poly1Bounds.x = Math.min(poly1Bounds.x, poly1[i].x);
			poly2Bounds.x = Math.min(poly2Bounds.x, poly2[i].x);

			poly1Bounds.width = Math.max(poly1Bounds.width, poly1[i].x);
			poly2Bounds.width = Math.max(poly2Bounds.width, poly2[i].x);

			poly1Bounds.y = Math.min(poly1Bounds.y, poly1[i].y);
			poly2Bounds.y = Math.min(poly2Bounds.y, poly2[i].y);

			poly1Bounds.height = Math.max(poly1Bounds.height, poly1[i].y);
			poly2Bounds.height = Math.max(poly2Bounds.height, poly2[i].y);
		}
		poly1Bounds.width = poly1Bounds.width - poly1Bounds.x;
		poly2Bounds.width = poly2Bounds.width - poly2Bounds.x;
		poly1Bounds.height = poly1Bounds.height - poly1Bounds.y;
		poly2Bounds.height = poly2Bounds.height - poly2Bounds.y;

		Rect overlap = poly1Bounds.GetIntersection(poly2Bounds);
		if (overlap != null) {
			if (overlap.width < overlap.height) {
				if (poly1[0].x < overlap.x) mover.gameObject.Move(new Vector2(-overlap.width, 0));
				else mover.gameObject.Move(new Vector2(overlap.width, 0));
			} else {
				if (poly1[0].y < overlap.y) mover.gameObject.Move(new Vector2(0, -overlap.height));
				else mover.gameObject.Move(new Vector2(0, overlap.height));
			}
		}
	}
}
