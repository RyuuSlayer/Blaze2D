package engine;

public abstract class LogicBehaviour {
    private final String name;
    public boolean enabled = true;
    protected GameObject gameObject;

    public LogicBehaviour() {
        name = getClass().getSimpleName();
    }

    public void Init(GameObject g) {
        if (gameObject == null) gameObject = g;
    }

    public void Awake() {
    }

    public void Update() {
    }

    public void OnWillRender() {
    }

    public void OnGUI() {
    }

    public void print(String s) {
        System.out.println(s);
    }

    public final GameObject gameObject() {
        return gameObject;
    }

    public String Name() {
        return name;
    }
}
