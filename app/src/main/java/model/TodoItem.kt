package model

import android.app.Activity
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContracts
import com.example.to_do_list.MainActivity.Companion.TODO_ITEM_KEY
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
        parcel.readValue(LocalDate::class.java.classLoader) as LocalDate?
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(desc)
        parcel.writeString(priority.name)
        parcel.writeString(deadline)
        parcel.writeByte(if (isDone) 1 else 0)
        parcel.writeLong(createdAt.toEpochDay())
        parcel.writeValue(modifiedDate)
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


