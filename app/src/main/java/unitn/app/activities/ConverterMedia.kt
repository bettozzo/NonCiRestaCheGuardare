package unitn.app.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.runBlocking
import unitn.app.api.LocalMedia
import unitn.app.remotedb.Media
import unitn.app.remotedb.Piattaforme
import unitn.app.remotedb.RemoteDAO

object ConverterMedia {
    fun toRemote(media: LocalMedia): Media {
        var generi: StringBuilder? = StringBuilder();
        if (media.generi != null) {
            media.generi.forEach { generi?.append(it)?.append(",") }
        } else {
            generi = null;
        }
        return Media(
            media.mediaId,
            media.isFilm,
            media.title,
            media.posterPath,
            media.sinossi!!,
            generi.toString(),
            media.periodoPubblicazione,
            media.durata
        )
    }

    fun toLocal(
        context: Context,
        media: Media,
        isLocal: Boolean,
        appCompatActivity: AppCompatActivity,
    ): LocalMedia = runBlocking {
        val remoteDao = RemoteDAO(
            context,
            coroutineContext
        );

        val piattaforme = ConverterPiattaforme.toLocal(
            remoteDao.getDoveVedereMedia(
                media.mediaID,
                appCompatActivity
            )
        )
        return@runBlocking LocalMedia(
            media.mediaID,
            media.is_film,
            media.titolo,
            piattaforme,
            media.poster_path,
            isLocal,
            media.sinossi,
            media.annoUscita,
            media.generi,
            media.durata
        )
    }
}


object ConverterPiattaforme {
    fun toLocal(piattaforme: List<Piattaforme>): List<Pair<String, String>> {
        val list = emptyList<Pair<String, String>>().toMutableList();
        piattaforme.map { list.add(Pair(it.nome, it.logo_path)) }
        return list;
    }
}