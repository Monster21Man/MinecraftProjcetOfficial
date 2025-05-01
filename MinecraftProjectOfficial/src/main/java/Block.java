package main.java;

/***************************************************************
 * file: Block.java
 * author: Jeffrey Rodas, Jahdon Faulcon, Logan Bailey
 * class: CS 4450
 *
 * assignment: Checkpoint 2
 * date last modified: 4/15/2025
 *
 * purpose: This code creates the class for a block. It will allow the easy 
 * generation of blocks without having to write it out every we want to 
 * initialize one.
 ****************************************************************/ 

public class Block {
    private boolean IsActive;
    private BlockType Type;
    private float x,y,z;
    //enum: Block
    /*purpose: This enum creates the block and holds the BlockID, as well as 
    *         get/set for the ID
    */
    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Default(6);
        
        private int BlockID;
        
        BlockType(int i) {
            BlockID=i;
        }
        
        public int GetID() {
            return BlockID;
        }
        
        public void SetID(int i) {
            BlockID = i;
        }
    }
    //method: Block
    //purpose: This method keeps track of the Block type 
    public Block(BlockType type) {
        Type = type;
    }
    //method: setCoords
    //purpose: This method sets the coordinates for the block
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    //method: IsActive
    //purpose: This is a boolean method that will return the state of the block
    //         True for active and False for inactive
    public boolean IsActive() {
        return IsActive;
    }
    //method: SetActive
    //purpose: This method is used to set the status of the block to active
    public void SetActive(boolean active) {
        IsActive = active;
    }
    //method: GetID
    //purpose: This method is used to get the ID of the block
    public int GetID() {
        return Type.GetID();
    }
}
