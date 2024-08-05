package unitn.app.api

object Genres {

    private val listOfGeneres = initData();

    fun getGenres(listId: List<Int>?): String?{
        if(listId == null){
            return null;
        }
        val results = StringBuilder();
        listId.forEach { idDaCercare ->
            val genereTrovato = listOfGeneres.find { it.id == idDaCercare};
            val genere = genereTrovato?.name ?: "errore"
            results.append(genere).append(", ")
        }

        return results.removeSuffix(",").toString();
    }

    private fun initData(): List<GenereStructure> {
        return listOf(
            GenereStructure(12, "Avventura"),
            GenereStructure(14, "Fantasy"),
            GenereStructure(16, "Animazione"),
            GenereStructure(18, "Dramma"),
            GenereStructure(27, "Horror"),
            GenereStructure(28, "Azione"),
            GenereStructure(35, "Commedia"),
            GenereStructure(36, "Storia"),
            GenereStructure(37, "Western"),
            GenereStructure(53, "Thriller"),
            GenereStructure(80, "Crimine"),
            GenereStructure(99, "Documentario"),
            GenereStructure(878, "Fantascienza"),
            GenereStructure(9648, "Mistero"),
            GenereStructure(10402, "Musica"),
            GenereStructure(10749, "Romance"),
            GenereStructure(10751, "Famiglia"),
            GenereStructure(10752, "Guerra"),
            GenereStructure(10759, "Azione & Avventura"),
            GenereStructure(10762, "Kids"),
            GenereStructure(10763, "News"),
            GenereStructure(10764, "Reality"),
            GenereStructure(10765, "Sci-Fi & Fantasy"),
            GenereStructure(10766, "Soap"),
            GenereStructure(10767, "Talk"),
            GenereStructure(10768, "War & politics"),
            GenereStructure(10770, "Televisione film"),
        )
    }
    private data class GenereStructure(
        val id: Int,
        val name: String,
    )
}

