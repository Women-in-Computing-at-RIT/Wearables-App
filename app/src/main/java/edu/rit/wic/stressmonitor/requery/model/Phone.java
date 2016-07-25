package edu.rit.wic.stressmonitor.requery.model;


import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.Persistable;

@Entity
public interface Phone extends Observable, Parcelable, Persistable {

    @Key
    @Generated
    int getId();

    @Bindable
    String getPhoneNumber();
    void setPhoneNumber(String phoneNumber);

    @Bindable
    @ManyToOne
    Person getOwner();

    void setOwner(Person person);
}
