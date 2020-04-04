package ca.ulaval.ima.mp.ui.profil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_connexion.view.*


class ConnexionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_connexion, container, false)

        root.textViewMinscrire.setOnClickListener {
            val newfragment: Fragment = InscriptionFragment()

            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.nav_host_fragment, newfragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        return root
    }
}