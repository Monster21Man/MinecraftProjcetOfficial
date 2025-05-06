package main.java;

/***************************************************************
 * file: FPCameraController.java
 * author: Jeffrey Rodas, Jahdon Faulcon, Logan Bailey
 * class: CS 4450
 *
 * assignment: Checkpoint 2
 * date last modified: 4/15/2025
 *
 * purpose: This code implements a first-person camera controller in 
 a 3D environment using LWJGL, allowing the user to move 
 (WASD, Space/Shift) and look around (mouse) in real time. It 
 renders a colored cube with a wireframe outline to demonstrate the 
 camera's movement and perspective. The system serves as a basic 
 foundation for first-person 3D games or simulations.
 *
 ****************************************************************/ 
import org.lwjgl.util.vector.Vector3f;
 import org.lwjgl.input.Keyboard;
 import org.lwjgl.input.Mouse;
 import org.lwjgl.opengl.Display;
 import static org.lwjgl.opengl.GL11.*;
 import org.lwjgl.Sys;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
 
 //Overall class for the code
 public class FPCameraController {
     private Vector3f position = null;
     private Vector3f lPosition = null;
     private float yaw = 0.0f;
     private float pitch = 0.0f;
     private World world;
 
     //Constructor: Sets the position of the first person camera
     public FPCameraController(float x, float y, float z) {
         position = new Vector3f(x, y, z);
         lPosition = new Vector3f(x, y, z);
         lPosition.x = 0f;
         lPosition.y = 15f;
         lPosition.z = 0f;
     }
     //method: inizilaizeWorld
     //purpose: this method initializes our Minecraft world
     public void initializeWorld() {
         world = new World();
     }
     
     //method: yaw
     //purpose: This method controls how much the yaw will change
     public void yaw(float amount) {
         yaw += amount;
     }
 
     //method: pitch
     //purpose: This method controls how much the pitch will change
     public void pitch(float amount) {
         pitch -= amount;
     }
 
     //method: walkForward
     //purpose: This method controls the users ability to walk forwards in the 3D window
     public void walkForward(float distance) {
         float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
         float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
         position.x -= xOffset;
         position.z += zOffset;
     }
 
     //method: walkBackwards
     //purpose: This method controls the users ability to walk backwards in the 3D window
     public void walkBackwards(float distance) {
         float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
         float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
         position.x += xOffset;
         position.z -= zOffset;
     }
 
     //method: strafeLeft
     //purpose: This method controls the users ability to walk left or strafe left in the 3D window
     public void strafeLeft(float distance) {
         float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
         float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
         position.x -= xOffset;
         position.z += zOffset;
     }
 
     //method: strafeRight
     //purpose: This method controls the users ability to walk right or strafe right in the 3D window
     public void strafeRight(float distance) {
         float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
         float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
         position.x -= xOffset;
         position.z += zOffset;
     }
 
     //method: moveUp
     //purpose: This method controls the users ability to rise up in the 3D window
     public void moveUp(float distance) {
         position.y -= distance;
     }
 
     //method: moveDown
     //purpose: This method controls the users ability to sink down in the 3D window
     public void moveDown(float distance) {
         position.y += distance;
     }


   float zEndPos = 0.0f;
   float zStartPos = 450.0f; //3*CHUNK_SIZE*WORLD_SIZE
  
  
   public static FloatBuffer sunPosition = BufferUtils.createFloatBuffer(4);
   public static FloatBuffer sunLight = BufferUtils.createFloatBuffer(4);
   static float sunDelta = 0;
   
   public static FloatBuffer moonPosition = BufferUtils.createFloatBuffer(4);
   public static FloatBuffer moonLight = BufferUtils.createFloatBuffer(4);
   public static float moonDelta = 0;
   
   static boolean day = true;
   
   int lightSourceIncrementAmt = 10;
   int timeBeforeIncrement = 100;
   float incrementRate = (float) (lightSourceIncrementAmt/ (timeBeforeIncrement/1000f));

   public static final float[] R_VALUES = {
  
   0.536f//135/255.0f  // Final daylight (unchanged)*/
   0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.012f, 0.027f,
   0.043f, 0.059f, 0.075f, 0.090f, 0.106f, 0.122f, 0.137f, 0.153f, 0.169f, 0.184f,
   0.200f, 0.216f, 0.231f, 0.247f, 0.263f, 0.278f, 0.294f, 0.310f, 0.325f, 0.341f,
   0.357f, 0.373f, 0.388f, 0.404f, 0.420f, 0.435f, 0.451f, 0.467f, 0.482f, 0.498f,
   // **Seamless transition (perceptual easing-out curve)**
   0.505f,  // +0.007 (gentle start)
   0.513f,  // +0.008 (slightly faster)
   0.520f,  // +0.007 (peak momentum)
   0.526f,  // +0.006 (starting to slow)
   0.531f,  // +0.005 (easing)
   0.534f,  // +0.003 (almost there)
   0.536f
   
    
   };
     
   public static final float[] G_VALUES = {
   .090f, 0.110f, 0.130f, 0.150f, 0.170f, 0.190f, 0.210f, 0.230f, 0.250f, 0.270f, 0.290f,
   0.306f, 0.322f, 0.337f, 0.353f, 0.369f, 0.384f, 0.400f, 0.416f, 0.431f, 0.447f,
   0.463f, 0.478f, 0.494f, 0.510f, 0.525f, 0.541f, 0.557f, 0.573f, 0.588f, 0.604f,
   0.620f, 0.635f, 0.651f, 0.667f, 0.682f, 0.698f, 0.714f, 0.729f, 0.745f, 0.761f,
   // **Seamless transition (natural light curve)**
   0.768f,  // +0.007
   0.776f,  // +0.008
   0.783f,  // +0.007
   0.789f,  // +0.006
   0.794f,  // +0.005
   0.797f,  // +0.003
   0.800f   // Adjusted final for smoothness (barely noticeable change)
   
   };
     
   public static final float[] B_VALUES = {
   .308f, 0.333f, 0.358f, 0.383f, 0.383f, 0.408f, 0.433f, 0.458f, 0.483f, 0.508f, 0.533f,
   0.545f, 0.557f, 0.569f, 0.580f, 0.592f, 0.604f, 0.616f, 0.627f, 0.639f, 0.651f,
   0.663f, 0.675f, 0.686f, 0.698f, 0.710f, 0.722f, 0.733f, 0.745f, 0.757f, 0.769f,
   0.780f, 0.792f, 0.804f, 0.816f, 0.827f, 0.839f, 0.851f, 0.863f, 0.875f, 0.886f,
   // **Seamless transition (logarithmic easing)**
   0.893f,  // +0.007
   0.901f,  // +0.008
   0.908f,  // +0.007
   0.914f,  // +0.006
   0.918f,  // +0.004
   0.921f,  // +0.003
   0.922f   // Final (exact 235/255.0f, now smooth)
   };

   public static final float[] r_night = {
   0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f,
   0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f,
   0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f,
   0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f,
   // Rapid transition to daylight (last 7 values)
   0.200f, 0.300f, 0.400f, 0.480f, 0.536f, 0.536f, 0.536f
   };

   public static final float[] g_night = {
   0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f,
   0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f,
   0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f,
   0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f,
   // Rapid transition to daylight (last 7 values)
   0.300f, 0.500f, 0.650f, 0.750f, 0.800f, 0.800f, 0.800f
   };

   public static final float[] b_night = {
   0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f,
   0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f,
   0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f,
   0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f,
   // Rapid transition to daylight (last 7 values)
   0.500f, 0.700f, 0.850f, 0.900f, 0.922f, 0.922f, 0.922f
   };

   public static void changeBackground(int pos) {
      int index = 0;
      if (day == true) {
         index = (pos/10) - 1;
         glClearColor(R_VALUES[index], G_VALUES[index], B_VALUES[index], 1.0f);
          
       } else {
           index = (pos/10);
           glClearColor(r_night[index], g_night[index], b_night[index], 1.0f);
       }
  
    }

    public static void configureSunLight() {
        sunPosition.clear();
        sunPosition.put(CHUNK_SIZE*WORLD_SIZE).put(0.0f).put(3*CHUNK_SIZE*WORLD_SIZE+sunDelta).put(1.0f).flip(); 
        glLight(GL_LIGHT0, GL_POSITION, sunPosition);
    
        // Configure light colors
        sunLight.clear();
        sunLight.put(1.0f).put(1.0f).put(0.9f).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_DIFFUSE, sunLight);
    
        // Ambient light - not too bright
        FloatBuffer sunAmbient = BufferUtils.createFloatBuffer(4);
        sunAmbient.put(0.3f).put(0.3f).put(0.3f).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_AMBIENT, sunAmbient);
    
        // Configure attenuation (this is key to making light fade with distance)
        glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1.0f);
        glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, 0.0014f);    // Adjust these values to control lighting falloff
        glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, 0.000007f); // Quadratic gives more realistic falloff
     }

     // Configure the moon as a positional light with proper attenuation
     public static void configureMoonLight() {
         moonPosition.clear();
         moonPosition.put(CHUNK_SIZE*WORLD_SIZE).put(0.0f).put(3*CHUNK_SIZE*WORLD_SIZE+moonDelta).put(1.0f).flip();
         glLight(GL_LIGHT1, GL_POSITION, moonPosition);
         
         // Configure light colors
         moonLight.clear();
         moonLight.put(0.6f).put(0.7f).put(0.9f).put(1.0f).flip();
         glLight(GL_LIGHT1, GL_DIFFUSE, moonLight);
         
         // Dimmer ambient for night
         FloatBuffer moonAmbient = BufferUtils.createFloatBuffer(4);
         moonAmbient.put(0.1f).put(0.1f).put(0.2f).put(1.0f).flip();
         glLight(GL_LIGHT1, GL_AMBIENT, moonAmbient);
         
         // Configure attenuation (same as sun but can be adjusted for moon)
         glLightf(GL_LIGHT1, GL_CONSTANT_ATTENUATION, 1.0f);
         glLightf(GL_LIGHT1, GL_LINEAR_ATTENUATION, 0.0014f); 
         glLightf(GL_LIGHT1, GL_QUADRATIC_ATTENUATION, 0.000007f);
     }
     public static void switchLighting() {
        if(day == true){
           configureMoonLight();
           // Switching from day to night
           glEnable(GL_LIGHT1);  // Enable moon light
           glDisable(GL_LIGHT0); // Disable sun light
           moonDelta = 0; // Reset moon delta
           System.out.println("Switched to night mode - Moon light enabled");
         } else {
            configureSunLight();
            
            glEnable(GL_LIGHT0);  // Enable sun light
            glDisable(GL_LIGHT1); // Disable moon light
            sunDelta = 0; // Reset sun delta
            
            System.out.println("Switched to day mode - Sun light enabled");
         }
      
         day = !day; // Toggle day/night state
      }
      
     static float WORLD_SIZE = World.getWorldSize(); //stores world size
     static float CHUNK_SIZE = Chunk.CHUNK_SIZE; //stores chunck size

  
     //method: lookThrough
     //purpose: This method controls the cameras ability to move and rotate in the 3D window
     public void lookThrough() {
         glRotatef(pitch, 1.0f, 0.0f, 0.0f);
         glRotatef(yaw, 0.0f, 1.0f, 0.0f);
         glTranslatef(position.x, position.y, position.z);

         if (day) {
            configureSunLight();
         } else {
            configureMoonLight();
         }

         sunPosition.put(CHUNK_SIZE*WORLD_SIZE).put(0.0f).put(3*CHUNK_SIZE*WORLD_SIZE+sunDelta).put(1.0f).flip(); 
         glLight(GL_LIGHT0, GL_POSITION, sunPosition);
         
         moonPosition.put(CHUNK_SIZE*WORLD_SIZE).put(0.0f).put(3*CHUNK_SIZE*WORLD_SIZE+moonDelta).put(1.0f).flip();
         glLight(GL_LIGHT1, GL_POSITION, moonPosition);
         moonLight.put(0.6f).put(0.7f).put(0.9f).put(1.0f).flip();
         glLight(GL_LIGHT1, GL_SPECULAR, moonLight);
         glLight(GL_LIGHT1, GL_DIFFUSE, moonLight);
         glLight(GL_LIGHT1, GL_AMBIENT, moonLight);
     }
 
     //method: gameLoop
     /*
      *purpose: This method is the actual game loop for the program and allows the other functions to be used constantly.
      * It allows the program to detect input from the users keyboard and translate it into player movement 
      * through the scene.
      */
     public void gameLoop() {
         glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
         float dx, dy, dt, lastTime = 0.0f;
         long time;
         float mouseSensitivity = 0.09f;
         float movementSpeed = 0.15f;

         long start = Sys.getTime();
 
         Mouse.setGrabbed(true);
         //Loop that allows the window to remain open and the user to control their player with the keyboard and mouse
         while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)&& !Keyboard.isKeyDown(Keyboard.KEY_Q)) {
             time = Sys.getTime();
             lastTime = time;

             if (time - start >=timeBeforeIncrement && day == true && sunPosition.get(2) > zEndPos) {
                 start = time;
                 float sunPos = sunPosition.get(2);
                 changeBackground((int)sunPos);
                 sunDelta -= lightSourceIncrementAmt;
                 System.out.println("sunPos = " + sunPos);
                 if (sunPos <= 10 && day == true) {
                     switchLighting();
                     sunPosition.clear();
                     sunPosition.put(CHUNK_SIZE*WORLD_SIZE).put(0.0f).put(3*CHUNK_SIZE*WORLD_SIZE).put(1.0f).flip();
                     glLight(GL_LIGHT0, GL_POSITION, sunPosition);
                     //sunDelta += (zStartPos - sunPos);
                     System.out.println("sunPosition.get(2) = " + sunPosition.get(2));
                     
                 } 
             } else if (time - start >= timeBeforeIncrement && day != true && moonPosition.get(2) > zEndPos) {
                 start = time;
                 float moonPos = moonPosition.get(2);
                 System.out.println("zStartPos = " + zStartPos);
                 System.out.println("moonPos = " + moonPos);
                 changeBackground((int)(zStartPos - moonPos));
                 moonDelta-= lightSourceIncrementAmt;
                 if (moonPos <= 10 && day != true) {
                     switchLighting();
                     moonPosition.clear();
                     moonPosition.put(CHUNK_SIZE*WORLD_SIZE).put(0.0f).put(3*CHUNK_SIZE*WORLD_SIZE).put(1.0f).flip();
                     glLight(GL_LIGHT1, GL_POSITION, moonPosition);
                     System.out.println("moonPosition.get(2) = " + moonPosition.get(2));
                 }
             }
             
             dx = Mouse.getDX();
             dy = Mouse.getDY();
             if (dx != 0 || dy != 0) {
                 System.out.println("Mouse movement: " + dx + ", " + dy);
             }
             this.yaw(dx * mouseSensitivity);
             this.pitch(dy * mouseSensitivity);
 
             //This series of 'if' statements detects whether the user is pressing any of the movement keys
             if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                 this.walkForward(movementSpeed);
             }
             if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                 this.walkBackwards(movementSpeed);
             }
             if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                 this.strafeLeft(movementSpeed);
             }
             if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                 this.strafeRight(movementSpeed);
             }
             if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                 this.moveUp(movementSpeed);
             }
             if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                 this.moveDown(movementSpeed);
             }
 
             glLoadIdentity();
             this.lookThrough();
             glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
             glPushMatrix();
             world.render();
             glPopMatrix();
             Display.update();
             Display.sync(60);
         }
 
         Display.destroy();
     }
 
     // method: render
     /*
      * purpose: the method draws a cube centered at the origin (0,0). Each face is drawn by specifying the 6 
      * sets of 4 vertices within a glBegin(GL_QUADS) block. Each face's vertices are specified in 
      * counterclockwise order. Each edge (wire in the wireframe) is drawn by specifying the same 6 sets of 4 
      * vertices within a glBegin(GL_LINE_LOOP) block
     */
     private void render() {
         try {
             glBegin(GL_QUADS);
             //Top
             glColor3f(0.0f, 0.0f, 1.0f);
             glVertex3f(1.0f, 1.0f, -1.0f);
             glVertex3f(-1.0f, 1.0f, -1.0f);
             glVertex3f(-1.0f, 1.0f, 1.0f);
             glVertex3f(1.0f, 1.0f, 1.0f);
             //Bottom
             glColor3f(0.0f, 1.0f, 1.0f);
             glVertex3f(1.0f, -1.0f, 1.0f);
             glVertex3f(-1.0f, -1.0f, 1.0f);
             glVertex3f(-1.0f, -1.0f, -1.0f);
             glVertex3f(1.0f, -1.0f, -1.0f);
             //Front
             glColor3f(1.0f, 1.0f, 1.0f);
             glVertex3f(1.0f, 1.0f, 1.0f);
             glVertex3f(-1.0f, 1.0f, 1.0f);
             glVertex3f(-1.0f, -1.0f, 1.0f);
             glVertex3f(1.0f, -1.0f, 1.0f);
             //Back
             glColor3f(1.0f, 0.0f, 0.0f);
             glVertex3f(1.0f, -1.0f, -1.0f);
             glVertex3f(-1.0f, -1.0f, -1.0f);
             glVertex3f(-1.0f, 1.0f, -1.0f);
             glVertex3f(1.0f, 1.0f, -1.0f);
             //Left
             glColor3f(1.0f, 1.0f, 0.0f);
             glVertex3f(-1.0f, 1.0f, 1.0f);
             glVertex3f(-1.0f, 1.0f, -1.0f);
             glVertex3f(-1.0f, -1.0f, -1.0f);
             glVertex3f(-1.0f, -1.0f, 1.0f);
             //Right
             glColor3f(1.0f, 0.0f, 1.0f);
             glVertex3f(1.0f, 1.0f, -1.0f);
             glVertex3f(1.0f, 1.0f, 1.0f);
             glVertex3f(1.0f, -1.0f, 1.0f);
             glVertex3f(1.0f, -1.0f, -1.0f);
             glEnd();
 
             //drawing wireframe
             glBegin(GL_LINE_LOOP);
             //Top
             glColor3f(0.0f, 0.0f, 0.0f);
             glVertex3f(1.0f, 1.0f, -1.0f);
             glVertex3f(-1.0f, 1.0f, -1.0f);
             glVertex3f(-1.0f, 1.0f, 1.0f);
             glVertex3f(1.0f, 1.0f, 1.0f);
             glEnd();
             glBegin(GL_LINE_LOOP);
             //Bottom
             glVertex3f(1.0f, -1.0f, 1.0f);
             glVertex3f(-1.0f, -1.0f, 1.0f);
             glVertex3f(-1.0f, -1.0f, -1.0f);
             glVertex3f(1.0f, -1.0f, -1.0f);
             glEnd();
             glBegin(GL_LINE_LOOP);
             //Front
             glVertex3f(1.0f, 1.0f, 1.0f);
             glVertex3f(-1.0f, 1.0f, 1.0f);
             glVertex3f(-1.0f, -1.0f, 1.0f);
             glVertex3f(1.0f, -1.0f, 1.0f);
             glEnd();
             glBegin(GL_LINE_LOOP);
             //Back
             glVertex3f(1.0f, -1.0f, -1.0f);
             glVertex3f(-1.0f, -1.0f, -1.0f);
             glVertex3f(-1.0f, 1.0f, -1.0f);
             glVertex3f(1.0f, 1.0f, -1.0f);
             glEnd();
             glBegin(GL_LINE_LOOP);
             //Left
             glVertex3f(-1.0f, 1.0f, 1.0f);
             glVertex3f(-1.0f, 1.0f, -1.0f);
             glVertex3f(-1.0f, -1.0f, -1.0f);
             glVertex3f(-1.0f, -1.0f, 1.0f);
             glEnd();
             glBegin(GL_LINE_LOOP);
             //Right
             glVertex3f(1.0f, 1.0f, -1.0f);
             glVertex3f(1.0f, 1.0f, 1.0f);
             glVertex3f(1.0f, -1.0f, 1.0f);
             glVertex3f(1.0f, -1.0f, -1.0f);
             glEnd();
         } catch (Exception e) {
         }
     }
 }
