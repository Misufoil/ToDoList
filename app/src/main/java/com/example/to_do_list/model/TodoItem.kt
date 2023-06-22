package com.example.to_do_list.model

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.util.*

data class TodoItem (
    val id: UUID = UUID.randomUUID(),
    var desc: String,
    var priority: Importance,
    var deadline: String?,
    var isDone: Boolean = false,
    val createdAt: LocalDate = LocalDate.now(),
    var modifiedDate: LocalDate?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(UUID::class.java.classLoader) as UUID,
        parcel.readString()!!,
        Importance.valueOf(parcel.readString()!!),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        LocalDate.ofEpochDay(parcel.readLong()),
        parcel.readString()?.let { LocalDate.parse(it) }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(desc)
        parcel.writeString(priority.name)
        parcel.writeString(deadline)
        parcel.writeByte(if (isDone) 1 else 0)
        parcel.writeLong(createdAt.toEpochDay())
        parcel.writeString(modifiedDate?.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TodoItem> {
        override fun createFromParcel(parcel: Parcel): TodoItem {
            return TodoItem(parcel)
        }

        override fun newArray(size: Int): Array<TodoItem?> {
            return arrayOfNulls(size)
        }
    }
}



