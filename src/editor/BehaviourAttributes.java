package editor;


import engine.LogicBehaviour;

import java.lang.reflect.Field;

public class BehaviourAttributes {
    public Object behaviour;
    public Class<?> c;
    public String[] fields;

    public int height;

    public BehaviourAttributes(Object o) {
        this.behaviour = o;
        c = o.getClass();

        Field[] f = c.getFields();
        if (o instanceof LogicBehaviour) fields = new String[f.length - 1];
        else fields = new String[f.length];
        for (int i = 0; i < fields.length; i++) {
            String[] splitName = f[i].getType().toString().split("\\.");
            fields[i] = f[i].getName() + " " + splitName[splitName.length - 1];

            if (i < fields.length - 1) height += 24;
            else height += 22;
        }
    }
}
