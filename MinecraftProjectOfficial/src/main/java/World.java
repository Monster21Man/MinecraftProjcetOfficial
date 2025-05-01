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
//This is the overall World class
public class World {

    private static final int WORLD_SIZE = 5; // 5x5 chunks = 150x150 if chunk size is 30
    private Chunk[][] chunks; // 2d arraystores all chunks in world

    //method: getWorldSize
    //purpose: retrieve private value WORLD_SIZE
    public static float getWorldSize() {
        return WORLD_SIZE;
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
