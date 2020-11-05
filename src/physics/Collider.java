package physics;

import engine.LogicBehaviour;
import engine.SpriteRenderer;
import math.Rect;
import math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Collider extends LogicBehaviour {
	private static final List<Collider> movers = new ArrayList<Collider>();
	private static final List<Collider> solids = new ArrayList<Collider>();
	static List<Collider> colliders = new ArrayList<Collider>();
	public boolean isTrigger = false;

	public static void Clear() {
		colliders.clear();
	}

	public static void ClearFrame() {
		movers.clear();
		solids.clear();
	}

	public static void AddFrameCollider(Collider c) {
		if (c == null) return;

		//isDirty is resetting somewhere for some reason
		if (c.gameObject.isDirty()) movers.add(c);
		else solids.add(c);
	}

	public static void CompareCollisions() {
		for (int m = 0; m < movers.size(); m++) {
			Collider mover = movers.get(m);
			for (int s = 0; s < solids.size(); s++) {
				Collider solid = solids.get(s);
				if (mover instanceof BoundsCollider && solid instanceof BoundsCollider)
					CompareCollision((BoundsCollider) mover, (BoundsCollider) solid);
				if (mover instanceof AABBCollider && solid instanceof AABBCollider)
					CompareCollision((AABBCollider) mover, (AABBCollider) solid);
				else if (mover instanceof CircleCollider && solid instanceof CircleCollider)
					CompareCollision((CircleCollider) mover, (CircleCollider) solid);
			}
			mover.gameObject.ResetDirty();
		}
	}

	private static void CompareCollision(CircleCollider mover, CircleCollider solid) {
		Vector2 moverCenter = mover.gameObject.Position().Add((mover.anchor));
		Vector2 solidCenter = solid.gameObject.Position().Add((solid.anchor));

		float distance = solidCenter.Sub(moverCenter).Length();

		if (distance >= mover.radius + solid.radius) return;

		//Collision point = solid - (direction to mover * radius);
		if (mover.isTrigger || solid.isTrigger) {
			mover.gameObject.CallTriggerCallback();
			solid.gameObject.CallTriggerCallback();
		} else {
			mover.gameObject.CallCollisionCallback();
			solid.gameObject.CallCollisionCallback();
			mover.gameObject.Move(moverCenter.Sub(solidCenter).Normalized().Mul((mover.radius + solid.radius) - distance));
		}
	}

	private static void CompareCollision(AABBCollider mover, AABBCollider solid) {
		Vector2 moverCorner = new Vector2(mover.gameObject().Position().Add(mover.anchor));
		Rect moverRect = new Rect(moverCorner, mover.size);

		Vector2 solidCorner = new Vector2(solid.gameObject().Position().Add(solid.anchor));
		Rect solidRect = new Rect(solidCorner, solid.size);

		Rect overlap = moverRect.GetIntersection(solidRect);
		if (overlap != null) {
			if (mover.isTrigger || solid.isTrigger) {
				mover.gameObject.CallTriggerCallback();
				solid.gameObject.CallTriggerCallback();
			} else {
				mover.gameObject.CallCollisionCallback();
				solid.gameObject.CallCollisionCallback();

				if (overlap.width < overlap.height) {
					if (moverRect.x < overlap.x) mover.gameObject.Move(new Vector2(-overlap.width, 0));
					else mover.gameObject.Move(new Vector2(overlap.width, 0));
				} else {
					if (moverRect.y < overlap.y) mover.gameObject.Move(new Vector2(0, -overlap.height));
					else mover.gameObject.Move(new Vector2(0, overlap.height));
				}
			}
		}
	}


	private static void CompareCollision(BoundsCollider mover, BoundsCollider solid) {
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
			if (mover.isTrigger || solid.isTrigger) {
				mover.gameObject.CallTriggerCallback();
				solid.gameObject.CallTriggerCallback();
			} else {
				mover.gameObject.CallCollisionCallback();
				solid.gameObject.CallCollisionCallback();

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
}
