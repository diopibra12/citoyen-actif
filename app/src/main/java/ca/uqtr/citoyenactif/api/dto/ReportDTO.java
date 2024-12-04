package ca.uqtr.citoyenactif.api.dto;

public class ReportDTO {
    public ReportDTO(long id, String titre, String description, String imagePath, boolean duplicate, boolean repaired, double lng, double lat, long citoyenId, boolean isNew) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.imagePath = imagePath;
        this.duplicate = duplicate;
        this.repaired = repaired;
        this.lng = lng;
        this.lat = lat;
        this.citoyenId = citoyenId;
        this.isNew = isNew;
    }

    private long id;
    private String titre;
    private String description;
    private String imagePath;
    private boolean duplicate;
    private boolean repaired;
    private double lng;
    private double lat;
    private long citoyenId;
    private boolean isNew;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public boolean isRepaired() {
        return repaired;
    }

    public void setRepaired(boolean repaired) {
        this.repaired = repaired;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public long getCitoyenId() {
        return citoyenId;
    }

    public void setCitoyenId(long citoyenId) {
        this.citoyenId = citoyenId;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
