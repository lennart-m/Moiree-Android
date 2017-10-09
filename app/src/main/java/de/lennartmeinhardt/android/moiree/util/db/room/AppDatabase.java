package de.lennartmeinhardt.android.moiree.util.db.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {PersistableMoireeTransformation.class, PersistableMoireeColors.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase db;

    public abstract PersistableMoireeTransformationDao persistableMoireeTransformationDao();

    public abstract PersistableMoireeColorsDao persistableMoireeColorsDao();


    public static AppDatabase getInstance(Context context) {
        if(db == null || (! db.isOpen())) {
            db = Room.databaseBuilder(context, AppDatabase.class, "moiree-database.db").build();
        }

        return db;
    }
}
