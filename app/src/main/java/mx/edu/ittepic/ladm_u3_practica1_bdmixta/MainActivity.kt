package mx.edu.ittepic.ladm_u3_practica1_bdmixta

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        conectada()
    }


    fun conectada() {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (!isConnected) {
                    Toast.makeText(
                        this,
                        "ERROR! No se pudo recuperar data desde NUBE",
                        Toast.LENGTH_LONG
                    ).show()

                }else{
                    Toast.makeText(this,"CONECTADA",Toast.LENGTH_LONG).show()
                }

            }
    }


