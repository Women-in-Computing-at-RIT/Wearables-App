/**
 * File: AnstractHealthData.java
 * Created by Cara on 6/21/2016.
 */

package edu.rit.wic.stressmonitor.requery.model;

import io.requery.Key;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Generated;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;
import io.requery.Persistable;

import java.util.Date;

@Entity
public interface HealthData extends Observable, Parcelable, Persistable {
    @Key @Generated
    Date getDateTime();

    @Bindable
    int getBPM();

    @Bindable
    int getIBI();

    @Bindable
    int getConductance();

    @Bindable
    int getStatisticalData();

//    @Bindable
//    @ForeignKey
//    int getId();
}
