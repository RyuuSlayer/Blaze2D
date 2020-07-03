package editor;


import engine.GameObject;
import engine.Rect;
import gui.GUI;
import input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class Hierarchy {
    private int scroll;

    public Hierarchy() {

    }

    public void Render(Rect r) {
        List<GameObject> loop = new ArrayList<>();
        int offset = 0;
        loop.add(GameObject.Master());
        int i;
        while (loop.size() > 0) {
            GameObject g = loop.get(0);
            List<GameObject> children = g.Children();

            if (loop.size() > 0) {
                if (g.Expanded()) for (i = 0; i < children.size(); i++) loop.add(0, children.get(i));
            }
            if (g == GameObject.Master()) {
                loop.remove(loop.size() - 1);
                continue;
            }

            offset += 20;
            loop.remove(g);
        }
        scroll = GUI.SetScrollView(offset, scroll);

        List<GameObject> updateList = new ArrayList<>();
        int offsetY = 0;

        updateList.add(GameObject.Master());
        while (updateList.size() > 0) {
            GameObject g = updateList.get(0);
            List<GameObject> children = g.Children();

            if (children.size() > 0) {
                if (g.Expanded()) for (i = 0; i < children.size(); i++) updateList.add(0, children.get(i));
            }
            if (g == GameObject.Master()) {
                updateList.remove(updateList.size() - 1);
                continue;
            }

            float inline = (g.Inline()) * 16;
            Rect clickRect = new Rect(0, offsetY, r.width, 20);

            GameObject selected = Editor.GetSelected();
            if (selected != null) {
                if (g == selected) GUI.Box(clickRect, "Box");
            }

            if (children.size() > 0) {
                g.Expand(GUI.Toggle(g.Expanded(), inline, offsetY + 2, Editor.arrowDown, Editor.arrowRight));
                GUI.Label(g.name, inline + 15, offsetY);
            } else GUI.Label(g.name, inline, offsetY);

            clickRect.Set(0, (r.y + offsetY) - scroll, r.width, 20);

            if (clickRect.Contains(Mouse.Position())) {
                if (Mouse.GetButtonDown(0)) Editor.SetSelected(g);
            }

            offsetY += 20;
            updateList.remove(g);
        }
    }
}