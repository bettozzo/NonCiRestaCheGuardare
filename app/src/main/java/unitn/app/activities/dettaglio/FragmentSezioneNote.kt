package unitn.app.activities.dettaglio

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.test.R

class FragmentSezioneNote() : Fragment() {
    private var root: View? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_sezione_note, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //note
        val note = view.findViewById<EditText>(R.id.note);
        note.text = SpannableStringBuilder("Non funziona ancora questa funzionalità. Riprovare più tardi. Ovvero quando avrò cambiato anche il database.");
    }
}