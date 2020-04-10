package ca.ulaval.ima.mp

import android.Manifest
import android.R.attr.bitmap
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.ui.profil.MonProfilFragment
import kotlinx.android.synthetic.main.fragment_inscription.view.*
import kotlinx.android.synthetic.main.new_eval_activity.*
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class NewEvalActivity : AppCompatActivity() {
    var identificationToken : String? = ""
    var restoId = 0
    private val PICK_FROM_GALLERY = 1
    private var apiHelper: ApiHelper = ApiHelper()
    private var addImg = false
    private lateinit var bitmap:Bitmap
    private var imgName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_eval_activity)
        val token = intent.getStringExtra("token")
        val getRestoId = intent.getLongExtra("restoId",0)

        restoId = getRestoId.toInt()
        identificationToken = token

        val galleryButton: AppCompatImageView = findViewById<AppCompatImageView>(R.id.addImg)
        //bouton d'ajout d'image
        galleryButton.setOnClickListener {

            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                , 0
            )
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            // Start the Intent
            startActivityForResult(galleryIntent, PICK_FROM_GALLERY)
            addImg = true
        }

        //bouton soumettre
        buttonSoumettreEval.setOnClickListener {
            //TODO : validation de formulaire
            val note = findViewById<RatingBar>(R.id.stars_layout)?.rating
            val commentaire = findViewById<EditText>(R.id.editTextCommentaire)?.text.toString()
            val photo = findViewById<AppCompatImageView>(R.id.addImg)
            submitReview(note?.toInt(),commentaire,photo)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

    }

    private fun submitReview(note: Int?, commentaire: String, photo: AppCompatImageView?) {

        //premier appel pour la note et le commentaire
        apiHelper.submitReview(restoId,
            note,
            commentaire,
            identificationToken,
            object : ApiHelper.HttpCallback {
                override fun onFailure(
                    response: Response?,
                    throwable: Throwable?
                ) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        val text: CharSequence = "connexion invalide"
                        val duration = Toast.LENGTH_LONG
                        val toast = Toast.makeText(this@NewEvalActivity, text, duration)
                        toast.show()
                    }
                }
                override fun onSuccess(response: Response?) {
                    try {
                        val jsonResponse = JSONObject(response!!.body()!!.string())
                        val jsonContent = jsonResponse.getJSONObject("content")
                        val reviewId = jsonContent.getString("id")
                        //si photo non-null on fait une deuxieme post
                        if(addImg){
                            submitImage(imgName, reviewId.toInt())
                        }else{
                            onBackPressed()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }
    private fun submitImage(imgName: String, reviewID: Int?) {

        //premier appel pour la note et le commentaire
        apiHelper.submitImage(imgName,
            reviewID,
            identificationToken,
            object : ApiHelper.HttpCallback {
                override fun onFailure(
                    response: Response?,
                    throwable: Throwable?
                ) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        val text: CharSequence = "envoie de l'image a échoué "
                        val duration = Toast.LENGTH_LONG
                        val toast = Toast.makeText(this@NewEvalActivity, text, duration)
                        toast.show()
                    }
                }
                override fun onSuccess(response: Response?) {
                    try {
                        val mainHandler = Handler(Looper.getMainLooper())
                        mainHandler.post {
                            val jsonResponse = JSONObject(response!!.body()!!.string())
                            val jsonContent = jsonResponse.getJSONObject("content")
                            val text: CharSequence = "Votre évaluation a été soumise avec succès"
                            val duration = Toast.LENGTH_LONG
                            val toast = Toast.makeText(this@NewEvalActivity, text, duration)
                            toast.show()
                        }
                        onBackPressed()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_FROM_GALLERY){
            val selectedImage:Uri  = data?.data!!

            val filePath: String  = getPath(selectedImage)

            imgName = filePath

            val galleryButton: AppCompatImageView = findViewById<AppCompatImageView>(R.id.addImg)
            galleryButton.setImageURI(data.data)
        }
    }

     fun getPath(uri :Uri ) : String {
        var projection = arrayOf<String>( MediaStore.MediaColumns.DATA)
         var cursor = managedQuery(uri, projection, null, null, null)
         var column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
         cursor.moveToFirst()
         var imagePath = cursor.getString(column_index)

         return cursor.getString(column_index)
     }
}
