package unitn.app.activities.segnaComeVisto

import android.app.DatePickerDialog
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
import unitn.app.remotedb.RemoteDAO
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
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
        //extras from ../dettaglio o ../ratings
        val isNewRating = extras.getBoolean("isNewRating");
        val mediaID = extras.getInt("mediaID");
        val titoloFilm = extras.getString("titoloFilm");
        var dataVisione = extras.getString("dataVisione");
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
            dataVisioneView.text = formatToShowDate(today());
            dataVisione = dataVisioneView.text.toString();
        } else {
            dataVisioneView.text = formatToShowDate(dataVisione);
        }
        val splittedData = dataVisione.split(" ")
        val date: String;
        val time: String?;
        if (splittedData.size == 1) {
            date = splittedData[0]
            time = null;
        } else if (splittedData.size == 2) {
            date = splittedData[0];
            time = splittedData[1];
        } else {
            date = "Error";
            time = "Error"
        }

        val dataVisioneInitialValue = formatToSaveDate(date, time);

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
        if (isNewRating) {
            buttonConfirm.setOnClickListener {
                segnaComeVisto(
                    mediaID,
                    dataVisioneView.text.toString(),
                    ratingBar.rating,
                    ratingBar.numStars.toFloat(),
                    recensioneView.text.toString()
                );
                finish();
            }
        } else {
            buttonConfirm.text = "Conferma le modifiche";
            buttonConfirm.setOnClickListener {
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
            val timeToSave = formatToSaveDate(dataVisione, null);
            remoteDao.insertToCronologia(
                mediaID,
                timeToSave,
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
            val date = dataVisione.split(" ")[0];
            val time = vecchiaDataVisione.split(" ")[1];
            val timeToSave = formatToSaveDate(date, time);
            remoteDao.updateRating(
                mediaID,
                vecchiaDataVisione,
                timeToSave,
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

    private fun formatToShowDate(dataDaFromattare: String): String {
        val data = dataDaFromattare.split(" ")[0];
        return data.split("-").reversed().joinToString("/")
    }

    private fun formatToSaveDate(dataDaFormattare: String, tempoDaFormattare: String?): String {
        val time: String;
        if (tempoDaFormattare == null) {
            val sdfDate = SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            time = sdfDate.format(Date());
        } else {
            time = tempoDaFormattare;
        }
        val date = dataDaFormattare.replace("/", "-").split("-").reversed().joinToString("-");
        return "$date $time";
    }
}
