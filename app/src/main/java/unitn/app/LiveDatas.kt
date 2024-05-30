package unitn.app

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.Button
import android.widget.ImageButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import unitn.app.api.LocalMedia

object LiveDatas {
    /*--------------------------*/
    /*----------TEMA------------*/
    /*--------------------------*/

    private var mutLiveIsDarkTHeme: MutableLiveData<Boolean> = MutableLiveData(false)
    val liveIsDarkTheme: LiveData<Boolean>
        get() = mutLiveIsDarkTHeme;

    fun setIsDarkTheme(isDark: Boolean) {
        mutLiveIsDarkTHeme.value = isDark
    }


    /*--------------------------*/
    /*----------COLORE----------*/
    /*--------------------------*/
    private var mutLiveColore: MutableLiveData<String> = MutableLiveData()
    val liveColore: LiveData<String>
        get() = mutLiveColore;

    fun setColore(colore: String) {
        mutLiveColore.value = colore
    }

    fun updateColorsOfImgButtons(imageButtons: List<ImageButton>) {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(-android.R.attr.state_enabled), // disabled
            intArrayOf(-android.R.attr.state_checked), // unchecked
            intArrayOf(android.R.attr.state_pressed)  // pressed
        )
        val colors = intArrayOf(
            Color.parseColor(liveColore.value),
            Color.parseColor(liveColore.value),
            Color.parseColor(liveColore.value),
            Color.parseColor(liveColore.value)
        )

        val myList = ColorStateList(states, colors)

        for (button in imageButtons) {
            button.backgroundTintList = myList
        }
    }

    fun updateColorsOfButtons(imageButtons: List<Button>) {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(-android.R.attr.state_enabled), // disabled
            intArrayOf(-android.R.attr.state_checked), // unchecked
            intArrayOf(android.R.attr.state_pressed)  // pressed
        )
        val colors = intArrayOf(
            Color.parseColor(liveColore.value),
            Color.parseColor(liveColore.value),
            Color.parseColor(liveColore.value),
            Color.parseColor(liveColore.value)
        )

        val myList = ColorStateList(states, colors)

        for (button in imageButtons) {
            button.backgroundTintList = myList
        }
    }


    /*--------------------------*/
    /*--------WatchList---------*/
    /*--------------------------*/
    private val mutWatchList = MutableLiveData<List<LocalMedia>>(emptyList());
    val liveWatchlist: LiveData<List<LocalMedia>>
        get() = mutWatchList;

    fun addMedia(media: LocalMedia) {
        val lista = (mutWatchList.value ?: emptyList()).toMutableList();
        lista.add(media);
        mutWatchList.postValue(lista);
    }

    fun removeMedia(mediaId: Int) {
        val lista = (mutWatchList.value ?: return).toMutableList();
        lista.removeIf { it.mediaId == mediaId };
        mutWatchList.value = lista;
    }

    fun emptyMedia() {
        mutWatchList.value = emptyList();
    }


    /*--------------------------*/
    /*---------Ricerca----------*/
    /*--------------------------*/
    private val mutLiveRicercaMedia = MutableLiveData<List<LocalMedia>>(emptyList());
    val liveRicercaMedia: LiveData<List<LocalMedia>>
        get() = mutLiveRicercaMedia;

    var mediaRicercato: String = "Kill Bill"
    fun addRicercaMedia(media: LocalMedia) {
        val lista = (mutLiveRicercaMedia.value ?: emptyList()).toMutableList();
        lista.add(media);
        mutLiveRicercaMedia.postValue(lista);
    }

    fun removeRicercaMedia(media: LocalMedia) {
        val lista = (mutLiveRicercaMedia.value ?: emptyList()).toMutableList();
        lista.remove(media);
        mutLiveRicercaMedia.postValue(lista);
    }

    fun emptyRicercaMedia() {
        mutLiveRicercaMedia.postValue(emptyList());
    }
}