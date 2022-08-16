package com.luz.melisearch.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Luz on 14/8/2022.
 */
data class ItemMeliDetail(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String?,
    @SerializedName("subtitle")
    val subtitle: String?,
    @SerializedName("price")
    val price: Float,
    @SerializedName("sold_quantity")
    val soldQuantity: Int?,
    @SerializedName("permalink")
    val permalink: String?,
    @SerializedName("pictures")
    val pictures: List<PictureMeli>
) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.createTypedArrayList(PictureMeli)?: listOf()
    )

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(subtitle)
        parcel.writeFloat(price)
        parcel.writeValue(soldQuantity)
        parcel.writeString(permalink)
        parcel.writeTypedList(pictures)
    }

    companion object CREATOR : Parcelable.Creator<ItemMeliDetail> {
        override fun createFromParcel(parcel: Parcel): ItemMeliDetail {
            return ItemMeliDetail(parcel)
        }

        override fun newArray(size: Int): Array<ItemMeliDetail?> {
            return arrayOfNulls(size)
        }
    }

}

data class PictureMeli(
    @SerializedName("id")
    val id: String,
    @SerializedName("url")
    val url: String?,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(url)
    }

    companion object CREATOR : Parcelable.Creator<PictureMeli> {
        override fun createFromParcel(parcel: Parcel): PictureMeli {
            return PictureMeli(parcel)
        }

        override fun newArray(size: Int): Array<PictureMeli?> {
            return arrayOfNulls(size)
        }
    }

}