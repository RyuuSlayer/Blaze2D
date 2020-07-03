package gui;

public class Glyph {
    public final float x;
    public final float y;
    public final float w;
    public final float h;
    public final float scaleX;
    public final float scaleY;

    //Constructor for holding glyph information of a character in a font
    public Glyph(float x, float y, float w, float h, float sX, float sY) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        scaleX = sX;
        scaleY = sY;
    }
}