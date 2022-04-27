package engine.view;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import engine.controller.Controller;

public class Window {
    // The window handle
    public long window;

    private final int WIDTH = 1750;
    private final int HEIGHT = 1100;

    public Window() {
        init();
    }

    public void cleanUp() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public float getAspectRatio() {
        return (float) WIDTH / (float) HEIGHT;
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);


        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Zariel", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        /**
         * Input settings
         */
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        /**
         * key press, scroll, cursor position, and mouse button callbacks
         */
        glfwSetKeyCallback(window, Controller::onKeyPressedCallback);
        glfwSetCursorPosCallback(window, Controller::onMousePositionCallback);
        glfwSetScrollCallback(window, Controller::onScrollCallback);
        glfwSetMouseButtonCallback(window, Controller::onMouseButtonCallback);

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // enable depth testing
        glEnable(GL_DEPTH_TEST);
        // update the depth buffer
        glDepthMask(true);
        // replace the existing value in the buffer is the new value is less than the old value
        glDepthFunc(GL_LESS);

        // Set the clear color (WHITE)
        glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 0.0f);
    }

    public void prepareWindow() {
        // clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void setTitle(String windowTitle) {
        glfwSetWindowTitle(window, windowTitle);
    }

    public void render() {
        // swap the color buffers
        glfwSwapBuffers(window);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwDestroyWindow(window);
        }
    }
}