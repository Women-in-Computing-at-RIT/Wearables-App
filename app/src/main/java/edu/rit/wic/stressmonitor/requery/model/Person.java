/**
 * File: Person.java
 * Created by Cara on 6/21/2016.
 */
package edu.rit.wic.stressmonitor.requery.model;

import io.requery.Column;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Generated;
import io.requery.Index;
import io.requery.Key;
import io.requery.Nullable;
import io.requery.OneToMany;
import android.databinding.Bindable;
import io.requery.Persistable;
import io.requery.CascadeAction;
import io.requery.query.MutableResult;
import android.databinding.Observable;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public interface Person extends Observable, Parcelable, Persistable {
    @Key
    @Generated
    int getId();

    @Bindable
    @Nullable
    String getFirstName();

    @Bindable
    @Nullable
    String getLastName();

    @Bindable
    @Index(value = "email_index")
    String getEmail();

    @Bindable
    String getPassword();

    @Bindable
    Date getBirthday();

    @Bindable
    int getAge();

    @Bindable
    String getGender();

    @Bindable
    String getEthnicity();

    @OneToMany(mappedBy = "owner", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    MutableResult<Phone> getPhoneNumbers();

    @Bindable
    @Column(unique = true)
    UUID getUUID();

//    @OneToMany(mappedBy = "userId", cascade ={CascadeAction.DELETE, CascadeAction.SAVE})
    MutableResult<Integer> getDeviceIds();

    @OneToMany(mappedBy = "owner", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    List<Phone> getPhoneNumberList();

//    @OneToMany(mappedBy = "owner", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    List<Integer> getDeviceIdsList();

//    @Bindable
//    @ForeignKey
//    int getFamilyId();
}
