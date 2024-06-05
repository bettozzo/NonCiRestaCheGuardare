package unitn.app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.runBlocking
import unitn.app.api.LocalMedia
import unitn.app.remotedb.Media
import unitn.app.remotedb.Piattaforme
import unitn.app.remotedb.RemoteDAO

object ConverterMedia {
    fun toRemote(media: LocalMedia): Media {
        return Media(media.mediaId, media.isFilm, media.title, media.posterPath, media.sinossi!!)
    }
    fun toLocal(context: Context, media: Media, isLocal: Boolean, appCompatActivity: AppCompatActivity): LocalMedia = runBlocking {
        val remoteDao = RemoteDAO(
            context,
            coroutineContext
        );

        val piattaforme = ConverterPiattaforme.toLocal(remoteDao.getDoveVedereMedia(media.mediaID, appCompatActivity))
        return@runBlocking LocalMedia(media.mediaID, media.is_film, media.titolo, piattaforme, media.poster_path, isLocal, media.sinossi)
    }

    fun toLocal(media: Media, isLocal: Boolean, piattaforme:List<Pair<String, String>>): LocalMedia{
        return LocalMedia(media.mediaID, media.is_film, media.titolo, piattaforme, media.poster_path, isLocal, media.sinossi)
    }

}


object ConverterPiattaforme{
    fun toLocal(piattaforme: List<Piattaforme>): List<Pair<String, String>> {
        val list = emptyList<Pair<String, String>>().toMutableList();
        piattaforme.map {  list.add(Pair(it.nome, it.logo_path))}
        return list;
    }
}