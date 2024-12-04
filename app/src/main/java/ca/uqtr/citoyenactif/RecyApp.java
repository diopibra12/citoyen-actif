package ca.uqtr.citoyenactif;

import androidx.multidex.MultiDexApplication;

import ca.uqtr.citoyenactif.db.entity.CitoyenEntity;
import ca.uqtr.citoyenactif.db.entity.ReportEntity;

public class RecyApp extends MultiDexApplication {

    private ReportEntity report;
    private CitoyenEntity currentCitoyen;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public ReportEntity getCurrent() {
        return report;
    }

    public void setCurrent(ReportEntity report) {
        this.report = report;
    }

    public CitoyenEntity getCurrentCitoyen() {
        return currentCitoyen;
    }

    public void setCurrentCitoyen(CitoyenEntity currentCitoyen) {
        this.currentCitoyen = currentCitoyen;
    }
}
