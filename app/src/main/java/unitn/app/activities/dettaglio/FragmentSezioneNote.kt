package unitn.app.activities.dettaglio

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.activities.ricerca.hideKeyboard
import unitn.app.remotedb.RemoteDAO

class FragmentSezioneNote(private val note: String?, private val mediaId: Int) : Fragment() {
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
        super.onViewCreated(view, savedInstanceState);

        val noteView = view.findViewById<EditText>(R.id.note);
        val btnSave = view.findViewById<Button>(R.id.buttonSaveNote);

        if(note != null) {
            noteView.text = SpannableStringBuilder(note);
        }

        LiveDatas.updateColorsOfButtons(listOf(btnSave));
        btnSave.setOnClickListener {
            lifecycleScope.launch {
                val remoteDao = RemoteDAO(
                    requireContext(),
                    coroutineContext
                );
                remoteDao.updateNote(mediaId, noteView.text.toString())
                noteView.hideKeyboard();
                Toast.makeText(requireContext(), "Nota salvata correttamente", Toast.LENGTH_SHORT).show();
            }
        }
        noteView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                noteView.hideKeyboard();
            }
        }
    }

}