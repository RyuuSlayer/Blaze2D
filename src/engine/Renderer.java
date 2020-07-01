package engine;

import gui.GUI;
import math.Matrix4x4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private static Map<Material, List<SpriteRenderer>> batch = new HashMap<Material, List<SpriteRenderer>>();
    private static Mesh mesh;
    private static Matrix4x4 projection;

    private static FBO fbo;

    //Initialize the renderer
    public static void Init() {
        //Create the vertices and uvs and generated mesh using them
        float[] verts = new float[]{0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1};
        float[] uvs = new float[]{0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0};
        mesh = new Mesh(verts, uvs);

        //Generate an fbo that we can render to
        UpdateFBO(Application.Width(), Application.Height());
    }

    //Add a sprite render to get rendered this frame
    public static void AddToRenderer(SpriteRenderer r) {
        //Get all the sprite renderers using the material of the passed in renderer
        List<SpriteRenderer> matRenderers = batch.get(r.sprite.material);

        //If that material does not exist yet or we have no renderers for that material
        if (matRenderers == null) {
            //Create a new list of renderers and set it to the passed in material
            matRenderers = new ArrayList<SpriteRenderer>();
            batch.put(r.sprite.material, matRenderers);
        }
        //And add the sprite renderer to the list
        matRenderers.add(r);
    }

    //Render the fbo in a specified rectangle
    public static void Render(Rect r) {
        //Bind the fbo an clear the color buffer to black
        fbo.BindFrameBuffer();
        glClearColor(0, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        //Cache the applications size and half size
        float w = Application.Width();
        float h = Application.Height();
        float halfW = w * 0.5f;
        float halfH = h * 0.5f;

        //And create a projection matrix that is zeroed in the center of the screen with a flipped y direction
        projection = Matrix4x4.Ortho(-halfW, halfW, halfH, -halfH, -1, 1);

        //Bind the mesh
        mesh.Bind();

        //For all the materials being used
        for (Material material : batch.keySet()) {
            //Bind the material
            material.Bind();

            //Set the projection and color uniforms
            material.shader.SetUniform("projection", projection);
            material.shader.SetUniform("matColor", material.color);

            //Get the list of renderers belonging to that material and loop through them
            List<SpriteRenderer> renderers = batch.get(material);
            for (SpriteRenderer renderer : renderers) {
                //And set all the renderers uniforms and render the mesh
                renderer.SetUniforms();
                mesh.Render();
            }
            //Then unbind the material
            material.Unbind();
        }

        //And unbind the mesh and clear the batch for the next set of batches
        mesh.Unbind();
        batch.clear();

        //Unbind the fbo
        fbo.UnBind();

        //Prepare the gui, render the fbo in the specified rectangle and unbind the gui
        GUI.Prepare();
        GUI.DrawTextureWithTexCoords(fbo.Image(), r, new Rect(r.x / w, r.y / h, r.width / w, r.height / h));
        GUI.Unbind();
    }

    public static void UpdateFBO(int w, int h) {
        fbo = new FBO(w, h);
    }
}
