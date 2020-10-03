package engine;

import math.Vector2;

public class Rect {
    public float x;
    public float y;
    public float width;
    public float height;

    //Constructor that sets position and size to 0
    public Rect() {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    //Constructor that sets position and size using Vector2's
    public Rect(Vector2 pos, Vector2 scale) {
        x = pos.x;
        y = pos.y;
        width = scale.x;
        height = scale.y;
    }

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

    //Set the position and size of the rectangle without creating a new one using vector2's
    public void Set(Vector2 pos, Vector2 size) {
        this.x = pos.x;
        this.y = pos.y;
        width = size.x;
        height = size.y;
    }

    //Get and set the size of the rectangle without creating a new one
    public void SetPosition(Vector2 pos) {
        this.x = pos.x;
        this.y = pos.y;
    }

    public void SetPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 GetPosition() {
        return new Vector2(x, y);
    }

    //Get and set the size of the rectangle without creating a new one
    public void SetSize(Vector2 size) {
        width = size.x;
        height = size.y;
    }

    public void SetSize(int w, int h) {
        width = w;
        height = h;
    }

    public Vector2 GetSize() {
        return new Vector2(width, height);
    }

    public Rect AddPosition(Rect r) {
        return new Rect(r.x + x, r.y + y, width, height);
    }

    public Rect AddPosition(Vector2 v) {
        return new Rect(v.x + x, v.y + y, width, height);
    }

    //Return whether or not a specific point is inside this rectangle
    public boolean Contains(Vector2 v) {
        return v.x > x && v.x < x + width && v.y > y && v.y < y + height;
    }

    public boolean Intersects(Rect r) {
        return !(x > r.x + r.width) && !(x + width < r.x) && !(y > r.y + r.height) && !(y + height < r.y);
    }

    public Rect GetIntersection(Rect r) {
        if (!Intersects(r)) return null;
        Vector2 v = new Vector2(Math.max(x, r.x), Math.max(y, r.y));
        return new Rect(v.x, v.y, Math.min(x + width, r.x + r.width) - v.x, Math.min(y + height, r.y + r.height) - v.y);
    }

    public String ToShortString() {
        return x + "," + y + "," + width + "," + height;
    }

    public String ToString() {
        return "(" + x + ", " + y + ", " + width + ", " + height + ")";
    }
}
