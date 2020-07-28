package engine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class FBO {
    private final int frameBufferID;
    private final Texture texture;
    private final int width;
    private final int height;

    //Constructor to initialize width and height of the fbo
    protected FBO(int width, int height) {
        //Cache width and height
        this.width = width;
        this.height = height;

        //Create the frame buffer
        frameBufferID = CreateFrameBuffer();

        //Create the texture attachment and create a texture from the generated id
        int textureID = CreateTextureAttachment();
        texture = new Texture(textureID, width, height);

        //Unbind the fbo
        UnBind();
    }

    //Return the texture for this fbo
    public final Texture Image() {
        return texture;
    }

    //Bind the frame buffer
    public void BindFrameBuffer() {
        //Bind texture slot 0 and bind the framebuffer
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBufferID);
    }

    //Unbind the frame buffer
    public void UnBind() {
        //Tell opengl not to use an fbo
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    //Create the frame buffer
    private int CreateFrameBuffer() {
        //Create the fbo id, bind it and draw the attachment buffer
        int buffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        //Then return the fbo id
        return buffer;
    }

    //Create the texture attachment for this fbo
    private int CreateTextureAttachment() {
        //Generate and bind the texture id
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        //Create a gl texture with default parameters and assign the texture id to the frame buffer
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texture, 0);

        //Then return the texture id
        return texture;
    }

    //Clean up the fbo
    public void CleanUp() {
        //Delete the frame buffer from memory
        GL30.glDeleteFramebuffers(frameBufferID);
    }
}