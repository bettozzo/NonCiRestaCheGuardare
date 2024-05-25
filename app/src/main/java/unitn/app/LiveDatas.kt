package unitn.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import unitn.app.api.LocalMedia

object LiveDatas {
    /*----------COLORE-----------*/
    private var mutLiveColore: MutableLiveData<String> = MutableLiveData()
    val liveColore: LiveData<String>
        get() = mutLiveColore;

    fun setColore(colore: String) {
        mutLiveColore.value = colore
    }


    /*----------MEDIA-----------*/
    private val mutLiveListMedia = MutableLiveData<List<LocalMedia>>(emptyList());
    val liveListMedia: LiveData<List<LocalMedia>>
        get() = mutLiveListMedia;

    fun addMedia(media: LocalMedia) {
        val lista = (mutLiveListMedia.value ?: emptyList()).toMutableList();
        lista.add(media);
        mutLiveListMedia.value = lista;

    }
    fun removeMedia(mediaId: Int) {
        val lista = (mutLiveListMedia.value ?: return).toMutableList();
        val size_before = lista.size;
        lista.removeIf{it.mediaId == mediaId};
        Log.d("diff", (size_before-lista.size).toString())
        Log.d("mediaId", mediaId.toString()+"\n")
        mutLiveListMedia.value = lista;
    }
}