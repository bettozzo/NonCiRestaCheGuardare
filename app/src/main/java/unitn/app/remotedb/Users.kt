package unitn.app.remotedb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.NotNull

@Entity @Serializable
data class Users(
    @PrimaryKey var userId: String,
    @ColumnInfo(name = "colours") var coloreTemaPrincipale: CustomColors
)
