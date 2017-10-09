package de.lennartmeinhardt.android.moiree.util.db.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import de.lennartmeinhardt.android.moiree.MoireeTransformation;

@Entity(tableName = "moireeTransformations")
public class PersistableMoireeTransformation {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String transformationName;

    @ColumnInfo(name = "rotation")
    public float rotation;
    @ColumnInfo(name = "commonScaling")
    public float commonScaling;
    @ColumnInfo(name = "scalingX")
    public float scalingX;
    @ColumnInfo(name = "scalingY")
    public float scalingY;
    @ColumnInfo(name = "translationX")
    public float translationX;
    @ColumnInfo(name = "translationY")
    public float translationY;
    @ColumnInfo(name = "useCommonScaling")
    public boolean useCommonScaling;


    public void readDataFromTransformation(MoireeTransformation source) {
        rotation = source.rotation.get();
        commonScaling = source.commonScaling.get();
        scalingX = source.scalingX.get();
        scalingY = source.scalingY.get();
        translationX = source.translationX.get();
        translationY = source.translationY.get();
        useCommonScaling = source.useCommonScaling.get();
    }

    public void storeDataInTransformation(MoireeTransformation destination) {
        destination.rotation.set(rotation);
        destination.commonScaling.set(commonScaling);
        destination.scalingX.set(scalingX);
        destination.scalingY.set(scalingY);
        destination.translationX.set(translationX);
        destination.translationY.set(translationY);
        destination.useCommonScaling.set(useCommonScaling);
    }

}
