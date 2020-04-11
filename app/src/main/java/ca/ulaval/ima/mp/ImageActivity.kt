package ca.ulaval.ima.mp


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.image_activity.*



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_activity)
        backButtonImg.setOnClickListener {
            onBackPressed()
        }
        val image = intent.getStringExtra("image")
        Picasso.get().load(image).fit().into(imageToShow)
    }
}
