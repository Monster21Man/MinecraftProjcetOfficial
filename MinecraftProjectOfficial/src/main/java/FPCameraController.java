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
import org.lwjgl.Sys;
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
    private Vector3f lPosition = null;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private World world;
    
    // Collision detection variables
    private boolean collisionDetectionEnabled = true; // Default to enabled
    private boolean debugMode = true; // Set to true to see debug messages
    private float playerWidth = 0.5f; // Player's "width" for collision detection
    private float playerHeight = 1.8f;
    private boolean vKeyPressed = false;
    
    // Third-person view variables
    private Texture handTexture = null; // Field for the hand texture, initialize to null
    private Texture steveBoxTexture = null;
    private boolean isThirdPerson = false; // Camera mode state
    private boolean f5KeyPressed = false; // For key debounce
    private float thirdPersonDistance = 4.0f; // How far back the TP camera is
    
    // Player Model Dimensions
    // Player Model Dimensions
    private float bodyWidth = 0.5f;
    private float bodyHeight = 0.75f; // Add this line
    private float bodyDepth = 0.3f;
    private float headSize = 0.5f; // Used for width, height, depth of head
    // Lighting variables
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
    static float WORLD_SIZE = World.getWorldSize(); //stores world size
    static float CHUNK_SIZE = Chunk.CHUNK_SIZE; //stores chunck size
    
    // Lighting color arrays
    public static final float[] R_VALUES = {
        
        0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f,
        0.012f, 0.027f, 0.043f, 0.059f, 0.075f, 0.090f, 0.106f, 0.122f, 0.137f, 0.153f, 
        0.169f, 0.184f, 0.200f, 0.216f, 0.231f, 0.247f, 0.263f, 0.278f, 0.294f, 0.310f, 
        0.325f, 0.341f, 0.357f, 0.373f, 0.388f, 0.404f, 0.420f, 0.435f, 0.451f, 0.467f, 
        0.482f, 0.498f, 0.505f, 0.513f, 0.520f, 0.526f, 0.531f, 0.534f, 0.536f
    };
     
    public static final float[] G_VALUES = {
        0.090f, 0.110f, 0.130f, 0.150f, 0.170f, 0.190f, 0.210f, 0.230f, 0.250f, 0.270f, 
        0.290f, 0.306f, 0.322f, 0.337f, 0.353f, 0.369f, 0.384f, 0.400f, 0.416f, 0.431f, 
        0.447f, 0.463f, 0.478f, 0.494f, 0.510f, 0.525f, 0.541f, 0.557f, 0.573f, 0.588f, 
        0.604f, 0.620f, 0.635f, 0.651f, 0.667f, 0.682f, 0.698f, 0.714f, 0.729f, 0.745f, 
        0.761f, 0.768f, 0.776f, 0.783f, 0.789f, 0.794f, 0.797f, 0.800f
    };
     
    public static final float[] B_VALUES = {
        0.308f, 0.333f, 0.358f, 0.383f, 0.383f, 0.408f, 0.433f, 0.458f, 0.483f, 0.508f, 
        0.533f, 0.545f, 0.557f, 0.569f, 0.580f, 0.592f, 0.604f, 0.616f, 0.627f, 0.639f, 
        0.651f, 0.663f, 0.675f, 0.686f, 0.698f, 0.710f, 0.722f, 0.733f, 0.745f, 0.757f, 
        0.769f, 0.780f, 0.792f, 0.804f, 0.816f, 0.827f, 0.839f, 0.851f, 0.863f, 0.875f, 
        0.886f, 0.893f, 0.901f, 0.908f, 0.914f, 0.918f, 0.921f, 0.922f
    };

    public static final float[] r_night = {
        0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f,
        0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f,
        0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f,
        0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f, 0.000f,
        0.200f, 0.300f, 0.400f, 0.480f, 0.536f, 0.536f, 0.536f
    };

    public static final float[] g_night = {
        0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f,
        0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f,
        0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f,
        0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f,
        0.300f, 0.500f, 0.650f, 0.750f, 0.800f, 0.800f, 0.800f
    };

    public static final float[] b_night = {
        0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f,
        0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f,
        0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f,
        0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f, 0.308f,
        0.500f, 0.700f, 0.850f, 0.900f, 0.922f, 0.922f, 0.922f
    };
    
    //Constructor: Sets the position of the first person camera
    public FPCameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x, y, z);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
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
    
    //method: wouldCollide
    //purpose: This method checks if the user will collide with a block
    private boolean wouldCollide(float newX, float newY, float newZ) {
        // If collision detection is disabled, always return false (no collision)
        if (!collisionDetectionEnabled) {
            return false;
        }

        // Simple debugging output
        if (debugMode) {
            System.out.println("Player position: " + newX + ", " + newY + ", " + newZ);
        }

        // Convert world coordinates to chunk coordinates
        int chunkX = (int) Math.floor(newX / (Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH));
        int chunkZ = (int) Math.floor(newZ / (Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH));

        // Check if we're outside the world
        if (chunkX < 0 || chunkX >= World.getWorldSize() || chunkZ < 0 || chunkZ >= World.getWorldSize()) {
            if (debugMode) {
                System.out.println("Outside world bounds");
            }
            return true; // Collision with world boundary
        }

        // Get the chunk
        Chunk chunk = world.getChunk(chunkX, chunkZ);
        if (chunk == null) {
            if (debugMode) {
                System.out.println("Chunk is null");
            }
            return false; // No chunk, no collision
        }

        // Calculate block coordinates within the chunk
        // Note: Adjusting for your coordinate system - may need modification
        float relativeX = newX - (chunkX * Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH);
        float relativeZ = newZ - (chunkZ * Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH);

        int blockX = (int) (relativeX / Chunk.CUBE_LENGTH);
        // Adjust Y coordinate based on your system
        int blockY = (int) ((newY + (Chunk.CHUNK_SIZE * 0.8f)) / Chunk.CUBE_LENGTH);         
        int blockZ = (int) (relativeZ / Chunk.CUBE_LENGTH);

        // Ensure block coordinates are valid
        if (blockX < 0 || blockX >= Chunk.CHUNK_SIZE
                || blockY < 0 || blockY >= Chunk.CHUNK_SIZE
                || blockZ < 0 || blockZ >= Chunk.CHUNK_SIZE) {
            if (debugMode) {
                System.out.println("Block coordinates out of range: " + blockX + ", " + blockY + ", " + blockZ);
            }
            return false;
        }

        // Get the block
        Block block = chunk.getBlock(blockX, blockY, blockZ);
        if (block == null) {
            if (debugMode) {
                System.out.println("Block is null");
            }
            return false;
        }

        // Check if this is a solid block
        boolean isSolid = block.IsActive()
                && block.GetID() != Block.BlockType.BlockType_Default.GetID()
                && block.GetID() != Block.BlockType.BlockType_Water.GetID();

        if (debugMode) {
            System.out.println("Block at " + blockX + ", " + blockY + ", " + blockZ
                    + " ID: " + block.GetID() + " Active: " + block.IsActive()
                    + " Solid: " + isSolid);
        }

        return isSolid;
    }
    
    //method: initializeWorld
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
        
        float newX = position.x - xOffset;
        float newZ = position.z + zOffset;
        
        if (!wouldCollide(newX, position.y, newZ)) {
            // No collision, can move
            position.x = newX;
            position.z = newZ;
        }
    }
 
    //method: walkBackwards
    //purpose: This method controls the users ability to walk backwards in the 3D window
    public void walkBackwards(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        float newX = position.x + xOffset;
        float newZ = position.z - zOffset;

        // Check for collision
        if (!wouldCollide(newX, position.y, newZ)) {
            // No collision, can move
            position.x = newX;
            position.z = newZ;
        }
    }
 
    //method: strafeLeft
    //purpose: This method controls the users ability to walk left or strafe left in the 3D window
    public void strafeLeft(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        float newX = position.x - xOffset;
        float newZ = position.z + zOffset;

        // Check for collision
        if (!wouldCollide(newX, position.y, newZ)) {
            // No collision, can move
            position.x = newX;
            position.z = newZ;
        }
    }
 
    //method: strafeRight
    //purpose: This method controls the users ability to walk right or strafe right in the 3D window
    public void strafeRight(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        float newX = position.x - xOffset;
        float newZ = position.z + zOffset;

        // Check for collision
        if (!wouldCollide(newX, position.y, newZ)) {
            // No collision, can move
            position.x = newX;
            position.z = newZ;
        }
    }
 
    //method: moveUp
    //purpose: This method controls the users ability to rise up in the 3D window
    public void moveUp(float distance) {
        // Calculate new position
        float newY = position.y - distance;

        // Check for collision
        if (!wouldCollide(position.x, newY, position.z)) {
            // No collision, can move
            position.y = newY;
        }
    }
 
    //method: moveDown
    //purpose: This method controls the users ability to sink down in the 3D window
    public void moveDown(float distance) {
        float newY = position.y + distance;

        // Check for collision
        if (!wouldCollide(position.x, newY, position.z)) {
            // No collision, can move
            position.y = newY;
        }
    }
    
    // Lighting methods
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
      
    // Hand and Third-Person View methods
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
        glVertex3f(-h, h, h); // Top Left UV -> Top Front
        // Left Face continued
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

            // Calculate camera eye position based on distance, yaw, pitch FROM the lookAt point
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
            
            // Lighting setup
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
    }
    
    //method: gameLoop
    //purpose: This method is the actual game loop for the program and allows the other functions to be used constantly.
    //It handles input, updates camera, and renders the scene and overlay.
    public void gameLoop() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        float dx, dy, dt, lastTime = 0.0f;
        long time;
        float mouseSensitivity = 0.09f;
        float movementSpeed = 0.15f;

        // Load textures for third-person view
        loadTextures();
        
        long start = Sys.getTime();
 
        Mouse.setGrabbed(true);
        //Loop that allows the window to remain open and the user to control their player with the keyboard and mouse
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            time = Sys.getTime();
            lastTime = time;
            
            // Collision detection toggle with V key
            if (Keyboard.isKeyDown(Keyboard.KEY_V) && !vKeyPressed) {
                collisionDetectionEnabled = !collisionDetectionEnabled;
                System.out.println("Collision detection: " + (collisionDetectionEnabled ? "enabled" : "disabled"));
                vKeyPressed = true;
            } else if (!Keyboard.isKeyDown(Keyboard.KEY_V)) {
                vKeyPressed = false;
            }

            // Camera Mode Toggle with F5 key
            boolean f5Down = Keyboard.isKeyDown(Keyboard.KEY_F5);
            if (f5Down && !f5KeyPressed) { // Check if pressed this frame, but not last frame
                isThirdPerson = !isThirdPerson; // Toggle mode
                System.out.println("Camera Mode Toggled: " + (isThirdPerson ? "Third Person" : "First Person"));
            }
            f5KeyPressed = f5Down; // Update status for next frame

            // Day/Night cycle code
            if (time - start >= timeBeforeIncrement && day == true && sunPosition.get(2) > zEndPos) {
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
                    System.out.println("sunPosition.get(2) = " + sunPosition.get(2));
                } 
            } else if (time - start >= timeBeforeIncrement && day != true && moonPosition.get(2) > zEndPos) {
                start = time;
                float moonPos = moonPosition.get(2);
                System.out.println("zStartPos = " + zStartPos);
                System.out.println("moonPos = " + moonPos);
                changeBackground((int)(zStartPos - moonPos));
                moonDelta -= lightSourceIncrementAmt;
                if (moonPos <= 10 && day != true) {
                    switchLighting();
                    moonPosition.clear();
                    moonPosition.put(CHUNK_SIZE*WORLD_SIZE).put(0.0f).put(3*CHUNK_SIZE*WORLD_SIZE).put(1.0f).flip();
                    glLight(GL_LIGHT1, GL_POSITION, moonPosition);
                    System.out.println("moonPosition.get(2) = " + moonPosition.get(2));
                }
            }
            
            // Mouse input handling
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            if (dx != 0 || dy != 0) {
                System.out.println("Mouse movement: " + dx + ", " + dy);
            }
            this.yaw(dx * mouseSensitivity);
            this.pitch(dy * mouseSensitivity);
 
            // Keyboard movement handling
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
 
            // Rendering
            glLoadIdentity();
            this.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            // Render world
            glPushMatrix();
            world.render();
            glPopMatrix();
            
            // Render player in third person view
            if (isThirdPerson) {
                renderPlayerBox();
            }
            
            // Render hand in first person view
            if (!isThirdPerson) {
                renderHand();
            }
            
            Display.update();
            Display.sync(60);
        }
 
        // Cleanup
        if (handTexture != null) {
            handTexture.release();
        }
        if (steveBoxTexture != null) {
            steveBoxTexture.release();
        }
        Display.destroy();
    }
    
    // method: render
    //purpose: the method draws a cube centered at the origin (0,0). Each face is drawn by specifying the 6 
    //sets of 4 vertices within a glBegin(GL_QUADS) block. Each face's vertices are specified in 
    //counterclockwise order. Each edge (wire in the wireframe) is drawn by specifying the same 6 sets of 4 
    //vertices within a glBegin(GL_LINE_LOOP) block
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
