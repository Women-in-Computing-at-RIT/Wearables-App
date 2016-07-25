/**
 * File: ScheduleTasks.java
 * Created by Cara on 6/21/2016.
 */

package edu.rit.wic.stressmonitor.requery.model;


import io.requery.Key;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Generated;
import io.requery.ManyToMany;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;
import io.requery.CascadeAction;
import io.requery.Persistable;

import java.util.Date;

@Entity
public interface ScheduleTasks extends Observable, Parcelable, Persistable {
    @Key @Generated
    int getTaskId();

    @Bindable
    String getLabel();

    @Bindable
    Date getStartTime();

    @Bindable
    Date getEndTime();

    @Bindable
    int getDuration();

//    @ForeignKey
//    @Bindable
//    @ManyToMany(mappedBy = "scheduleTask", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
//    Person getUser();

}
