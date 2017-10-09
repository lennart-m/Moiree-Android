package de.lennartmeinhardt.android.moiree.util.db.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.lennartmeinhardt.android.moiree.util.db.Deleter;
import de.lennartmeinhardt.android.moiree.util.db.Inserter;
import de.lennartmeinhardt.android.moiree.util.db.Querier;

@Dao
public interface PersistableMoireeColorsDao
        extends
        Inserter<PersistableMoireeColors>,
        Deleter<PersistableMoireeColors>,
        Querier<PersistableMoireeColors> {

    @Query("SELECT * FROM moireeColors")
    @Override
    List<PersistableMoireeColors> queryAll();

    @Delete
    @Override
    void deleteAll(List<PersistableMoireeColors> persistableMoireeColors);

    @Insert
    @Override
    void insertAll(List<PersistableMoireeColors> persistableMoireeColors);

}
