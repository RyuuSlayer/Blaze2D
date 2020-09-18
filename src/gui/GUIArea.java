package gui;

import engine.Rect;
import input.Mouse;
import math.Mathf;

public class GUIArea {
    public Rect area = new Rect(0, 0, 0, 0);
    public int scrollHeight;
    private int scroll = 0;

    public GUIArea(Rect area) {
        if (area != null) this.area = area;
    }

    public final int Scroll() {
        return scroll;
    }

    public final int Scroll(int offset) {
        if (area.Contains(Mouse.Position())) {
            float leftOver = scrollHeight - area.height;
            if (leftOver <= 0) scroll = 0;
            else scroll = (int) Mathf.Clamp(offset + (-Mouse.Scroll() * 10), 0, leftOver);
        } else scroll = offset;

        return scroll;
    }
}
