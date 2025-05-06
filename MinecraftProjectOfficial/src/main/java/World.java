package main.java;
/***************************************************************
 * file: World.java
 * author: Jeffrey Rodas, Jahdon Faulcon, Logan Bailey
 * class: CS 4450
 *
 * assignment: Checkpoint 2
 * date last modified: 4/15/2025
 *
 * purpose: This code manages our Minecraft "world" and allows us to generate 
 * multiple chunks easily without overburdening the computer. 
 ****************************************************************/
import java.util.Random;
//This is the overall World class
public class World {

    private static final int WORLD_SIZE = 5; // 5x5 chunks = 150x150 if chunk size is 30
    private Chunk[][] chunks; // 2d arraystores all chunks in world
    private Random random = new Random();

    //method: getWorldSize
    //purpose: retrieve private value WORLD_SIZE
    public static float getWorldSize() {
        return WORLD_SIZE;
    }
    
    //method: getChunk
    //purpose: Used to get the chunk position
    public Chunk getChunk(int x, int z) {
        if (x >= 0 && x < WORLD_SIZE && z >= 0 && z < WORLD_SIZE) {
            return chunks[x][z];
        }
        return null;
    }
    
    //method: World
    //purpose: This method handles the generation of chunks in a world and where to place each chunk in the world
    public World() {
        chunks = new Chunk[WORLD_SIZE][WORLD_SIZE];
        for (int x = 0; x < WORLD_SIZE; x++) { // for loop iterates through creation of each chunk's position in world
            for (int z = 0; z < WORLD_SIZE; z++) {
                // Create chunks with proper positioning
                chunks[x][z] = new Chunk(
                        x * Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH, // x coordinate in world space
                        0, // y coordinate in world space (0 indicates base height)
                        z * Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH // z coordinate in world space
                );
            }
        }
        
        generateNetherPortals();
    }
    
    // method: generateNetherPortals
    //pupose: This method handles the gneration of the nether portals on the map
    private void generateNetherPortals() {
        // Determine how many portals to generate (1-5)
        int numPortals = random.nextInt(5) + 1;
        
        for (int portal = 0; portal < numPortals; portal++) {
            // Find a suitable location for the portal
            boolean validLocation = false;
            int chunkX = 0, chunkZ = 0;
            int blockX = 0, blockZ = 0;
            int blockY = 0;
            
            // Try up to 10 times to find a valid location
            for (int attempt = 0; attempt < 10 && !validLocation; attempt++) {
                // Select a random chunk
                chunkX = random.nextInt(WORLD_SIZE);
                chunkZ = random.nextInt(WORLD_SIZE);
                
                // Select a position within the chunk, away from the edges
                // Note: to ensure portal doesn't cross chunk boundaries
                blockX = random.nextInt(Chunk.CHUNK_SIZE - 6) + 3;
                blockZ = random.nextInt(Chunk.CHUNK_SIZE - 6) + 3;
                
                // Find the ground level at this position
                blockY = findGroundLevel(chunkX, chunkZ, blockX, blockZ);
                
                // Check if there's enough space for the portal
                if (blockY > 5 && blockY < Chunk.CHUNK_SIZE - 7) {
                    validLocation = true;
                }
            }
            
            if (validLocation) {
                // Build the nether portal
                buildNetherPortal(chunkX, chunkZ, blockX, blockY, blockZ);
            }
        }
    }
    
    private int findGroundLevel(int chunkX, int chunkZ, int blockX, int blockZ) {
        // Start from the top and find the first solid block
        for (int y = Chunk.CHUNK_SIZE - 1; y >= 0; y--) {
            Block block = chunks[chunkX][chunkZ].getBlock(blockX, y, blockZ);
            if (block != null && block.IsActive()
                    && block.GetID() != Block.BlockType.BlockType_Default.GetID()
                    && block.GetID() != Block.BlockType.BlockType_Water.GetID()) {
                return y + 1; // Return the position above the ground
            }
        }
        return 0; // Default to ground level if no solid block found
    }
    
    //method: buildNetherPortal
    //purpose: Sets the build pattern of a nether portal
    private void buildNetherPortal(int chunkX, int chunkZ, int x, int y, int z) {
        // Portal dimensions
        final int portalWidth = 4;
        final int portalHeight = 5;
        final int innerWidth = 2;
        final int innerHeight = 3;
        
        // Calculate offsets for the inner empty space
        int xOffset = (portalWidth - innerWidth) / 2;
        int yOffset = 1; // Bottom row is always obsidian
        
        // Build the obsidian frame
        for (int dx = 0; dx < portalWidth; dx++) {
            for (int dy = 0; dy < portalHeight; dy++) {
                // Skip the inner empty space
                if (dx >= xOffset && dx < xOffset + innerWidth && 
                    dy >= yOffset && dy < yOffset + innerHeight) {
                    continue;
                }
                
                // Place obsidian block
                setBlockInChunk(chunkX, chunkZ, x + dx, y + dy, z, 
                        Block.BlockType.BlockType_Obsidian);
            }
        }
    }
    
    //method: setBlockInChunk
    //purpose: This method tells the code what chunk to spawn the portals in
    private void setBlockInChunk(int chunkX, int chunkZ, int blockX, int blockY, int blockZ, 
                                Block.BlockType blockType) {
        if (chunkX >= 0 && chunkX < WORLD_SIZE && 
            chunkZ >= 0 && chunkZ < WORLD_SIZE && 
            blockX >= 0 && blockX < Chunk.CHUNK_SIZE && 
            blockY >= 0 && blockY < Chunk.CHUNK_SIZE && 
            blockZ >= 0 && blockZ < Chunk.CHUNK_SIZE) {
            
            Block block = new Block(blockType);
            block.SetActive(true);
            chunks[chunkX][chunkZ].setBlock(blockX, blockY, blockZ, block);
        }
    }

    //method: render
    //purpose: This method actually renders the chunks that we have chosen to generate in our world. Instead of calling for a single chunk to render,
    //         we call for the world to render and this handles chunk generation and rendering
    public void render() {
        // Render all chunks
        for (int x = 0; x < WORLD_SIZE; x++) { // for loop iterates through and renders each chunk
            for (int z = 0; z < WORLD_SIZE; z++) {
                chunks[x][z].render();
            }
        }
    }

    // Additional methods for updating chunks, etc.
}
