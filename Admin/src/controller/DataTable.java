package controller;

public class DataTable {
    private final String entityName;
    private int entityAmount;

    public DataTable(String entityName, int entityAmount) {
        this.entityName = entityName;
        this.entityAmount = entityAmount;
    }
    public String getEntityName() {
        return entityName;
    }

    public int getEntityAmount() {
        return entityAmount;
    }

    public void setEntityAmount(int endPopulation) {
        this.entityAmount = endPopulation;
    }
}
