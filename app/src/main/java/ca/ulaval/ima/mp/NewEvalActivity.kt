package ca.ulaval.ima.mp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ca.ulaval.ima.mp.R
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.new_eval_activity.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class NewEvalActivity : AppCompatActivity() {
    var identificationToken : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_eval_activity)
        val token = intent.getStringExtra("token")

        identificationToken = token
    }


}
