package unitn.app.activities.dettaglio

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unitn.app.activities.LiveDatas
import unitn.app.remotedb.Piattaforme
import unitn.app.remotedb.RemoteDAO

class FragmentDoveVedere(private val extras: Bundle) : Fragment() {
    private var root: View? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_dove_vedere, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //id
        val id = extras.getInt("id");

        //platforms
        showAvailablePlatforms(extras)

        //switch
        val switch = view.findViewById<SwitchCompat>(R.id.switchLocal)
        switch.isChecked = extras.getBoolean("isInLocal")
        switch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                val remoteDao = RemoteDAO(
                    requireActivity(),
                    coroutineContext
                );
                remoteDao.changeIsLocal(id, isChecked)
                LiveDatas.tickIsLocal(id)
            }
        }
    }

    private fun showAvailablePlatforms(extras: Bundle) = runBlocking {

        var hasAtLeastOnePlatofrm = false;
        val userPlatforms = RemoteDAO(requireActivity(), coroutineContext).getPiattaformeUser()

        val userPlatformList = root!!.findViewById<LinearLayout>(R.id.listaPiattaformeUser)
        val notUserPlatformList = root!!.findViewById<LinearLayout>(R.id.listaAltrePiattaforme)

        val netflixLogoPath = extras.getString("NetflixPath");
        val primevideoLogo = extras.getString("AmazonPath")
        val disneyplusLogo = extras.getString("DisneyPath")
        val raiplayLogo = extras.getString("RaiPath")
        val crunchyrollLogo = extras.getString("CrunchyrollPath")

        val userHasNetflix = addToAList(
            "Netflix",
            netflixLogoPath,
            userPlatforms,
            userPlatformList,
            notUserPlatformList
        );
        val userHasPrime = addToAList(
            "Amazon Prime Video",
            primevideoLogo,
            userPlatforms,
            userPlatformList,
            notUserPlatformList
        );

        val userHasDisney = addToAList(
            "Disney Plus",
            disneyplusLogo,
            userPlatforms,
            userPlatformList,
            notUserPlatformList
        );

        val userHasRai = addToAList(
            "Rai Play",
            raiplayLogo,
            userPlatforms,
            userPlatformList,
            notUserPlatformList
        );

        val userHasCrunchyRoll = addToAList(
            "Crunchyroll",
            crunchyrollLogo,
            userPlatforms,
            userPlatformList,
            notUserPlatformList
        );

        hasAtLeastOnePlatofrm =
            userHasNetflix || userHasPrime || userHasDisney || userHasRai || userHasCrunchyRoll;

        if (hasAtLeastOnePlatofrm) {
            root!!.findViewById<TextView>(R.id.alertNoPiattaforme).visibility = View.GONE;
        }

        if (notUserPlatformList.isEmpty()) {
            root!!.findViewById<TextView>(R.id.altrePiattaforme).visibility = View.GONE;
            notUserPlatformList.visibility = View.GONE;
        }
    }

    private fun addToAList(
        platform: String,
        platfromLogoPath: String?,
        userPlatforms: List<Piattaforme>,
        userPlatformList: LinearLayout,
        notUserPlatformList: LinearLayout,
    ): Boolean {
        val mediaIsOnPlatform = platfromLogoPath != null;
        if (!mediaIsOnPlatform) {
            return false;
        }
        val platformView = ImageView(requireActivity());
        Picasso.get().load(platfromLogoPath).into(platformView);

        val userHasPlatform = userPlatforms.find { it.nome == platform } != null;
        if (userHasPlatform) {
            userPlatformList.addView(platformView);
            return true;
        } else {
            val grigio = getARGB((ContextCompat.getColor(requireContext(), R.color.GrigioScuro)));
            platformView.setColorFilter(Color.argb(150, grigio[1], grigio[2], grigio[3]));
            notUserPlatformList.addView(platformView);
            return false;
        }
    }

    private fun getARGB(hex: Int): IntArray {
        val a = (hex and -0x1000000) shr 24
        val r = (hex and 0xFF0000) shr 16
        val g = (hex and 0xFF00) shr 8
        val b = (hex and 0xFF)
        return intArrayOf(a, r, g, b)
    }
}