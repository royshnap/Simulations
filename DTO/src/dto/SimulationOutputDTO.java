package dto;


public class SimulationOutputDTO {

    private final int id;
    private final String reasonsOfEnding;

    public int getId() {
        return id;
    }


    public String getReasonsOfEnding() {
        return reasonsOfEnding;
    }

    public SimulationOutputDTO(int id,String reasonsOfEnding) {
        this.id = id;
        this.reasonsOfEnding = reasonsOfEnding;
    }
}
