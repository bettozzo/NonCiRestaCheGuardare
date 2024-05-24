package unitn.app.remotedb

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun stringToColors(data: String?): Colori {
        val gson = Gson()
        if (data == null) {
            return Colori("Verde", "#008c00")
        }
        val type = object : TypeToken<Colori>() {}.type
        return gson.fromJson<Colori>(data, type)
    }

    @TypeConverter
    fun colorsToString(myObjects: Colori?): String {
        val gson = Gson()
        return gson.toJson(myObjects)
    }
}
