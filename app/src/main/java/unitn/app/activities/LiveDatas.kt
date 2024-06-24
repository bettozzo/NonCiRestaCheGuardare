package unitn.app.activities

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.runBlocking
import unitn.app.api.LocalMedia
import unitn.app.remotedb.RemoteDAO

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

    fun updateIsDarkTheme(isDark: Boolean, context: Context) {
        mutLiveIsDarkTHeme.value = isDark
        runBlocking {
            RemoteDAO(context, coroutineContext).updateDarkTheme(isDark)
            if (isDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
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

    fun getColore(): String {
        return mutLiveColore.value!!;
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
    private val watchList = emptyList<LocalMedia>().toMutableList();
    private val mutWatchList = MutableLiveData(watchList.toList());
    val liveWatchlist: LiveData<List<LocalMedia>>
        get() = mutWatchList;

    fun addMedia(media: LocalMedia) {
        if (!watchList.contains(media)) {
            watchList.add(media)
            mutWatchList.postValue(watchList);
        }
    }

    fun removeMedia(mediaId: Int) {
        watchList.removeIf { it.mediaId == mediaId };
        mutWatchList.value = watchList;
    }

    fun emptyMedia() {
        watchList.removeAll { true };
        mutWatchList.value = emptyList();
    }

    fun tickIsLocal(mediaid: Int) {
        val media = watchList.find { it.mediaId == mediaid }!!
        val index = watchList.indexOf(media)
        media.isLocallySaved = !media.isLocallySaved;

        watchList[index] = media;
        mutWatchList.postValue(watchList);
    }

    /*--------------------------*/
    /*---------Ricerca----------*/
    /*--------------------------*/
    private val ricerca = emptyList<LocalMedia>().toMutableList();
    private val mutLiveRicercaMedia = MutableLiveData(ricerca.toList());
    val liveRicercaMedia: LiveData<List<LocalMedia>>
        get() = mutLiveRicercaMedia;

    var mediaRicercato: String = ""
    fun addRicercaMedia(media: LocalMedia) {
        ricerca.add(media)
        mutLiveRicercaMedia.postValue(ricerca);
    }

    fun removeRicercaMedia(media: LocalMedia) {
        ricerca.removeIf { it.mediaId == media.mediaId };
        mutLiveRicercaMedia.postValue(ricerca);
    }

    fun emptyRicercaMedia() {
        ricerca.removeAll { true };
        mutLiveRicercaMedia.postValue(emptyList());
    }
}