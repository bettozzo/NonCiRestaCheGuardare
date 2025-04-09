package unitn.app.activities.homepage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerFragmentAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val filmFragment = FragmentFilm();
    private val seriesFragment = FragmentSeries();

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return filmFragment
            1 -> return seriesFragment
        }
        return filmFragment
    }

}
