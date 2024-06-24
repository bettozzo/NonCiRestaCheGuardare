package unitn.app.remotedb

import android.graphics.Color
import kotlinx.serialization.Serializable


@Serializable
enum class ColoriName {
    Azzurro, Verde, Viola, Ocra
}


private val colori = arrayOf(
    Colori(ColoriName.Azzurro, "#2d95eb"),
    Colori(ColoriName.Verde, "#008c00"),
    Colori(ColoriName.Viola, "#852deb"),
    Colori(ColoriName.Ocra, "#ff9900"),
);

@Serializable
data class Colori(
    var colorName: ColoriName,
    var colorCode: String,
) {
    companion object {
        fun getColori(): Array<Colori> {
            return colori;
        }

        fun getColore(name: ColoriName): Colori {
            return colori.find { it.colorName == name }!!;
        }

        fun getTabColore(value: ColoriName): Int {
            return when (value) {
                ColoriName.Azzurro -> {
                    Color.parseColor("#77edff")
                }

                ColoriName.Verde -> {
                    Color.parseColor("#A8FF2F")
                }

                ColoriName.Viola -> {
                    Color.parseColor("#cf06f9")
                }

                ColoriName.Ocra -> {
                    Color.parseColor("#ffc266")
                }
            }
        }
    }
}


fun coloriGetStructure(): String {
    return """
              colorName,
              colorCode
            """.trimIndent().replace("\n", "")
}

