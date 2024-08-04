package unitn.app.activities.dettaglio

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerFragmentAdapter(fragmentActivity: FragmentActivity, extras: Bundle) :
    FragmentStateAdapter(fragmentActivity) {
    private val info = FragmentInfo(extras);
    private val doveVedere = FragmentDoveVedere(extras);
    private val sezioneNote = FragmentSezioneNote();
    private val segnaposto = FragmentSegnaposto();

    private val isFilm = extras.getBoolean("isFilm")
    override fun getItemCount(): Int {
        return if (isFilm) {
            3
        } else {
            4;
        }
    }

    override fun createFragment(position: Int): Fragment {
        if (isFilm) {
            when (position) {
                0 -> return info
                1 -> return doveVedere
                2 -> return sezioneNote
            }
        } else {
            when (position) {
                0 -> return info
                1 -> return segnaposto
                2 -> return doveVedere
                3 -> return sezioneNote
            }
        }
        return info
    }



}