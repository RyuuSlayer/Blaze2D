package game;

import engine.LogicBehaviour;
import input.Input;
import math.Vector2;

public class Test extends LogicBehaviour {
    public String someValue = "";
    public int anotherValue = 1;
    public float someFloatValue = 1000;
    public Vector2 someVector = new Vector2(8, 5);

    public void Update() {
        //if(Input.GetKey(KeyEvent.VK_D)) gameObject.Rotation(gameObject.Rotation() + (Time.DeltaTime() * 10));

        if (Input.GetKey('r')) System.out.println("Holding r");
        else if (Input.GetKeyDown('t')) System.out.println("Pushed t");
        else if (Input.GetKeyUp('y')) System.out.println("Released y");
    }
}