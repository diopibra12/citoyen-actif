package ca.uqtr.citoyenactif.api.dto;

public class ReportCreateDTO {

    private String id;

    public ReportCreateDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
