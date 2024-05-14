package unitn.app.localdb

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import unitn.app.api.MediaDetails

@ProvidedTypeConverter
class Converters {

    @TypeConverter
    fun stringToPlatform(data: String?): List<Pair<String, String>> {
        val gson = Gson()
        if (data == null) {
            return emptyList<Pair<String, String>>()
        }
        val listType = object : TypeToken<List<Pair<String, String>?>?>() {}.type
        return gson.fromJson<List<Pair<String, String>>>(data, listType)
    }

    @TypeConverter
    fun platformToString(myObjects: List<Pair<String, String>?>?): String {
        val gson = Gson()
        return gson.toJson(myObjects)
    }
}
