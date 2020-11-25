package editor;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BehaviourAttributes {
    public Class<?> c;
    public List<BehaviourField> fields = new ArrayList<BehaviourField>();

    public int height;

    public BehaviourAttributes(engine.Object o) {
        c = o.getClass();

        SetClass(o, 0);
        height -= 2;
    }

    private void SetClass(engine.Object o, int offset) {
        Field[] fields = o.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (f.getName().equals("enabled") || f.getName().equals("expanded") || f.getType().isArray() || f.getName().equals("f") || f.getName().equals("lastModified"))
                continue;

            this.fields.add(new BehaviourField(o, f, offset));
            if (engine.CustomClass.class.isAssignableFrom(f.getType())) {
                try {
                    if (((engine.CustomClass) f.get(o)).expanded) SetClass((engine.Object) f.get(o), offset + 16);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (math.Rect.class.isAssignableFrom(f.getType())) height += 48;
            else height += 24;
        }
    }
}

class BehaviourField {
    engine.Object object;
    Field field;
    int offset;

    public BehaviourField(engine.Object o, Field f, int offset) {
        object = o;
        field = f;
        this.offset = offset;
    }
}
