package ca.uqtr.citoyenactif.db.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = CitoyenEntity.class, parentColumns = "id", childColumns = "citoyen_id"))
public class ReportEntity extends Auditable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String titre;
    private String description;
    private String imagePath;
    private boolean duplicate;
    private boolean repaired;
    private double lng;
    private double lat;
    @Ignore
    private boolean isNew;

    @ColumnInfo(name = "citoyen_id")
    private long citoyenId;

    public ReportEntity() {
        super();
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        return id == ((ReportEntity) obj).id;
    }

    public long getCitoyenId() {
        return citoyenId;
    }

    public void setCitoyenId(long citoyenId) {
        this.citoyenId = citoyenId;
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

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
