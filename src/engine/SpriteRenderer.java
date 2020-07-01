package engine;

import gui.Sprite;
import math.Vector2;

public class SpriteRenderer extends LogicBehaviour {
    public Sprite sprite;
    public Vector2 anchor = new Vector2(0, 0);

    //Set the uniforms of the gameobject and it's sprite
    public void SetUniforms() {
        sprite.material.shader.SetUniform("anchor", anchor);

        if (gameObject.isDirty()) {
            gameObject.UpdateMatrix();
        }
        sprite.material.shader.SetUniform("transformationMatrix", gameObject.Matrix());
        sprite.material.shader.SetUniform("objectScale", gameObject.Scale());

        //Set the position uniform
        sprite.material.shader.SetUniform("screenPos", gameObject.Position().x, gameObject.Position().y);

        //Set the scale uniform
        sprite.material.shader.SetUniform("pixelScale", sprite.offset.width, sprite.offset.height);

        //Create and set the uv offset uniform
        Rect uv = sprite.UV();
        sprite.material.shader.SetUniform("offset", uv.x, uv.y, uv.width, uv.height);
    }
}
