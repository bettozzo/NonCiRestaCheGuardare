package unitn.app.activities.profilo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerFragmentAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val cronolgia = FragmentCronologia();
    private val personalizzazioneUI = FragmentPersonalizzazioneUI();
    private val piattaforme = FragmentPiattaforme();
    private val settings = FragmentSettings();

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return piattaforme
            1 -> return personalizzazioneUI
            2 -> return settings
            3 -> return cronolgia
        }
        return piattaforme
    }

}