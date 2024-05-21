package unitn.app.remotedb

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters {

    @TypeConverter
    fun stringToColors(data: String?): CustomColors {
        val gson = Gson()
        if (data == null) {
            return CustomColors("Verde", "#008c00")
        }
        val type = object : TypeToken<CustomColors>() {}.type
        return gson.fromJson<CustomColors>(data, type)
    }

    @TypeConverter
    fun colorsToString(myObjects: CustomColors?): String {
        val gson = Gson()
        return gson.toJson(myObjects)
    }
}
