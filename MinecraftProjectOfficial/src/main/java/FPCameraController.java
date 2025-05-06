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
 * a 3D environment using LWJGL, allowing the user to move
 * (WASD, Space/Shift) and look around (mouse) in real time. It
 * renders a colored cube with a wireframe outline to demonstrate the
 * camera's movement and perspective. The system serves as a basic
 * foundation for first-person 3D games or simulations.
 * This code also implements a third person view of world and character
 * Includes rendering a 2D hand overlay.
 ****************************************************************/
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import java.io.InputStream;
import org.lwjgl.util.glu.GLU; 

//Overall class for the code
public class FPCameraController {
    private Vector3f position = null;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private World world;
    private Texture handTexture = null; // Field for the hand texture, initialize to null
    private Texture steveBoxTexture = null;
    
    private boolean isThirdPerson = false; // Camera mode state
    private boolean f5KeyPressed = false; // For key debounce
    private float thirdPersonDistance = 4.0f; // How far back the TP camera is
    
    // --- Player Model Dimensions ---
    private float bodyWidth = 0.5f;
    private float bodyHeight = 0.75f;
    private float bodyDepth = 0.3f;
    private float headSize = 0.5f; // Used for width, height, depth of head
    
    // Constructor: Sets the position of the first person camera
    public FPCameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
    }

    // New method to load textures AFTER OpenGL context is created
    public void loadTextures() {
        try {
            InputStream stream = ResourceLoader.getResourceAsStream("steve-hand.png");
            handTexture = TextureLoader.getTexture("PNG", stream);
            stream.close();
            stream = ResourceLoader.getResourceAsStream("steve-png.png");
            steveBoxTexture = TextureLoader.getTexture("PNG", stream);
            stream.close();
          
        } catch (Exception e) { // Catch broader exceptions during loading
            e.printStackTrace();
            steveBoxTexture = null;
        }
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
         // prevent camera from flipping over
        pitch -= amount;
        if (pitch > 90.0f) {
           pitch = 90.0f;
        } else if (pitch < -90.0f) {
           pitch = -90.0f;
        }
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
        if (isThirdPerson) {
            // --- Third Person Camera ---
            float playerRootX = -position.x;
            float playerRootY = -position.y; // Player's base Y coordinate
            float playerRootZ = -position.z;

            // Calculate desired look-at height 
            float lookAtYOffset = bodyHeight * 0.8f; // Aim near the top of the body

            float lookAtX = playerRootX;
            float lookAtY = playerRootY + lookAtYOffset; 
            float lookAtZ = playerRootZ;

            // Calculate camera eye position based on distance, yaw, pitch FROM THE lookAt point
            float horizontalDistance = thirdPersonDistance * (float) Math.cos(Math.toRadians(pitch));
            float verticalDistance = thirdPersonDistance * (float) Math.sin(Math.toRadians(pitch));

            float dx = horizontalDistance * (float) Math.sin(Math.toRadians(yaw));
            float dz = horizontalDistance * (float) Math.cos(Math.toRadians(yaw));

            // Camera position relative to the look-at point
            float camX = lookAtX + dx;
            float camY = lookAtY - verticalDistance; // Apply vertical offset relative to lookAtY
            float camZ = lookAtZ + dz;

            // Set the view using gluLookAt
            GLU.gluLookAt(camX, camY, camZ, // Eye position
                    lookAtX, lookAtY, lookAtZ, // Look at position
                    0.0f, 1.0f, 0.0f);      // Up vector (Y is up)

        } else {
            // --- First Person Camera ---
            glRotatef(pitch, 1.0f, 0.0f, 0.0f);
            glRotatef(yaw, 0.0f, 1.0f, 0.0f);
            glTranslatef(position.x, position.y, position.z);
        }

        // Common lighting setup
        FloatBuffer lightPositionBuffer = BufferUtils.createFloatBuffer(4);
        lightPositionBuffer.put(-position.x).put(-position.y + 5f).put(-position.z).put(1.0f).flip(); // Light above player
        glLight(GL_LIGHT0, GL_POSITION, lightPositionBuffer);
    }

    //method: renderHand
    //purpose: Renders the 2D hand texture overlay to make is more like 
    //         the first person view in Minecraft
    private void renderHand() {
        try {
            glMatrixMode(GL_PROJECTION);
            glPushMatrix();
            glLoadIdentity();
            glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);

            glMatrixMode(GL_MODELVIEW);
            glPushMatrix();
            glLoadIdentity();

            glDisable(GL_DEPTH_TEST);
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_TEXTURE_2D);

            glBindTexture(GL_TEXTURE_2D, handTexture.getTextureID());
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White

            float handWidth = Display.getWidth() * 0.25f;
            float handHeight = handWidth * ((float) handTexture.getImageHeight() / handTexture.getImageWidth());
            float marginX = 0.00f;
            float marginY = 0.00f;
            float handX = Display.getWidth() - handWidth - marginX;
            float handY = Display.getHeight() - handHeight - marginY;

            glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2f(handX, handY);
            glTexCoord2f(1, 0);
            glVertex2f(handX + handWidth, handY);
            glTexCoord2f(1, 1);
            glVertex2f(handX + handWidth, handY + handHeight);
            glTexCoord2f(0, 1);
            glVertex2f(handX, handY + handHeight);
            glEnd();

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_LIGHTING);
            glDisable(GL_BLEND);

            glMatrixMode(GL_MODELVIEW);
            glPopMatrix();
            glMatrixMode(GL_PROJECTION);
            glPopMatrix();
            glMatrixMode(GL_MODELVIEW);

        } catch (Exception e) {
            System.err.println("Error during renderHand:");
            e.printStackTrace();
        }
    }
    
    // method: drawPlayerPartCube  
    // purpose: Helper method to draw a standard textured cube centered at origin
    //          This would be used to create a box on the camera to create a player
    private void drawPlayerPartCube(float frontU0, float frontV0, float frontU1, float frontV1,
            float backU0, float backV0, float backU1, float backV1,
            float topU0, float topV0, float topU1, float topV1,
            float bottomU0, float bottomV0, float bottomU1, float bottomV1,
            float leftU0, float leftV0, float leftU1, float leftV1,
            float rightU0, float rightV0, float rightU1, float rightV1) {

        float h = 0.5f; // Cube half-size

        glBegin(GL_QUADS);

        // Front Face
        glTexCoord2f(frontU0, frontV1);
        glVertex3f(-h, -h, h); // Bottom Left Vert, Bottom Left UV
        glTexCoord2f(frontU1, frontV1);
        glVertex3f(h, -h, h); // Bottom Right Vert, Bottom Right UV
        glTexCoord2f(frontU1, frontV0);
        glVertex3f(h, h, h); // Top Right Vert, Top Right UV
        glTexCoord2f(frontU0, frontV0);
        glVertex3f(-h, h, h); // Top Left Vert, Top Left UV

        // Back Face (Note: UVs might need flipping depending on perspective)
        glTexCoord2f(backU1, backV1);
        glVertex3f(-h, -h, -h); // Bottom Right UV -> Bottom Left Vert
        glTexCoord2f(backU1, backV0);
        glVertex3f(-h, h, -h); // Top Right UV -> Top Left Vert
        glTexCoord2f(backU0, backV0);
        glVertex3f(h, h, -h); // Top Left UV -> Top Right Vert
        glTexCoord2f(backU0, backV1);
        glVertex3f(h, -h, -h); // Bottom Left UV -> Bottom Right Vert

        // Top Face
        glTexCoord2f(topU0, topV1);
        glVertex3f(-h, h, -h); // Bottom Left UV -> Top Back Left Vert
        glTexCoord2f(topU0, topV0);
        glVertex3f(-h, h, h); // Top Left UV -> Top Front Left Vert
        glTexCoord2f(topU1, topV0);
        glVertex3f(h, h, h); // Top Right UV -> Top Front Right Vert
        glTexCoord2f(topU1, topV1);
        glVertex3f(h, h, -h); // Bottom Right UV -> Top Back Right Vert

        // Bottom Face
        glTexCoord2f(bottomU1, bottomV1);
        glVertex3f(-h, -h, -h); // Bottom Right UV -> Bottom Back Left Vert
        glTexCoord2f(bottomU0, bottomV1);
        glVertex3f(h, -h, -h); // Bottom Left UV -> Bottom Back Right Vert
        glTexCoord2f(bottomU0, bottomV0);
        glVertex3f(h, -h, h); // Top Left UV -> Bottom Front Right Vert
        glTexCoord2f(bottomU1, bottomV0);
        glVertex3f(-h, -h, h); // Top Right UV -> Bottom Front Left Vert

        // Right face
        glTexCoord2f(rightU0, rightV1);
        glVertex3f(h, -h, -h); // Bottom Left UV -> Bottom Back Vert
        glTexCoord2f(rightU0, rightV0);
        glVertex3f(h, h, -h); // Top Left UV -> Top Back Vert
        glTexCoord2f(rightU1, rightV0);
        glVertex3f(h, h, h); // Top Right UV -> Top Front Vert
        glTexCoord2f(rightU1, rightV1);
        glVertex3f(h, -h, h); // Bottom Right UV -> Bottom Front Vert

        // Left Face
        glTexCoord2f(leftU1, leftV1);
        glVertex3f(-h, -h, -h); // Bottom Right UV -> Bottom Back Vert
        glTexCoord2f(leftU0, leftV1);
        glVertex3f(-h, -h, h); // Bottom Left UV -> Bottom Front Vert
        glTexCoord2f(leftU0, leftV0);
        glVertex3f(-h, h, h); // Top Left UV -> Top Front Vert
        glTexCoord2f(leftU1, leftV0);
        glVertex3f(-h, h, -h); // Top Right UV -> Top Back Vert

        glEnd();
    }
    
    // Renders the player representation box in the world
    private void renderPlayerBox() {
        if (steveBoxTexture == null) {
            return;
        }

        // Player's actual world coordinates (using the feet as the reference point)
        float playerX = -position.x;
        float playerY = -position.y; // This is now the Y for the bottom of the feet
        float playerZ = -position.z;

        // Texture dimensions (assuming standard 64x64 skin)
        final float TW = 64.0f; // Texture Width
        final float TH = 64.0f; // Texture Height (often 64, maybe 32 for older skins)

        // --- Calculate Head UVs (0.0 to 1.0) ---
        float headRight_U0 = 0 / TW, headRight_V0 = 8 / TH, headRight_U1 = 8 / TW, headRight_V1 = 16 / TH;
        float headFront_U0 = 8 / TW, headFront_V0 = 8 / TH, headFront_U1 = 16 / TW, headFront_V1 = 16 / TH;
        float headLeft_U0 = 16 / TW, headLeft_V0 = 8 / TH, headLeft_U1 = 24 / TW, headLeft_V1 = 16 / TH;
        float headBack_U0 = 24 / TW, headBack_V0 = 8 / TH, headBack_U1 = 32 / TW, headBack_V1 = 16 / TH;
        float headTop_U0 = 8 / TW, headTop_V0 = 0 / TH, headTop_U1 = 16 / TW, headTop_V1 = 8 / TH;
        float headBottom_U0 = 16 / TW, headBottom_V0 = 0 / TH, headBottom_U1 = 24 / TW, headBottom_V1 = 8 / TH;

        // --- Calculate Body UVs (0.0 to 1.0) ---
        float bodyRight_U0 = 16 / TW, bodyRight_V0 = 20 / TH, bodyRight_U1 = 20 / TW, bodyRight_V1 = 32 / TH;
        float bodyFront_U0 = 20 / TW, bodyFront_V0 = 20 / TH, bodyFront_U1 = 28 / TW, bodyFront_V1 = 32 / TH;
        float bodyLeft_U0 = 28 / TW, bodyLeft_V0 = 20 / TH, bodyLeft_U1 = 32 / TW, bodyLeft_V1 = 32 / TH;
        float bodyBack_U0 = 32 / TW, bodyBack_V0 = 20 / TH, bodyBack_U1 = 40 / TW, bodyBack_V1 = 32 / TH;
        float bodyTop_U0 = 20 / TW, bodyTop_V0 = 16 / TH, bodyTop_U1 = 28 / TW, bodyTop_V1 = 20 / TH;
        float bodyBottom_U0 = 28 / TW, bodyBottom_V0 = 16 / TH, bodyBottom_U1 = 36 / TW, bodyBottom_V1 = 20 / TH;

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING); 
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f); 

        // Bind the single skin texture once
        glBindTexture(GL_TEXTURE_2D, steveBoxTexture.getTextureID());

        // --- Render Body ---
        glPushMatrix();
        // Position body center slightly above the player's root Y position
        float bodyCenterYOffset = bodyHeight / 2.0f;
        glTranslatef(playerX, playerY + bodyCenterYOffset, playerZ);
        glRotatef(-yaw, 0.0f, 1.0f, 0.0f); // Rotate entire model based on yaw
        glScalef(bodyWidth, bodyHeight, bodyDepth);

        drawPlayerPartCube(bodyFront_U0, bodyFront_V0, bodyFront_U1, bodyFront_V1,
                bodyBack_U0, bodyBack_V0, bodyBack_U1, bodyBack_V1,
                bodyTop_U0, bodyTop_V0, bodyTop_U1, bodyTop_V1,
                bodyBottom_U0, bodyBottom_V0, bodyBottom_U1, bodyBottom_V1,
                bodyLeft_U0, bodyLeft_V0, bodyLeft_U1, bodyLeft_V1,
                bodyRight_U0, bodyRight_V0, bodyRight_U1, bodyRight_V1);
        glPopMatrix();

        // --- Render Head ---
        glPushMatrix();
        // Position head center above the body center
        float headCenterYOffset = bodyHeight + (headSize / 2.0f); // Place bottom of head at top of body
        glTranslatef(playerX, playerY + headCenterYOffset, playerZ);
        glRotatef(-yaw, 0.0f, 1.0f, 0.0f); // Rotate entire model based on yaw
        glScalef(headSize, headSize, headSize); // Head is roughly cubic

        drawPlayerPartCube(headFront_U0, headFront_V0, headFront_U1, headFront_V1,
                headBack_U0, headBack_V0, headBack_U1, headBack_V1,
                headTop_U0, headTop_V0, headTop_U1, headTop_V1,
                headBottom_U0, headBottom_V0, headBottom_U1, headBottom_V1,
                headLeft_U0, headLeft_V0, headLeft_U1, headLeft_V1,
                headRight_U0, headRight_V0, headRight_U1, headRight_V1);
        glPopMatrix();

        glBindTexture(GL_TEXTURE_2D, 0); // Unbind texture
        glEnable(GL_LIGHTING);
        glDisable(GL_BLEND);
    }

    //method: gameLoop
    /*
     * purpose: This method is the actual game loop for the program.
     * It handles input, updates camera, and renders the scene and overlay.
     */
    public void gameLoop() {
        float dx, dy;
        float mouseSensitivity = 0.09f;
        float movementSpeed = 0.35f;

        Mouse.setGrabbed(true);

        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !Keyboard.isKeyDown(Keyboard.KEY_Q)) {

            // --- Input ---
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            if (dx != 0) {
                yaw(dx * mouseSensitivity);
            }
            if (dy != 0) {
                pitch(dy * mouseSensitivity);
            }
            // Movement keys...
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                walkForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                walkBackwards(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                strafeLeft(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                strafeRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                moveDown(movementSpeed);
            }

            // Camera Mode Switch (using F5 key)
            boolean f5Down = Keyboard.isKeyDown(Keyboard.KEY_F5);
            if (f5Down && !f5KeyPressed) { // Check if pressed this frame, but not last frame
                isThirdPerson = !isThirdPerson; // Toggle mode
                System.out.println("Camera Mode Toggled: " + (isThirdPerson ? "Third Person" : "First Person"));
            }
            f5KeyPressed = f5Down; // Update status for next frame

            // --- Rendering ---
            glLoadIdentity(); // Start fresh each frame
            lookThrough();    // Apply FP or TP view transformations

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Render Player Box ONLY in Third Person
            if (isThirdPerson) {
                renderPlayerBox(); // Draw player model using the current view matrix
            }

            // Render World (relative to the camera view set by lookThrough)
            glPushMatrix();
            world.render();
            glPopMatrix();

            // Render 2D Hand Overlay only in first person
            if(!isThirdPerson){
                renderHand();
            }
            
            // --- Display Update ---
            Display.update();
            Display.sync(60);
        }

        // --- Cleanup ---
        if (handTexture != null) {
            handTexture.release();
        }
        if (steveBoxTexture != null) {
            steveBoxTexture.release();
        }
        Display.destroy();
    }
} // End of FPCameraController class
