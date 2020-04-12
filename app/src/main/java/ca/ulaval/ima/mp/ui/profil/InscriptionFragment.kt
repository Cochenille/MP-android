package ca.ulaval.ima.mp.ui.profil

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.ApiHelper
import ca.ulaval.ima.mp.MainActivity
import ca.ulaval.ima.mp.R
import kotlinx.android.synthetic.main.fragment_inscription.view.*
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject


class InscriptionFragment : Fragment() {
    private var apiHelper: ApiHelper = ApiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_inscription, container, false)

        root.textViewMeConnecter.setOnClickListener {
            val newfragment: Fragment = ConnexionFragment()

            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.nav_host_fragment, newfragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        root.buttonInscription.setOnClickListener {
            val prenom = root?.findViewById<EditText>(R.id.editTextPrenom)?.text.toString()
            val nom = root?.findViewById<EditText>(R.id.editTextNom)?.text.toString()
            val email = root?.findViewById<EditText>(R.id.editTextCourriel)?.text.toString()
            val password = root?.findViewById<EditText>(R.id.editTextPassword)?.text.toString()
            if(!prenom.equals("") && !nom.equals("") && !email.equals("") && !password.equals("")){
                createUser(prenom,nom,email,password)
            }
            else{
                val text: CharSequence = "Remplissez tous les champs, votre mot de passe doit contenir au moins 8 caractères et un chiffre"
                val duration = Toast.LENGTH_LONG
                val toast = Toast.makeText(activity, text, duration)
                toast.show()
            }
        }
        return root
    }
    private fun createUser(prenom: String, nom: String, courriel:String, motDePasse:String) {
        val acc = activity as MainActivity?
        apiHelper.createUser(nom,
            prenom,
            courriel,
            motDePasse,
            acc?.clientId,
            acc?.clientSecret,
            object : ApiHelper.HttpCallback {
                override fun onFailure(
                    response: Response?,
                    throwable: Throwable?
                ) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        val text: CharSequence = "connexion invalide"
                        val duration = Toast.LENGTH_LONG
                        val toast = Toast.makeText(acc, text, duration)
                        toast.show()
                    }
                }
                override fun onSuccess(response: Response?) {
                    try {
                        val jsonResponse = JSONObject(response!!.body()!!.string())
                        val jsonContent = jsonResponse.getJSONObject("content")
                        val token = jsonContent.getString("access_token")

                        val mainHandler =
                            Handler(Looper.getMainLooper())
                        mainHandler.post {
                            val activity = context as MainActivity
                            activity.identificationToken = token
                            val newfragment: Fragment = MonProfilFragment()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.replace(R.id.nav_host_fragment, newfragment)
                            transaction?.commit()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }
}