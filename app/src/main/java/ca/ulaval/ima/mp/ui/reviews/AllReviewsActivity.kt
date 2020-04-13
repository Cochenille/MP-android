package ca.ulaval.ima.mp.ui.reviews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.MainActivity
import ca.ulaval.ima.mp.NewEvalActivity
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.domain.Review


class AllReviewsActivity : AppCompatActivity() {
    private lateinit var recycledView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    var identificationToken: String? = ""
    var restoId : Long = 0
    val requestcodeGoConnect: Int = 0
    val requestcodeNewEval: Int = 1
    private lateinit var buttonBasDePage: Button
    private lateinit var textViewLaisserEval: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_resto_reviews)
        recycledView = findViewById(R.id.all_review_recycler_view)
        val restoId = intent.getLongExtra("restoID", 0)
        val token = intent.getStringExtra("token")
        this.restoId = restoId
        identificationToken = token
        layoutManager = LinearLayoutManager(this)
        recycledView.layoutManager = layoutManager
        val reviewViewModel: ReviewViewModel =
            ViewModelProviders.of(this).get(ReviewViewModel::class.java)
        reviewViewModel.setRestoId(restoId)

        val reviewPagedAdapter = ReviewPagedAdapter(this)

        reviewViewModel.reviewPagedList.observe(this,
            Observer<PagedList<Review>> { t -> reviewPagedAdapter.submitList(t) })

        recycledView.adapter = reviewPagedAdapter

        val reviewCount = intent.getLongExtra("reviewCount", 0)
        val reviewCountTextView = findViewById<TextView>(R.id.review_count_textview)
        reviewCountTextView.text = String.format("(%d)", reviewCount)

        //affiche le bon bouton en fonction de si l'usager est connecté ou non
        val buttonBack = findViewById<ImageView>(R.id.buttonBack)
        buttonBasDePage = findViewById<Button>(R.id.buttonConnexion)
        textViewLaisserEval = findViewById<TextView>(R.id.textViewConnexionLabel)
        textViewLaisserEval.visibility = View.VISIBLE

        buttonBack.setOnClickListener {
            onBackPressed()
        }
        setFooter(restoId)
    }

    private fun setFooter(restoId: Long) {
        if (identificationToken != null && identificationToken != "") {
            buttonBasDePage.text = "Laisser une évaluation"
            buttonBasDePage.setBackgroundResource(R.drawable.custom_rounded_button_black)
            buttonBasDePage.setOnClickListener {
                val intent = Intent(this, NewEvalActivity::class.java)
                intent.putExtra("token", identificationToken)
                intent.putExtra("restoId", restoId)
                startActivityForResult(intent, requestcodeNewEval)
            }
            textViewLaisserEval.visibility = View.INVISIBLE
        } else {
            buttonBasDePage.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("gotoConnexion", "true")
                startActivityForResult(intent, requestcodeGoConnect)
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == this.requestcodeGoConnect) {
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(this, NewEvalActivity::class.java)
                identificationToken = data?.getStringExtra("token")
                intent.putExtra("token", identificationToken)
                intent.putExtra("restoId", restoId)
                startActivityForResult(intent, requestcodeNewEval)
            }
        }
        if (requestCode == this.requestcodeNewEval) {
            if (resultCode == 0) {
                val buttonBasDePage = findViewById<Button>(R.id.buttonConnexion)
                val textViewLaisserEval = findViewById<TextView>(R.id.textViewConnexionLabel)
                buttonBasDePage.text = "Laisser une évaluation"
                buttonBasDePage.setBackgroundResource(R.drawable.custom_rounded_button_black)
                buttonBasDePage.setOnClickListener {

                    val intent = Intent(this, NewEvalActivity::class.java)
                    intent.putExtra("token", identificationToken)
                    intent.putExtra("restoId", restoId)
                    startActivityForResult(intent, requestcodeNewEval)
                }
                textViewLaisserEval.visibility = View.INVISIBLE
                setFooter(restoId.toLong())
            }
        }
    }

}