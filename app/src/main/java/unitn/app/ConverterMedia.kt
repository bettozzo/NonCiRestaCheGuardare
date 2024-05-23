package unitn.app

import android.content.Context
import kotlinx.coroutines.runBlocking
import unitn.app.api.LocalDbMedia
import unitn.app.remotedb.Media
import unitn.app.remotedb.Piattaforme
import unitn.app.remotedb.RemoteDAO

object ConverterMedia {
    fun toRemote(media: LocalDbMedia): Media {
        return Media(media.mediaId, media.isFilm, media.title, media.posterPath, media.isLocallySaved, media.sinossi!!)
    }
    fun toLocal(context: Context, media: Media): LocalDbMedia = runBlocking {
        val remoteDao = RemoteDAO(
            context,
            coroutineContext
        );

        val piattaforme = ConverterPiattaforme.toLocal(remoteDao.getDoveVedereMedia(media.mediaID))
        return@runBlocking LocalDbMedia(media.mediaID, media.is_film, media.titolo, piattaforme, media.poster_path, media.is_local, media.sinossi)
    }
}


object ConverterPiattaforme{
    fun toLocal(piattaforme: List<Piattaforme>): List<Pair<String, String>> {
        val list = emptyList<Pair<String, String>>().toMutableList();
        piattaforme.map {  list.add(Pair(it.nome, it.logo_path))}
        return list;
    }
}