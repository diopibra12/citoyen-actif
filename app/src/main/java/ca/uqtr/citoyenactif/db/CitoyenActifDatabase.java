package ca.uqtr.citoyenactif.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ca.uqtr.citoyenactif.db.converter.LocalDateTimeTypeConverter;
import ca.uqtr.citoyenactif.db.dao.CitoyenDAO;
import ca.uqtr.citoyenactif.db.dao.ReportDAO;
import ca.uqtr.citoyenactif.db.entity.CitoyenEntity;
import ca.uqtr.citoyenactif.db.entity.ReportEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ReportEntity.class, CitoyenEntity.class}, version = 10)
@TypeConverters({LocalDateTimeTypeConverter.class})
public abstract class CitoyenActifDatabase extends RoomDatabase {
    private static CitoyenActifDatabase INSTANCE;
    private final ExecutorService writPool = Executors.newSingleThreadExecutor();
    private final ExecutorService readPool = Executors.newFixedThreadPool(4);

    public synchronized static CitoyenActifDatabase instance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, CitoyenActifDatabase.class, "database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;

    }

    public abstract ReportDAO reportDAO();

    public void read(Runnable r) {
        readPool.submit(r);
    }

    public abstract CitoyenDAO citoyenDAO();

    public void write(Runnable r) {
        writPool.submit(r);
    }
}
