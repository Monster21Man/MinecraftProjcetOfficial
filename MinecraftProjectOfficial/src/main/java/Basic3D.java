package main.java;

/***************************************************************
* file: Basic3D.java
* author: Jeffrey Rodas, Jahdon Faulcon, Logan Bailey
* class: CS 4450 Computer Graphics
*
* assignment: Checkpoint 2
* date last modified: 4/15/2025
*
* purpose: This program initializes a 3D window with a first person camera. 
* It initializes all processes and calls for other classes to run.
*
****************************************************************/

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

//Contains all methods and constructors for Basic3D
public class Basic3D {
    
    private FPCameraController fp = new FPCameraController(0, -55, -10);
    private DisplayMode displayMode;

    private FloatBuffer lightPosition; //declare buffer light position
    private FloatBuffer whiteLight; //declare buffer for lighting
    
    //method: initLightArrays
    //purpose: initializes lighting related buffers
    private void initLightArrays() {
        
        float WORLD_SIZE = World.getWorldSize(); //stores world size
        float CHUNK_SIZE = Chunk.CHUNK_SIZE; //stores chunk size
        
        lightPosition = BufferUtils.createFloatBuffer(4); //sets buffer's capacity to 4
        lightPosition.put((CHUNK_SIZE*WORLD_SIZE)).put(0.0f).put(CHUNK_SIZE*WORLD_SIZE).put(1.0f).flip();
            //assigns coordinates to buffer:
            //1/2 world's length in x direction
            //1/2 world's length in z direction
            //0 in y direction        
        
        whiteLight = BufferUtils.createFloatBuffer(4); //sets buffer's capacity to 4
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip(); //assigns white light values to buffer
    }

    //method: start
    //purpose: This method controls the initialization of the OpenGL window and begins the game loop
    public void start() {
        try {
            createWindow();
            initGL();
            fp.initializeWorld();
            fp.gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    //method: createWindow
    //purpose: This method creates the actual OpenGL window and sets it boundaries
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        //Uses a for loop to control the width and height of the window
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Hey Professor! We totally didn't rip off Minecraft!!!");
        Display.create();
    }

    //method: initGL
    //purpose: This method sets the background color of the window and sets it as a 3D space for the user to navigate
    private void initGL() {
        glClearColor(135 / 255.0f, 206 / 255.0f, 235 / 255.0f, 1.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        /*
            glEnable(GL_LIGHTING);
            glEnable(GL_LIGHT0);
            float[] lightPosition = {0, 100, 0, 1}; // Position the light above
            glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);
            float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f}; // White light
            glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
        */
        initLightArrays(); //initializes lighting and position buffers
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); // sets light position according to the position buffer
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight); // sets specular according to lighting buffer
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight); // sets diffuse light according to buffer
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight); //sets ambient lgiht according to lighting buffer
        glEnable(GL_LIGHTING); ///enables lighting
        glEnable(GL_LIGHT0); //enables light0
    }

    //method: main
    //purpose: This method calls for the program to begin
    public static void main(String[] args) {
        Basic3D basic = new Basic3D();
        basic.start();
    }
}
