/***************************************************************
 * file: FPCameraController.java
 * author: Jeffrey Rodas, Jahdon Faulcon, Logan Bailey
 * class: CS 4450
 *
 * assignment: Checkpoint 1
 * date last modified: 3/27/20204
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
 
 //Overall class for the code
 public class FPCameraController {
     private Vector3f position = null;
     private Vector3f lPosition = null;
     private float yaw = 0.0f;
     private float pitch = 0.0f;
 
     //Constructor: Sets the position of the first person camera
     public FPCameraController(float x, float y, float z) {
         position = new Vector3f(x, y, z);
         lPosition = new Vector3f(x, y, z);
         lPosition.x = 0f;
         lPosition.y = 15f;
         lPosition.z = 0f;
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
 
     //method: lookThrough
     //purpose: This method controls the cameras ability to move and rotate in the 3D window
     public void lookThrough() {
         glRotatef(pitch, 1.0f, 0.0f, 0.0f);
         glRotatef(yaw, 0.0f, 1.0f, 0.0f);
         glTranslatef(position.x, position.y, position.z);
     }
 
     //method: gameLoop
     /*
      *purpose: This method is the actual game loop for the program and allows the other functions to be used constantly.
      * It allows the program to detect input from the users keyboard and translate it into player movement 
      * through the scene.
      */
     public void gameLoop() {
         FPCameraController camera = new FPCameraController(0, 0, 0);
         float dx, dy, dt, lastTime = 0.0f;
         long time;
         float mouseSensitivity = 0.09f;
         float movementSpeed = 0.35f;
 
         Mouse.setGrabbed(true);
         //Loop that allows the window to remain open and the user to control their player with the keyboard and mouse
         while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)&& !Keyboard.isKeyDown(Keyboard.KEY_Q)) {
             time = Sys.getTime();
             lastTime = time;
 
             dx = Mouse.getDX();
             dy = Mouse.getDY();
             camera.yaw(dx * mouseSensitivity);
             camera.pitch(dy * mouseSensitivity);
 
             //This series of 'if' statements detects whether the user is pressing any of the movement keys
             if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                 camera.walkForward(movementSpeed);
             }
             if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                 camera.walkBackwards(movementSpeed);
             }
             if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                 camera.strafeLeft(movementSpeed);
             }
             if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                 camera.strafeRight(movementSpeed);
             }
             if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                 camera.moveUp(movementSpeed);
             }
             if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                 camera.moveDown(movementSpeed);
             }
 
             glLoadIdentity();
             camera.lookThrough();
             glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
             render();
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