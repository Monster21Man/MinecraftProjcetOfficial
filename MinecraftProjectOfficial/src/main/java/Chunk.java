package main.java;

/***************************************************************
 * file: Chunk.java
 * author: Jeffrey Rodas, Jahdon Faulcon, Logan Bailey
 * class: CS 4450
 *
 * assignment: Checkpoint 2
 * date last modified: 4/15/2025
 *
 * purpose: This code creates the framework for generating chunks in our 
 *          Minecraft world. It uses matrices and VBOs to handle generation. 
 ****************************************************************/ 
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
//method: Chunk
//purpose: This class groups the blocks together based one the CHUNK_SIZE
public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    
    private Random r;
    
    private int VBOTextureHandle;
    private Texture texture;
    private FloatBuffer VertexPositionData;
    private FloatBuffer VertexColorData;
    private FloatBuffer VertexTextureData;
    private int vertexCount = 0;
    //method: isBlockTransparent
    /*purpose: This method returns a boolean value depending on if the block 
    *          specified is transparent (in the air)
    */
    private boolean isBlockTransparent(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= CHUNK_SIZE || y >= CHUNK_SIZE || z >= CHUNK_SIZE) {
            return true;
        }

        int blockID = Blocks[x][y][z].GetID();
        return blockID == Block.BlockType.BlockType_Default.GetID()
                || blockID == Block.BlockType.BlockType_Water.GetID();
    }
    //method: createVisibleFaces
    /*purpose: This method will create a vertex of the coordinates of visible 
    *          faces of a single block
    */
    public float[] createVisibleFaces(float x, float y, float z, int bx, int by, int bz) {
        int offset = CUBE_LENGTH / 2;
        FloatBuffer buffer = BufferUtils.createFloatBuffer(24 * 3); // Max 6 faces * 4 vertices * 3 coords

        if (isBlockTransparent(bx, by + 1, bz)) {
            buffer.put(new float[]{
                x + offset, y + offset, z,
                x - offset, y + offset, z,
                x - offset, y + offset, z - CUBE_LENGTH,
                x + offset, y + offset, z - CUBE_LENGTH
            });
        }

        if (isBlockTransparent(bx, by - 1, bz)) {
            buffer.put(new float[]{
                x + offset, y - offset, z - CUBE_LENGTH,
                x - offset, y - offset, z - CUBE_LENGTH,
                x - offset, y - offset, z,
                x + offset, y - offset, z
            });
        }

        if (isBlockTransparent(bx, by, bz - 1)) {
            buffer.put(new float[]{
                x + offset, y + offset, z - CUBE_LENGTH,
                x - offset, y + offset, z - CUBE_LENGTH,
                x - offset, y - offset, z - CUBE_LENGTH,
                x + offset, y - offset, z - CUBE_LENGTH
            });
        }

        if (isBlockTransparent(bx, by, bz + 1)) {
            buffer.put(new float[]{
                x + offset, y - offset, z,
                x - offset, y - offset, z,
                x - offset, y + offset, z,
                x + offset, y + offset, z
            });
        }

        if (isBlockTransparent(bx - 1, by, bz)) {
            buffer.put(new float[]{
                x - offset, y + offset, z - CUBE_LENGTH,
                x - offset, y + offset, z,
                x - offset, y - offset, z,
                x - offset, y - offset, z - CUBE_LENGTH
            });
        }

        if (isBlockTransparent(bx + 1, by, bz)) {
            buffer.put(new float[]{
                x + offset, y + offset, z,
                x + offset, y + offset, z - CUBE_LENGTH,
                x + offset, y - offset, z - CUBE_LENGTH,
                x + offset, y - offset, z
            });
        }

        buffer.flip();
        float[] result = new float[buffer.remaining()];
        buffer.get(result);
        return result;
    }
    //method: render
    //purpose: this method will draw the chunks
    public void render() {
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
        glDrawArrays(GL_QUADS, 0, vertexCount);
        glPopMatrix();
    }
    //mehtod: rebuildMesh 
    /*purpose: This method prepares the chunks 3D mesh based ont eh current 
    *        block layout. It calculates which block faces are visbile
    *        builds vertex/texture/color of those faces and sends it to the VBO
    */
    public void rebuildMesh(float startX, float startY, float startZ) {

        VertexPositionData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        VertexColorData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        VertexTextureData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        int renderedFaces = 0;

        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    // Skip air blocks
                    if (!Blocks[x][y][z].IsActive()
                            || Blocks[x][y][z].GetID() == Block.BlockType.BlockType_Default.GetID()) {
                        continue;
                    }

                    float[] blockVertices = createVisibleFaces(
                            (float) (startX + x * CUBE_LENGTH),
                            (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                            (float) (startZ + z * CUBE_LENGTH),
                            x, y, z);

                    if (blockVertices.length == 0) {
                        continue;
                    }

                    int faces = blockVertices.length / (4 * 3);
                    renderedFaces += faces;
                    VertexPositionData.put(blockVertices);

                    float[] cubeColors = createCubeVertexCol(getCubeColor(Blocks[x][y][z]));
                    for (int i = 0; i < faces; i++) {
                        for (int j = 0; j < 4; j++) {
                            VertexColorData.put(cubeColors, 0, 3);
                        }
                    }

                    float[] texCoords = createTexCube((float) 0, (float) 0, Blocks[x][y][z]);
                    for (int i = 0; i < faces; i++) {
                        VertexTextureData.put(texCoords, i * 8, 8);
                    }
                }
            }
        }

        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();

        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vertexCount = VertexPositionData.capacity() / 3;

    }
    //method: createCubeVertexCol
    //purpose: This method will give the cubes color. 
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        int componentsPerColor = CubeColorArray.length;
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];

        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    //method: createCube
    /*purpose: this method creates the cube by calculating and returning the 
    position of all 24 vertices
    */
    public static float[] createCube(float x, float y,
            float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[]{
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z};
    }
    //method: getCubeColor
    /*purpose: this method will returnt he color of the cube. Has a special case
    *         with water
    */
    private float[] getCubeColor(Block block) {
        switch (block.GetID()) {
            case 2: // Water
                return new float[]{0.0f, 0.0f, 0.8f};
            default:
                return new float[]{1.0f, 1.0f, 1.0f};
        }
    }
    //mehtod: Chunk
    /*purpose: this method will generate and initialize all blocks in a 3D grid
    *          It also assigns types (grass,stone,water) and loads textures
    *          and prepares the mesh
    */
    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG",
                    ResourceLoader.getResourceAsStream("terrain.png"));
        } catch (Exception e) {
            System.out.println("Failed to load texture: " + e.getMessage());
            e.printStackTrace();
        }
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        int waterLevel = (int) (CHUNK_SIZE * 0.3);
        
        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, 0.35, r.nextInt());
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                // Generate terrain height using SimplexNoise
                int height = (int) ((noise.getNoise(x, z) + 1) * CHUNK_SIZE * 0.375);
                height = Math.max(1, Math.min(height, CHUNK_SIZE - 1));

                for (int y = 0; y < CHUNK_SIZE; y++) {
                    if (y == 0) {
                        // Bottom layer is bedrock
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                        Blocks[x][y][z].SetActive(true);
                    } // If below water level but above terrain, it's water
                    else if (y <= waterLevel && y > height) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                        Blocks[x][y][z].SetActive(true);
                    } else if (y <= height - 4 && y > 0) {
                        // Deep underground = stone
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                        Blocks[x][y][z].SetActive(true);
                    } else if (y <= height - 1 && y > 0) {
                        // Just below surface = dirt
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                        Blocks[x][y][z].SetActive(true);
                    } else if (y == height) {
                        // Surface layer = grass
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                        Blocks[x][y][z].SetActive(true);
                    } else {
                        // Air above surface
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Default);
                        Blocks[x][y][z].SetActive(false);
                    }
                }
            }
        }
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    // Skip air blocks and bedrock
                    if (Blocks[x][y][z].GetID() == Block.BlockType.BlockType_Default.GetID()
                            || Blocks[x][y][z].GetID() == Block.BlockType.BlockType_Bedrock.GetID()) {
                        continue;
                    }

                    // Small chance to convert surface dirt to sand
                    if (Blocks[x][y][z].GetID() == Block.BlockType.BlockType_Grass.GetID() && r.nextFloat() < 0.1f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                        Blocks[x][y][z].SetActive(true);
                    }
                }
            }
        }

        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
    //method: createTextCube
    /* purpose: This method calculates and returns the UV texture coordinates
    *         for each face of the cube
    */
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f / 16) / 1024f;
        switch (block.GetID()) {
            
            //Grass
            case 0:
                return new float[]{
                    // TOP QUAD
                    x + offset * 3, y + offset * 10, 
                    x + offset * 2, y + offset * 10, 
                    x + offset * 2, y + offset * 9,
                    x + offset * 3, y + offset * 9,
                    // BOTTOM QUAD 
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 4, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 4, y + offset * 1,
                    // BACK QUAD
                    x + offset * 4, y + offset * 0,
                    x + offset * 3, y + offset * 0, 
                    x + offset * 3, y + offset * 1, 
                    x + offset * 4, y + offset * 1, 
                    // LEFT QUAD 
                    x + offset * 4, y + offset * 0,
                    x + offset * 3, y + offset * 0, 
                    x + offset * 3, y + offset * 1, 
                    x + offset * 4, y + offset * 1, 
                    // RIGHT QUAD 
                    x + offset * 4, y + offset * 0, 
                    x + offset * 3, y + offset * 0, 
                    x + offset * 3, y + offset * 1, 
                    x + offset * 4, y + offset * 1};
                
                //Sand
                case 1:
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // TOP!
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // FRONT QUAD
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // BACK QUAD
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // LEFT QUAD
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1};
                
                //Water
                case 2:
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1,
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0,
                    // TOP!
                    x + offset * 15, y + offset * 13,
                    x + offset * 14, y + offset * 13,
                    x + offset * 14, y + offset * 12,
                    x + offset * 15, y + offset * 12,
                    // FRONT QUAD
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1,
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0,
                    // BACK QUAD
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1,
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1,
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0,
                    // RIGHT QUAD
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1,
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0};
                
                //Dirt
                case 3:
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // TOP!
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // BACK QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // RIGHT QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0};
                
                //Stone
                case 4:
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // TOP!
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // BACK QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0};
                
                //Bedrock
                case 5:
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // TOP!
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // FRONT QUAD
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // BACK QUAD
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // LEFT QUAD
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1};
                
                //Default (Air)
                case 6:
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 5, y + offset * 12,
                    x + offset * 4, y + offset * 12,
                    x + offset * 4, y + offset * 11,
                    x + offset * 5, y + offset * 11,
                    // TOP!
                    x + offset * 5, y + offset * 12,
                    x + offset * 4, y + offset * 12,
                    x + offset * 4, y + offset * 11,
                    x + offset * 5, y + offset * 11,
                    // FRONT QUAD
                    x + offset * 5, y + offset * 12,
                    x + offset * 4, y + offset * 12,
                    x + offset * 4, y + offset * 11,
                    x + offset * 5, y + offset * 11,
                    // BACK QUAD
                    x + offset * 5, y + offset * 12,
                    x + offset * 4, y + offset * 12,
                    x + offset * 4, y + offset * 11,
                    x + offset * 5, y + offset * 11,
                    // LEFT QUAD
                    x + offset * 5, y + offset * 12,
                    x + offset * 4, y + offset * 12,
                    x + offset * 4, y + offset * 11,
                    x + offset * 5, y + offset * 11,
                    // RIGHT QUAD
                    x + offset * 5, y + offset * 12,
                    x + offset * 4, y + offset * 12,
                    x + offset * 4, y + offset * 11,
                    x + offset * 5, y + offset * 11};
                
        }
        
        return new float[] {1, 1, 1};
    }
}
