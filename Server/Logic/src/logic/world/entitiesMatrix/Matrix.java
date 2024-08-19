package logic.world.entitiesMatrix;

import logic.definition.entity.api.EntityDefinition;
import logic.execution.instance.entity.api.EntityInstance;
import logic.execution.instance.entity.impl.EntityInstanceImpl;
import java.util.ArrayList;
import java.util.Random;

public class Matrix {
    private Cell[][] matrix;
    public enum Direction {
        UP, DOWN, RIGHT, LEFT
    }

    public Matrix(int rows, int columns)
    {
        matrix = new Cell[rows][columns];
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++)
            {
                matrix[i][j]= new Cell(i,j,false);
            }
        }
    }
    public Cell[][] getMatrix() {
        return matrix;
    }
    public void removeEntityFromMatrix(EntityInstance entityInstance){
        Cell entCell = findEntityInstanceCell(entityInstance);
        if(entCell != null){
            matrix[entCell.getRow()][entCell.getCol()].setFull(false);
            matrix[entCell.getRow()][entCell.getCol()].setEntityInstance(null);
        }
    }
    public void moveAllCellsRandomly() {
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        Random random = new Random();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (matrix[i][j].isFull()) {
                    Direction randomDirection = getRandomDirection();
                    moveCellContents(matrix[i][j], randomDirection);
                }
            }
        }
    }

    public Direction getRandomDirection() {
        Random random = new Random();
        int directionIndex = random.nextInt(4);

        switch (directionIndex) {
            case 0:
                return Direction.UP;
            case 1:
                return Direction.DOWN;
            case 2:
                return Direction.RIGHT;
            case 3:
                return Direction.LEFT;
            default:
                // This should not happen, but you can handle it as needed
                throw new IllegalStateException("Invalid direction index");
        }
    }

    public void moveCellContents(Cell sourceCell, Direction initialDirection) {
        if (areAdjacentCellsFull(sourceCell)) {
            return;
        }
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        int sourceRow = sourceCell.getRow();
        int sourceCol = sourceCell.getCol();
        int targetRow = sourceRow;
        int targetCol = sourceCol;

        switch (initialDirection) {
            case UP:
                targetRow = (sourceRow - 1 + numRows) % numRows;
                break;
            case DOWN:
                targetRow = (sourceRow + 1) % numRows;
                break;
            case RIGHT:
                targetCol = (sourceCol + 1) % numCols;
                break;
            case LEFT:
                targetCol = (sourceCol - 1 + numCols) % numCols;
                break;
        }

        if (!matrix[targetRow][targetCol].isFull()) {
            matrix[targetRow][targetCol].setEntityInstance(sourceCell.getEntityInstance());
            matrix[targetRow][targetCol].setFull(true);
            sourceCell.setEntityInstance(null);
            sourceCell.setFull(false);

            return; // Successfully moved
        }
        Direction randomDirection = getRandomDirection();
        moveCellContents(sourceCell, randomDirection);
    }


    public boolean areAdjacentCellsFull(Cell cell) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        int cellRow = cell.getRow();
        int cellCol = cell.getCol();
        boolean upFull = matrix[(cellRow - 1 + numRows) % numRows][cellCol].isFull();
        boolean downFull = matrix[(cellRow + 1) % numRows][cellCol].isFull();
        boolean leftFull = matrix[cellRow][(cellCol - 1 + numCols) % numCols].isFull();
        boolean rightFull = matrix[cellRow][(cellCol + 1) % numCols].isFull();

        return upFull && downFull && leftFull && rightFull;
    }

    public boolean isEntitySurrounding(EntityInstance sourceEntity, EntityDefinition targetEntity, int rank) {
        Cell sourceCell = findEntityInstanceCell(sourceEntity);
        int sourceRow = sourceCell.getRow();
        int sourceCol = sourceCell.getCol();
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        for (int i = sourceRow - rank; i <= sourceRow + rank; i++) {
            for (int j = sourceCol - rank; j <= sourceCol + rank; j++) {
                int wrappedRow = (i + numRows) % numRows;
                int wrappedCol = (j + numCols) % numCols;

                if (i == sourceRow && j == sourceCol) {
                    continue;
                }
                Cell cell = matrix[wrappedRow][wrappedCol];
                if (cell.isFull() && cell.getEntityInstance().getEntityDefinition().getName().equals(targetEntity.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Cell findEntityInstanceCell(EntityInstance entityInstance) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Cell cell = matrix[i][j];
                if (cell.getEntityInstance()!=null && cell.getEntityInstance().getId() == entityInstance.getId()) {
                    return cell;
                }
            }
        }
        return null;
    }

    public void placeEntityRandomly(EntityInstance entityInstance) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        Random random = new Random();

        while (true) {
            int randomRow = random.nextInt(numRows);
            int randomCol = random.nextInt(numCols);

            if (!matrix[randomRow][randomCol].isFull()) {
                matrix[randomRow][randomCol].setEntityInstance(entityInstance);
                matrix[randomRow][randomCol].setFull(true);
                break;
            }
        }
    }

    public void placeEntity(EntityInstance entityInstance, int row, int col) {
        matrix[row][col].setEntityInstance(entityInstance);
        matrix[row][col].setFull(true);
    }
}
