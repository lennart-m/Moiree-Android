package de.lennartmeinhardt.android.moiree.util.db.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import de.lennartmeinhardt.android.moiree.MoireeColors;

@Entity(tableName = "moireeColors")
public class PersistableMoireeColors {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "foregroundColor")
    public int foregroundColor;

    @ColumnInfo(name = "backgroundColor")
    public int backgroundColor;

    public void readDataFromMoireeColors(MoireeColors source) {
        foregroundColor = source.foregroundColor.get();
        backgroundColor = source.backgroundColor.get();
    }

    public void storeDataInMoireeColors(MoireeColors destination) {
        destination.foregroundColor.set(foregroundColor);
        destination.backgroundColor.set(backgroundColor);
    }

}
