package ca.ulaval.ima.mp.ui.profil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.MainActivity
import ca.ulaval.ima.mp.R
import kotlinx.android.synthetic.main.fragment_connexion.view.*
import kotlinx.android.synthetic.main.fragment_inscription.view.*


class InscriptionFragment : Fragment() {

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
            //TODO: requête au serveur pour obtenir le token d'authentification et créé le compte
            val activity = context as MainActivity
            activity.identificationToken = "blabla"
            val newfragment: Fragment = MonProfilFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.nav_host_fragment, newfragment)
            transaction?.commit()
        }
        return root
    }
}