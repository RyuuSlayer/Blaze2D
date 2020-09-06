package engine;

import java.util.UUID;

public abstract class Object implements Cloneable {
	private String name = "";
	private String instanceID = UUID.randomUUID().toString();

	public final String Name() {
		return name;
	}

	public void Name(String name) {
		this.name = name;
	}

	public final String instanceID() {
		return instanceID;
	}

	public void instanceID(String id) {
		instanceID = id;
	}

	@Override
	public engine.Object clone() throws CloneNotSupportedException {
		return (engine.Object) super.clone();
	}
}
