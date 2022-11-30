package mx.edu.ittepic.ladm_u3_practica1_bdmixta

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    val baseDatos = BaseDatos(this,"Tecnologico",null,1)
    var idSeleccionado = ""
    var nube = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        nube = intent.extras!!.getString("nube")!!
        idSeleccionado = intent.extras!!.getString("idSeleccionado")!!

        if (nube == "SQLite") {
            val cursor = baseDatos.readableDatabase.query(
                "CANDIDATO",
                arrayOf("*"),
                "ID=?",
                arrayOf(idSeleccionado),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                act_nombre.setText(cursor.getString(1))
                act_escuela.setText(cursor.getString(2))
                act_telefono.setText(cursor.getInt(3).toString())
                act_carrera1.setText(cursor.getString(4))
                act_carrera2.setText(cursor.getString(5))
                act_correo.setText(cursor.getString(6))

            } else {
                act_nombre.setText("No hay datos")
                act_escuela.setText("No hay datos")
                act_telefono.setText("No hay datos")
                act_carrera1.setText("No hay datos")
                act_carrera2.setText("No hay datos")
                act_correo.setText("No hay datos")
                act_nombre.isEnabled = false
                act_escuela.isEnabled = false
                act_telefono.isEnabled = false
                act_carrera1.isEnabled = false
                act_carrera2.isEnabled = false
                act_correo.isEnabled = false
                actualizar.isEnabled = false
            }
        }else if(nube == "Firebase"){
            FirebaseFirestore.getInstance()
                .collection("Candidatos")
                .document(idSeleccionado)
                .get()
                .addOnSuccessListener {
                    act_nombre.setText(it.getString("NOMBRE"))
                    act_escuela.setText(it.getString("ESCUELAPROCEDENCIA"))
                    act_telefono.setText(it.getString("TELEFONO"))
                    act_carrera1.setText(it.getString("CARRERA1"))
                    act_carrera2.setText(it.getString("CARRERA2"))
                    act_correo.setText(it.getString("CORREO"))
                }
        }

        actualizar.setOnClickListener {
            var res: Int? = null
            if(nube == "SQLite") {
                val datos = ContentValues()
                datos.put("NOMBRE", act_nombre.text.toString())
                datos.put("ESCUELAPROCEDENCIA", act_escuela.text.toString())
                datos.put("TELEFONO", act_telefono.text.toString().toInt())
                datos.put("CARRERA1", act_carrera1.text.toString())
                datos.put("CARRERA2", act_carrera2.text.toString())
                datos.put("CORREO", act_correo.text.toString())
                res = baseDatos.writableDatabase.update(
                    "CANDIDATO",
                    datos,
                    "ID=?",
                    arrayOf(idSeleccionado)
                )
            }else if(nube == "Firebase"){
                FirebaseFirestore.getInstance()
                    .collection("Candidatos")
                    .document(idSeleccionado)
                    .update("NOMBRE", act_nombre.text.toString(),
                        "ESCUELAPROCEDENCIA", act_escuela.text.toString(),
                        "TELEFONO", act_telefono.text.toString(),
                        "CARRERA1", act_carrera1.text.toString(),
                        "CARRERA2", act_carrera2.text.toString(),
                        "CORREO", act_correo.text.toString())
                    .addOnSuccessListener {
                        Toast.makeText(this,"Se actualizo con exito", Toast.LENGTH_LONG)
                            .show()
                    }
                    .addOnFailureListener{
                        AlertDialog.Builder(this)
                            .setMessage(it.message!!)
                            .show()
                    }
            }
            if(res==0){
                AlertDialog.Builder(this)
                    .setMessage("NO SE PUDO ACTUALIZAR")
                    .show()

            }else{
                Toast.makeText(this,"EXITO!", Toast.LENGTH_LONG)
                    .show()

                act_nombre.isEnabled=false
                act_escuela.isEnabled=false
                act_telefono.isEnabled=false
                act_carrera1.isEnabled=false
                act_carrera2.isEnabled=false
                act_correo.isEnabled=false
                actualizar.isEnabled=false

                actualizar.text = "SE ACTUALIZO"

            }

        } // ACTUALIZAR
        regresar.setOnClickListener { finish() }


    }// ON CREATE
}