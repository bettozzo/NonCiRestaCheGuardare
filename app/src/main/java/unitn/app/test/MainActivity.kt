package unitn.app.test

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test.R
import android.view.View as View1

data class GridViewModal(
    // we are creating a modal class with 2 member
    // one is course name as string and
    // other course img as int.
    val courseName: String,
    val courseImg: Int
)


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val goToSearchButton = findViewById<Button>(R.id.goToSearchMediaButton)

        goToSearchButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchForMedia::class.java)
            startActivity(intent)
        }

        var courseList: List<GridViewModal> = ArrayList<GridViewModal>()

        // on below line we are adding data to
        // our course list with image and course name.
        courseList = courseList + GridViewModal("C++", R.drawable.ic_launcher_background)
        courseList = courseList + GridViewModal("Java", R.drawable.ic_launcher_background)
        courseList = courseList + GridViewModal("asd", R.drawable.ic_launcher_background)
        courseList = courseList + GridViewModal("ing", R.drawable.ic_launcher_background)
        courseList = courseList + GridViewModal("rnn", R.drawable.ic_launcher_background)
        courseList = courseList + GridViewModal("ngl", R.drawable.ic_launcher_background)
        val courseAdapter = GridRVAdapter(courseList = courseList, this@MainActivity)

        val courseGRV: GridView = findViewById(R.id.GridView)
        // on below line we are setting adapter to our grid view.
        courseGRV.adapter = courseAdapter
        courseGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // inside on click method we are simply displaying
            // a toast message with course name.
            Toast.makeText(
                applicationContext, courseList[position].courseName + " selected",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}

internal class GridRVAdapter(
    // on below line we are creating two 
    // variables for course list and context
    private val courseList: List<GridViewModal>,
    private val context: Context
) :
    BaseAdapter() {
    // in base adapter class we are creating variables 
    // for layout inflater, course image view and course text view.
    private var layoutInflater: LayoutInflater? = null
    private lateinit var courseTV: TextView
    private lateinit var courseIV: ImageView

    // below method is use to return the count of course list
    override fun getCount(): Int {
        return courseList.size
    }

    // below function is use to return the item of grid view.
    override fun getItem(position: Int): Any? {
        return null
    }

    // below function is use to return item id of grid view.
    override fun getItemId(position: Int): Long {
        return 0
    }

    // in below function we are getting individual item of grid view.
    override fun getView(position: Int, convertView: View1?, parent: ViewGroup?): View1 {
        var convertView = convertView
        // on blow line we are checking if layout inflater 
        // is null, if it is null we are initializing it.
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        // on the below line we are checking if convert view is null. 
        // If it is null we are initializing it.
        if (convertView == null) {
            // on below line we are passing the layout file
            // which we have to inflate for each item of grid view.
            convertView = layoutInflater!!.inflate(R.layout.gridview_item, null)
        }
        // on below line we are initializing our course image view 
        // and course text view with their ids.
        courseIV = convertView!!.findViewById(R.id.idIVCourse)
        courseTV = convertView.findViewById(R.id.idTVCourse)
        // on below line we are setting image for our course image view.
        courseIV.setImageResource(courseList.get(position).courseImg)
        // on below line we are setting text in our course text view.
        courseTV.text = courseList.get(position).courseName
        // at last we are returning our convert view.
        return convertView
    }
}