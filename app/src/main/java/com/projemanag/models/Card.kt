package com.projemanag.models

import android.os.Parcel
import android.os.Parcelable

data class Card(
    var name: String = "",
    val createBy: String= "",
    val assignedTo: ArrayList<String> = ArrayList(),
    val labelColor: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel?, flags: Int): Unit = with(dest) {
        dest?.writeString(name)
        dest?.writeString(createBy)
        dest?.writeStringList(assignedTo)
        dest?.writeString(labelColor)
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
