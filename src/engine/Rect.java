package engine;

import math.Vector2;

public class Rect {
    public float x;
    public float y;
    public float width;
    public float height;

    //Constructor that sets the position and size of the rectangle
    public Rect(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
    }

    //Set the position and size of the rectangle without creating a new one
    public void Set(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
    }

    public Rect AddPosition(Rect r) {
        return new Rect(r.x + x, r.y + y, width, height);
    }

    //Return whether or not a specific point is inside this rectangle
    public boolean Contains(Vector2 v) {
        return v.x > x && v.x < x + width && v.y > y && v.y < y + height;
    }

    public boolean Intersects(Rect r) {
        if (x > r.x + r.width || x + width < r.x || y > r.y + r.height || y + height < r.y) return false;
        return true;
    }

    public Rect GetIntersection(Rect r) {
        if (!Intersects(r)) return null;
        Vector2 v = new Vector2(Math.max(x, r.x), Math.max(y, r.y));
        return new Rect(v.x, v.y, Math.min(x + width, r.x + r.width) - v.x, Math.min(y + height, r.y + r.height) - v.y);
    }
}
