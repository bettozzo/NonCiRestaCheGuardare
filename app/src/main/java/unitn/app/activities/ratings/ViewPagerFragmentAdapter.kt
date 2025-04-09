package unitn.app.activities.ratings

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import unitn.app.remotedb.CronologiaConRating
import unitn.app.remotedb.Users

class ViewPagerFragmentAdapter(
    fragmentActivity: FragmentActivity,
    cronologia: List<CronologiaConRating>,
    ratingsMedia: List<Pair<Users, CronologiaConRating>>,
) :
    FragmentStateAdapter(fragmentActivity) {
    private val recensioneUser = FragmentRecensioneUser(cronologia);
    private val recensioniAltri = FragmentRecensioni(ratingsMedia);

    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return recensioneUser
            1 -> return recensioniAltri
        }
        return recensioneUser
    }
}
