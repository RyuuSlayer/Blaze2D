package math;

public class Vector2 {
    public float x;
    public float y;

    public Vector2() {
        x = 0;
        y = 0;
    }

    public Vector2(float v) {
        x = v;
        y = v;
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

    public static final Vector2 up = new Vector2(0, 1);

    public float Length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public static final Vector2 down = new Vector2(0, -1);

    public Vector2 Floor() {
        return new Vector2((float) Math.floor(x), (float) Math.floor(y));
    }

    public Vector2 Ceil() {
        return new Vector2((float) Math.ceil(x), (float) Math.ceil(y));
    }

    public static final Vector2 left = new Vector2(-1, 0);
    public static final Vector2 right = new Vector2(1, 0);

    public static float Dot(Vector2 v1, Vector2 v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public static float Distance(Vector2 v1, Vector2 v2) {
        return (v1.Sub(v2)).Length();
    }

    public static float Angle(Vector2 from, Vector2 to) {
        return (float) (Math.acos(Mathf.Clamp(Vector2.Dot(from.Normalized(), to.Normalized()), -1f, 1f)) * 57.29578f);
    }

    public static Vector2 Lerp(Vector2 v1, Vector2 v2, float time) {
        return new Vector2(v1.x + (v2.x - v1.x) * time, v1.y + (v2.y - v1.y) * time);
    }

    public static Vector2 Reflect(Vector2 dir, Vector2 normal) {
        return normal.Mul(-2f * Dot(normal, dir)).Add(dir);
    }

    public float Dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    public float SqrLength() {
        return x * x + y * y;
    }

    public Vector2 Normalized() {
        Vector2 v = new Vector2(this);
        v.Normalize();
        return v;
    }

    public void Normalize(Vector2 v) {
        float normal = (float) (1.0 / Math.sqrt(v.x * v.x + v.y * v.y));
        x = v.x * normal;
        y = v.y * normal;
    }

    public void Normalize() {
        float normal = (float) (1.0 / Math.sqrt(x * x + y * y));
        x *= normal;
        y *= normal;
    }

    public Vector2 Neg() {
        return new Vector2(-x, -y);
    }

    public boolean Equals(Vector2 v) {
        return v.x == x && v.y == y;
    }

    public String ToShortString() {
        return x + ", " + y;
    }

    public String ToString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Vector2)) return false;

        Vector2 v = (Vector2) o;
        return v.x == x && v.y == y;
    }
}
