package ca.ulaval.ima.mp.ui.profil

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.ApiHelper
import ca.ulaval.ima.mp.MainActivity
import ca.ulaval.ima.mp.R
import kotlinx.android.synthetic.main.fragment_connexion.view.*
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject


class ConnexionFragment : Fragment() {

    private var apiHelper: ApiHelper = ApiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_connexion, container, false)
        val activity = context as MainActivity

        root.textViewMinscrire.setOnClickListener {
            val newfragment: Fragment = InscriptionFragment()

            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.nav_host_fragment, newfragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        root.buttonConnexion.setOnClickListener {
            login(root.editTextCourriel.text.toString(), root.editTextPassword.text.toString(), activity.clientId, activity.clientSecret)
        }
        //si l'usager est déjà connecté on passe direct à son profil
        if(activity.userIsLogged()) {
            val newfragment: Fragment = MonProfilFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.nav_host_fragment, newfragment)
            transaction?.commit()
        }
        return root
    }

    private fun login(username: String?, password: String?, clientId: String?, clientSecret: String?) {
        val acc = activity as MainActivity?
        apiHelper.login(
            username,
            password,
            clientId,
            clientSecret,
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
                        acc?.identificationToken = jsonContent.getString("access_token")
                        val newfragment: Fragment = MonProfilFragment()
                        val transaction = fragmentManager?.beginTransaction()
                        transaction?.replace(R.id.nav_host_fragment, newfragment)
                        transaction?.commit()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }
}