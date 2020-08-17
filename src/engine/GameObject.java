package engine;

import editor.Editor;
import editor.EditorUtil;
import math.Mathf;
import math.Matrix4x4;
import math.Vector2;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameObject extends engine.Object {//Variables need to be after object creation and behaviour creation
	private static final GameObject master = new GameObject(false);
	private static final List<GameObject> instances = new ArrayList<GameObject>();
	private static final Matrix4x4 temp = new Matrix4x4();
	private static int h;
	private static int j;
	private final Vector2 position = new Vector2(0, 0);
	//New
	private final Vector2 localPosition = new Vector2();
	private final List<GameObject> children = new ArrayList<GameObject>();
	private final List<LogicBehaviour> components = new ArrayList<LogicBehaviour>();
	private final Matrix4x4 matrix = new Matrix4x4();
	public boolean enabled = true;
	public String tag = "Untagged";
	private Vector2 scale = new Vector2(1, 1);
	private float rotation = 0;
	private Vector2 localScale = new Vector2();
	private float localRotation = 0;
	private GameObject parent;
	private int inline = 0;
	private String id;
	private int layer = 0;
	private byte dirty = 1;
	private boolean expanded = false;
	private int i;

	public GameObject(boolean addToHierarchy) {
		id = UUID.randomUUID().toString();
		if (!addToHierarchy) {
			inline = -1;
			expanded = true;
			return;
		}
		Name("New GameObject");
		instances.add(this);
		Parent(master);
	}

	public GameObject(String name) {
		id = UUID.randomUUID().toString();
		Name(name);
		instances.add(this);
		Parent(master);
	}

	public static void Recalculate() {
		for (int i = 0; i < master.children.size(); i++) master.children.get(i).RecalculateGlobalTransformations();
	}

	public static GameObject Master() {
		return master;
	}

	static void Clear() {
		instances.clear();
		master.children.clear();
	}

	public static final List<GameObject> Instances() {
		return instances;
	}

	public static GameObject FindObjectByID(String s) {
		for (j = 0; j < instances.size(); j++) {
			GameObject g = instances.get(j);
			if (g.id.equals(s)) return g;
		}
		return null;
	}

	public static GameObject Find(String s) {
		for (j = 0; j < instances.size(); j++) {
			GameObject g = instances.get(j);
			if (g.Name().equals(s)) return g;
		}
		return null;
	}

	public static void StartAll() {
		for (h = 0; h < instances.size(); h++) {
			GameObject g = instances.get(h);
			g.Start();
		}
	}

	public static void PrepareObjects() {
		if (Editor.IsPlaying()) {
			for (h = 0; h < instances.size(); h++) {
				GameObject g = instances.get(h);
				g.Update();
			}
		}

		for (h = 0; h < instances.size(); h++) {
			GameObject g = instances.get(h);
			g.PrepareToRender();
		}
	}

	public final int GetLayer() {
		return layer;
	}

	public void SetLayer(int l) {
		layer = (int) Mathf.Clamp((float) layer, 0, Renderer.LayerCount() - 1);
	}

	public final boolean isDirty() {
		return dirty == 1;
	}

	//Get rid of update matrix because Matrix() already updates it if it's dirty when retrieved
	public final Matrix4x4 Matrix() {
		if (dirty == 1) matrix.SetTransformation(position, rotation, scale);
		dirty = 0;
		return matrix;
	}

	public final Vector2 Position() {
		return position;
	}

	public void Position(Vector2 v) {
		Position(v.x, v.y);
	}

	public void Position(float x, float y) {
		position.Set(x, y);
		RecalculateLocalTransformation();
	}

	public final Vector2 LocalPosition() {
		return localPosition;
	}

	public void LocalPosition(Vector2 v) {
		LocalPosition(v.x, v.y);
	}

	public void LocalPosition(float x, float y) {
		localPosition.Set(x, y);
		RecalculateGlobalTransformations();
	}

	public final Vector2 Scale() {
		return scale;
	}

	public void Scale(Vector2 v) {
		Scale(v.x, v.y);
	}

	public void Scale(float x, float y) {
		scale.Set(x, y);
		RecalculateLocalTransformation();
	}

	public final Vector2 LocalScale() {
		return localScale;
	}

	public void LocalScale(Vector2 v) {
		LocalScale(v.x, v.y);
	}

	public void LocalScale(float x, float y) {
		localScale.Set(x, y);
		RecalculateGlobalTransformations();
	}

	public final float Rotation() {
		return rotation;
	}

	public void Rotation(float v) {
		rotation = Mathf.Wrap(v, 0, 360);
		RecalculateLocalTransformation();
	}

	public final float LocalRotation() {
		return localRotation;
	}

	public void LocalRotation(float v) {
		localRotation = Mathf.Wrap(v, 0, 360);
		RecalculateGlobalTransformations();
	}

	private void RecalculateLocalTransformation() {
		dirty = 1;
		localScale = scale.Div(parent.scale);
		localRotation = Mathf.Wrap(rotation - parent.rotation, 0, 360);
		temp.SetTransformation(null, -parent.rotation, new Vector2(1, 1).Div(parent.scale));
		localPosition.Set(temp.TransformPoint(position.Sub(parent.position)));

		for (int i = 0; i < children.size(); i++) children.get(i).RecalculateGlobalTransformations();
	}

	private void RecalculateGlobalTransformations() {
		dirty = 1;
		scale = parent.scale.Mul(localScale);
		rotation = Mathf.Wrap(parent.rotation + localRotation, 0, 360);
		position.Set(parent.Matrix().TransformPoint(localPosition));

		for (int i = 0; i < children.size(); i++) {
			children.get(i).RecalculateGlobalTransformations();
		}
	}

	public GameObject Parent() {
		return parent;
	}

	public void Parent(GameObject g) {
		if (g == null) g = master;
		if (parent != null) {
			if (parent != g) parent.RemoveChild(this);
			else return;
		}

		parent = g;
		parent.children.add(this);
		SetInline(parent.inline + 1);
		RecalculateLocalTransformation();
	}

	public final List<LogicBehaviour> GetComponents() {
		return components;
	}

	public LogicBehaviour GetComponent(String s) {
		for (j = 0; j < components.size(); j++) {
			LogicBehaviour b = components.get(j);
			if (b.Name().equals(s)) return b;
		}
		return null;
	}

	public void RemoveComponent(LogicBehaviour b) {
		for (i = 0; i < components.size(); i++) {
			if (components.get(i) == b) {
				components.remove(i);
				return;
			}
		}
	}

	public void RemoveComponent(String s) {
		for (i = 0; i < components.size(); i++) {
			if (components.get(i).Name().equals(s)) {
				components.remove(i);
				return;
			}
		}
	}

	public LogicBehaviour AddComponent(LogicBehaviour b) {
		if (b == null) {
			Debug.Log("Could not find behaviour!");
			return null;
		}
		b.Init(this);
		components.add(b);
		return b;
	}

	public LogicBehaviour AddComponent(String v) {
		Class<?> cls;

		try {
			try {
				cls = Class.forName(v);
			} catch (NoClassDefFoundError e) {
				return null;
			}
			try {
				return AddComponent((LogicBehaviour) cls.getConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Debug.Log("Instance of " + v + " could not be instantiated!");
			}

		} catch (ClassNotFoundException e) {
			LogicBehaviour l = EditorUtil.GetBehaviour(v);
			if (l != null) return AddComponent(l);
			Debug.Log("Class " + v + " could not be found!");
		}
		return null;
	}

	void Id(String value) {
		id = value;
	}

	public final String ID() {
		return id;
	}

	public void Expand(boolean b) {
		expanded = b;
	}

	public boolean Expanded() {
		return expanded;
	}

	public int Inline() {
		return inline;
	}

	public void SetInline(int v) {
		inline = v;
		for (i = 0; i < children.size(); i++) children.get(i).SetInline(v + 1);
	}

	public List<GameObject> Children() {
		return children;
	}

	public void RemoveChild(GameObject g) {
		for (i = 0; i < children.size(); i++) {
			if (children.get(i) == g) {
				RemoveChild(i);
				return;
			}
		}
	}

	public void RemoveChild(int v) {
		if (v >= children.size()) return;
		GameObject g = children.get(v);
		//g.Parent(master);
		children.remove(v);
		g.inline = 0;
	}

	public void Destroy() {
		Parent(null);
		for (i = 0; i < children.size(); i++) children.get(i).Destroy();
		instances.remove(this);

		children.clear();
		master.children.remove(this);
	}

	public void Start() {
		for (int i = 0; i < components.size(); i++) {
			LogicBehaviour component = components.get(i);
			component.Start();
		}
	}

	public void Update() {
		for (int i = 0; i < components.size(); i++) {
			LogicBehaviour component = components.get(i);
			component.Update();
		}
	}

	public void PrepareToRender() {
		for (i = 0; i < components.size(); i++) {
			LogicBehaviour component = components.get(i);
			if (component.Name().equals("SpriteRenderer")) {
				SpriteRenderer sr = (SpriteRenderer) component;
				if (sr.sprite != null) Renderer.AddToRenderer(sr);
			}
		}
	}
}
