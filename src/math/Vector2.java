package math;

public class Vector2 {
    public float x;
    public float y;

    public Vector2() {
        x = 0;
        y = 0;
    }

    public Vector2(Vector2 v) {
        x = v.x;
        y = v.y;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void Set(Vector2 v) {
        x = v.x;
        y = v.y;
    }

    public void Set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 Add(float v) {
        return new Vector2(x + v, y + v);
    }

    public Vector2 Add(Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }

    public Vector2 Add(float x, float y) {
        return new Vector2(this.x + x, this.y + y);
    }

    public Vector2 Sub(float v) {
        return new Vector2(x - v, y - v);
    }

    public Vector2 Sub(Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }

    public Vector2 Sub(float x, float y) {
        return new Vector2(this.x - x, this.y - y);
    }

    public Vector2 Mul(float v) {
        return new Vector2(x * v, y * v);
    }

    public Vector2 Mul(Vector2 v) {
        return new Vector2(x * v.x, y * v.y);
    }

    public Vector2 Mul(float x, float y) {
        return new Vector2(this.x * x, this.y * y);
    }

    public Vector2 Div(float v) {
        return new Vector2(x / v, y / v);
    }

    public Vector2 Div(Vector2 v) {
        return new Vector2(x / v.x, y / v.y);
    }

    public Vector2 Div(float x, float y) {
        return new Vector2(this.x / x, this.y / y);
    }

    public Vector2 Neg() {
        return new Vector2(-x, -y);
    }

    public String ToString() {
        return "(" + x + ", " + y + ")";
    }
}