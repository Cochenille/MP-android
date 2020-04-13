package ca.ulaval.ima.mp

import android.Manifest
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng


class MainActivity : AppCompatActivity() {
    var identificationToken = ""
    var gottagoback = false
    lateinit var currentPosition: LatLng
    var hasMoved = false
    var distanceMax = 10000
    lateinit var navController:NavController
    var clientId = "STO4WED2NTDDxjLs8ODios5M15HwsrRlydsMa1t0"
    var clientSecret = "YOVWGpjSnHd5AYDxGBR2CIB09ZYM1OPJGnH3ijkKwrUMVvwLprUmLf6fxku06ClUKTAEl5AeZN36V9QYBYvTtrLMrtUtXVuXOGWleQGYyApC2a469l36TdlXFqAG1tpK"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)


        navController = findNavController(R.id.nav_host_fragment)


        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        , 0)

        val gotoConnexion = intent.getStringExtra("gotoConnexion")

        if(gotoConnexion != null){
            gottagoback = true
            navController.navigate(R.id.navigation_profil)
        }

        val identificationToken = intent.getStringExtra("token")

        if(identificationToken != null){
            this.identificationToken = identificationToken
        }

        navView.setupWithNavController(navController)
    }
    fun userIsLogged(): Boolean {
        if(identificationToken == ""){
            return false
        }
        return true
    }
}
