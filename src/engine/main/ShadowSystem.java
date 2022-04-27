package engine.main;

import engine.ecs.System;
import math.Matrix4;
import math.Vector3;

import static org.lwjgl.opengl.GL41.*;

public class ShadowSystem extends System {
    int fbo;
    int texture;

    int SHADOW_WIDTH = 1200;
    int SHADOW_HEIGHT = 1200;

    public ShadowSystem() {
        super(0);
    }

    public void init() {
        fbo = glGenFramebuffers();
        texture = glGenTextures();


        // Generate a texture to store the scene from the light's perspective
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Attach the depth texture to the frame buffer's depth buffer
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, texture, 0);
        /**
         * Tell OpenGL that we will not be writing any color data.
         */
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0); // unbind
    }

    public void renderIntoDepthMap() {
        glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glClear(GL_DEPTH_BUFFER_BIT);
//        ConfigureShaderAndMatrices();
//        RenderScene();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // 2. then render scene as normal with shadow mapping (using depth map)
//        glViewport(0, 0, SCR_WIDTH, SCR_HEIGHT);
//        ConfigureShaderAndMatrices();
//        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public Matrix4 generateLightSpaceTransform() {
        Matrix4 lightProjectionM = Camera.orthographicProjectionM((int) (10 * World.getInstance().window.getAspectRatio()), 10, 100);
        Matrix4 lightViewM = Camera.lookAt(new Vector3(-20, 100, 20), Vector3.zeros(), Transform.up);

        Matrix4 lightSpaceM = Matrix4.mult(lightProjectionM, lightViewM);

        return lightSpaceM;
    }

    public void update(float dt) {

    }
}
