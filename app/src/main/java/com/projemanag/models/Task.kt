package com.projemanag.models

import android.os.Parcel
import android.os.Parcelable
import android.service.quicksettings.Tile

data class Task(
    var tile: String? = "",
    var createBy : String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int): Unit = with(dest) {
        this?.writeString(tile)
        this?.writeString(createBy)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}