package logic.world.entitiesMatrix;

import logic.execution.instance.entity.api.EntityInstance;
import logic.execution.instance.entity.impl.EntityInstanceImpl;

public class Cell {
    private int row;
    private int col;
    private boolean full;
    private EntityInstance entityInstance;

    public Cell(int row, int col, boolean full) {
        this.row = row;
        this.col = col;
        this.full = full;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public void setCol(int col) {
        this.col = col;
    }

    public boolean isFull(){
        return full;
    }

    public void setFull(boolean full){
        this.full = full;
    }

    public void setEntityInstance(EntityInstance entityInstance) {
        this.entityInstance = entityInstance;
    }

    public EntityInstance getEntityInstance() {
        return entityInstance;
    }
}
