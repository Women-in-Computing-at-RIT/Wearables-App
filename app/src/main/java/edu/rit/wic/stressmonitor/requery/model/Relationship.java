/**
 * File: Relationship.java
 * Created by Cara on 6/21/2016.
 */
package edu.rit.wic.stressmonitor.requery.model;

import io.requery.Key;
import io.requery.Entity;
import io.requery.Generated;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;
import io.requery.Persistable;


/**
 * Interface that models relationships between family members for users
 */
@Entity
public interface Relationship extends Observable, Parcelable, Persistable {
    @Key @Generated
    int getRelationshipId();

    @Bindable
    String getType();
}
