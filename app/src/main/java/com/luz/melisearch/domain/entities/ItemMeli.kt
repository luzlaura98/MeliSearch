package com.luz.melisearch.domain.entities

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Luz on 14/8/2022.
 */
data class ItemMeli(
    val id : String,
    val title : String?,
    val price : Float, //1000
    val thumbnail : String?
) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString(),
        parcel.readFloat(),
        parcel.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeFloat(price)
        parcel.writeString(thumbnail)
    }

    companion object CREATOR : Parcelable.Creator<ItemMeli> {
        override fun createFromParcel(parcel: Parcel): ItemMeli {
            return ItemMeli(parcel)
        }

        override fun newArray(size: Int): Array<ItemMeli?> {
            return arrayOfNulls(size)
        }
    }

}
