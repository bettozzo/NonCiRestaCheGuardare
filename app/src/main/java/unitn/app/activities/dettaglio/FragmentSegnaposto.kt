package unitn.app.activities.dettaglio

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.test.R
import unitn.app.activities.ricerca.hideKeyboard

class FragmentSegnaposto : Fragment() {
    private var root: View? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_segnaposto, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stagioneText = view.findViewById<EditText>(R.id.numeroStagione)
        val episodioText = view.findViewById<EditText>(R.id.numeroEpisodio)
        listenForEnterToHideKeyboard(episodioText);
        hideKeyboardIfFocusChange(episodioText);
        hideKeyboardIfFocusChange(stagioneText);

        val btnIncrementEpNumber = view.findViewById<ImageButton>(R.id.btnAumenta)
        btnIncrementEpNumber.setOnClickListener {
            episodioText.text =
                SpannableStringBuilder((episodioText.text.toString().toInt() + 1).toString())
        }

        stagioneText.doOnTextChanged { text, start, before, count ->
            episodioText.text = SpannableStringBuilder("1");
        }

    }

    private fun hideKeyboardIfFocusChange(editTextView: EditText) {
        editTextView.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                editTextView.hideKeyboard();
            }
        }
    }

    private fun listenForEnterToHideKeyboard(editTextView: EditText) {
        editTextView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                editTextView.hideKeyboard();
                return@setOnKeyListener true;
            }
            return@setOnKeyListener false;
        }
        editTextView.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                editTextView.hideKeyboard();
            }
        }
    }
}