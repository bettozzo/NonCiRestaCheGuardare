package unitn.app.remotedb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Users(
    @PrimaryKey var userId: String,
    @ColumnInfo(name = "coloreTemaPrincipale") var coloreTemaPrincipale: Colori,
    @ColumnInfo(name = "temaScuro") var temaScuro: Boolean,
) {
    companion object {
        fun getStructure(): String {
            return "userId, " +
                    "coloreTemaPrincipale ( " +
                    coloriGetStructure() +
                    ")," +
                    "temaScuro"
        }
    }
}


@Serializable
data class InsertUsersParams(
    var userId: String,
)
