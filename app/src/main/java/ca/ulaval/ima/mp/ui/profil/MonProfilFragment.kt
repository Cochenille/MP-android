package ca.ulaval.ima.mp.ui.profil

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.ApiHelper
import ca.ulaval.ima.mp.MainActivity
import ca.ulaval.ima.mp.R
import kotlinx.android.synthetic.main.fragment_monprofil.view.*
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject


class MonProfilFragment : Fragment() {
    private var apiHelper: ApiHelper = ApiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_monprofil, container, false)

        root.buttonDeconnexion.setOnClickListener {
            val activity = context as MainActivity
            activity.identificationToken = ""
            val newfragment: Fragment = ConnexionFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.nav_host_fragment, newfragment)
            transaction?.commit()
        }
        getUserInfo(root)
        getUserReview(root)
        return root
    }
    private fun getUserInfo(root: View?) {
        val acc = activity as MainActivity?
        apiHelper.getUserInfo(acc?.identificationToken,
            object : ApiHelper.HttpCallback {
                override fun onFailure(
                    response: Response?,
                    throwable: Throwable?
                ) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        acc?.identificationToken = ""
                        val newfragment: Fragment = ConnexionFragment()
                        val transaction = fragmentManager?.beginTransaction()
                        transaction?.replace(R.id.nav_host_fragment, newfragment)
                        transaction?.commit()
                    }
                }
                override fun onSuccess(response: Response?) {
                    try {
                        val jsonResponse = JSONObject(response!!.body()!!.string())
                        val jsonContent = jsonResponse.getJSONObject("content")
                        val prenom = jsonContent.getString("first_name")
                        val nom = jsonContent.getString("last_name")
                        val email = jsonContent.getString("email")
                        val mainHandler =
                            Handler(Looper.getMainLooper())
                        mainHandler.post {
                            root?.findViewById<TextView>(R.id.textViewCourriel)?.text = email
                            root?.findViewById<TextView>(R.id.textViewNom)?.text = prenom + " "+ nom
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }
    private fun getUserReview(root: View?) {
        val acc = activity as MainActivity?
        apiHelper.getUserReview(1,9999,acc?.identificationToken,
            object : ApiHelper.HttpCallback {
                override fun onFailure(
                    response: Response?,
                    throwable: Throwable?
                ) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        acc?.identificationToken = ""
                        val newfragment: Fragment = ConnexionFragment()
                        val transaction = fragmentManager?.beginTransaction()
                        transaction?.replace(R.id.nav_host_fragment, newfragment)
                        transaction?.commit()
                    }
                }
                override fun onSuccess(response: Response?) {
                    try {
                        val jsonResponse = JSONObject(response!!.body()!!.string())
                        val jsonContent = jsonResponse.getJSONObject("content")
                        val nbrDeReview = jsonContent.getString("count")
                        val mainHandler =
                            Handler(Looper.getMainLooper())
                        mainHandler.post {
                            root?.findViewById<TextView>(R.id.textViewEval)?.text = nbrDeReview.toString()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }
}