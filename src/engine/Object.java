package engine;


public abstract class Object implements Cloneable {
	private String name = "";

	public final String Name() {
		return name;
	}

	public void Name(String name) {
		this.name = name;
	}

	@Override
	public engine.Object clone() throws CloneNotSupportedException {
		return (engine.Object) super.clone();
	}
}
