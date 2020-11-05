package engine;

import java.util.ArrayList;
import java.util.List;

public class Camera extends LogicBehaviour {
    private static final List<Camera> cameras = new ArrayList<Camera>();
    public Shader shader;
    public int renderLayer = 0;

    public Camera() {
        cameras.add(this);
    }

    public static void Clear() {
        cameras.clear();
    }

    public static final List<Camera> Cameras() {
        return cameras;
    }
}
