package unitn.app.activities.profilo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unitn.app.activities.LiveDatas
import unitn.app.activities.auth.Login
import unitn.app.localdb.UserDatabase
import unitn.app.remotedb.RemoteDAO


class FragmentSettings(private val username: String) : Fragment() {
    constructor() : this("boh") {
    }

    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_settings, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameView = view.findViewById<EditText>(R.id.username)
        val buttonSaveUsername = view.findViewById<Button>(R.id.btnSaveUsername)
        val buttonLogOff = view.findViewById<Button>(R.id.buttonLogOff)
        val buttonDeleteAccount = view.findViewById<Button>(R.id.buttonDeleteAccount)

        //username
        usernameView.text = SpannableStringBuilder(username)

        //save button username
        setSaveUsernameButton(buttonSaveUsername, usernameView, view.context);

        //button log off
        setLogOffButton(buttonLogOff, view.context);

        //button delete account
        setDeleteAccount(buttonDeleteAccount, view.context);
    }

    private fun setSaveUsernameButton(buttonSave: Button, nameView: EditText, context: Context) {
        LiveDatas.liveColore.observe(viewLifecycleOwner) {
            LiveDatas.updateColorsOfButtons(listOf(buttonSave))
        }
        buttonSave.setOnClickListener {
            val newUsername = nameView.text.toString()
            lifecycleScope.launch {
                val remoteDao = RemoteDAO(context, coroutineContext);
                val result = remoteDao.updateUser(newUsername, context);
                if (!result) {
                    Toast.makeText(context, "Nome non disponibile", Toast.LENGTH_SHORT).show();
                } else {
                    requireActivity().findViewById<TextView>(R.id.titolo).text =
                        "Ciao, $newUsername!"
                }
            }
        }
    }

    private fun setDeleteAccount(button: Button, context: Context) {
        button.setOnClickListener {
            val builder = AlertDialog.Builder(context)

            with(builder)
            {
                setTitle("CANCELLA ACCOUNT")
                setMessage("Sei sicuro di voler cancellare l'account?\nTutti i dati andranno persi in maniera irreversibile")
                setPositiveButton(android.R.string.ok) { _, _ ->
                    runBlocking {
                        val localDao = Room.databaseBuilder(
                            context,
                            UserDatabase::class.java, "user-db"
                        ).fallbackToDestructiveMigration()
                            .build().userDao()
                        val userId = localDao.getUserId()!!;
                        localDao.deleteEvertyhing();
                        RemoteDAO.deleteUser(userId);
                        val intent = Intent(context, Login::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        activity?.finish();
                        startActivity(intent)
                    }
                }
                setNegativeButton(android.R.string.cancel, null)
                show()
            }
        }
    }

    private fun setLogOffButton(button: Button, context: Context) {
        button.setOnClickListener {
            runBlocking {
                Room.databaseBuilder(
                    context,
                    UserDatabase::class.java, "user-db"
                ).fallbackToDestructiveMigration()
                    .build().userDao().deleteEvertyhing()
            }
            val intent = Intent(context, Login::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity?.finish();
            startActivity(intent)
        }
    }
}