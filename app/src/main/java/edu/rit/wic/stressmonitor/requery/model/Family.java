/**
 * File: Family.java
 * Created by Cara on 6/21/2016.
 */

package edu.rit.wic.stressmonitor.requery.model;

import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Generated;
import io.requery.Key;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;
import io.requery.Persistable;


/**
 * Interface to model the Family relationship
 */
@Entity
public interface Family extends Observable, Parcelable, Persistable{
    @Key
    @Generated
    int getFamilyID();

    @Bindable
    String getFamilyName();

//    @Bindable
//    @ForeignKey
//    int getRelationshipId();
}
