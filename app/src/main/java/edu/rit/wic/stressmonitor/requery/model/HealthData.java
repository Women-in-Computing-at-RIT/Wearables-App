/**
 * File: HealthData.java
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

import io.requery.Nullable;
import io.requery.Persistable;

import java.util.Date;


/**
 * Interface to model the Health data for a user
 */
@Entity
public interface HealthData extends Observable, Parcelable, Persistable {
    @Key
    Date getDateTime();

    @Bindable
    @Nullable
    int getBPM();

    @Bindable
    @Nullable
    int getIBI();

    @Bindable
    @Nullable
    int getConductance();

    @Bindable
    @Nullable
    int getStatisticalData();

//    @Bindable
//    @ForeignKey
//    int getId();
}
