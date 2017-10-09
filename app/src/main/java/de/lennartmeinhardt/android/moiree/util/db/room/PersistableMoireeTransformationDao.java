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
public interface PersistableMoireeTransformationDao
        extends
        Inserter<PersistableMoireeTransformation>,
        Deleter<PersistableMoireeTransformation>,
        Querier<PersistableMoireeTransformation> {

    @Override
    @Query("SELECT * FROM moireeTransformations")
    List<PersistableMoireeTransformation> queryAll();

    @Override
    @Delete
    void deleteAll(List<PersistableMoireeTransformation> transformations);

    @Override
    @Insert
    void insertAll(List<PersistableMoireeTransformation> transformations);

}
