package unitn.app.activities.segnaComeVisto

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test.R
import kotlinx.coroutines.runBlocking
import unitn.app.activities.LiveDatas
import unitn.app.activities.homepage.HomePage
import unitn.app.remotedb.RemoteDAO
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt


class SegnaComeVisto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_segna_come_visto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val extras = intent.extras;
        if (extras == null) {
            System.err.println("Bundle is null");
            return;
        }
        //extras from ../dettaglio
        val mediaID = extras.getInt("mediaID");
        val titoloFilm = extras.getString("titoloFilm");
        val dataVisione = extras.getString("dataVisione");
        val rating = extras.getFloat("rating");
        val maxRating = extras.getFloat("maxRating");
        val recensione = extras.getString("recensione");

        val titoloFilmView = findViewById<TextView>(R.id.titoloFilm);
        val ratingBar = findViewById<RatingBar>(R.id.rating);
        val dataVisioneView = findViewById<TextView>(R.id.data_visione);
        val recensioneView = findViewById<EditText>(R.id.recensione);
        val buttonConfirm = findViewById<Button>(R.id.buttonSeen);

        //titolo
        setTitleProperties(titoloFilmView, titoloFilm);

        //data
        if (dataVisione == null) {
            dataVisioneView.text = today().replace("-", "/");
        } else {
            dataVisioneView.text = dataVisione.split("-").reversed().joinToString("/");
        }
        val dataVisioneInitialValue =
            dataVisioneView.text.toString().split("/").reversed().joinToString("-");

        dataVisioneView.setOnClickListener { showDatePicker(dataVisioneView) };

        //rating
        if (maxRating != 0.0F) {
            ratingBar.rating = rating;
            ratingBar.numStars = maxRating.roundToInt();
        }
        //recensione
        recensioneView.setText(recensione ?: "")
        recensioneView.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE

        //button to confirm
        LiveDatas.updateColorsOfButtons(listOf(buttonConfirm))
        buttonConfirm.setOnClickListener {
            if (extras.getBoolean("isNewRating")) {
                segnaComeVisto(
                    mediaID,
                    dataVisioneView.text.toString(),
                    ratingBar.rating,
                    ratingBar.numStars.toFloat(),
                    recensioneView.text.toString()
                );

                finishAffinity();
                val intent = Intent(this, HomePage::class.java);
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
                startActivity(intent);
                finish();
            } else {
                updateRating(
                    mediaID,
                    dataVisioneInitialValue,
                    dataVisioneView.text.toString(),
                    ratingBar.rating,
                    ratingBar.numStars.toFloat(),
                    recensioneView.text.toString()
                );
                finish();
            }
        }
    }

    private fun segnaComeVisto(
        mediaID: Int,
        dataVisione: String,
        rating: Float,
        maxRating: Float,
        recensione: String,
    ) {
        runBlocking {
            val remoteDao = RemoteDAO(
                applicationContext,
                coroutineContext
            );
            remoteDao.insertToCronologia(
                mediaID,
                dataVisione.replace("/", "-").split("-").reversed().joinToString("-"),
                rating,
                maxRating,
                recensione
            );
            LiveDatas.setIdToRemove(mediaID);
        }
    }

    private fun updateRating(
        mediaID: Int,
        vecchiaDataVisione: String,
        dataVisione: String,
        rating: Float,
        maxRating: Float,
        recensione: String,
    ) {
        runBlocking {
            val remoteDao = RemoteDAO(
                applicationContext,
                coroutineContext
            );
            remoteDao.updateRating(
                mediaID,
                vecchiaDataVisione,
                dataVisione.replace("/", "-").split("-").reversed().joinToString("-"),
                rating,
                maxRating,
                recensione
            );
        }
    }

    private fun showDatePicker(dataVisioneView: TextView) {
        val calendar = Calendar.getInstance();
        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this, { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time);
                dataVisioneView.text = formattedDate;
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }

    private fun today(): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    private fun setTitleProperties(titoloFilm: TextView, titolo: String?) {
        titoloFilm.text = titolo
        titoloFilm.ellipsize = TextUtils.TruncateAt.MARQUEE;
        titoloFilm.marqueeRepeatLimit = -1;
        titoloFilm.isSingleLine = true;
        titoloFilm.isSelected = true;
    }
}
