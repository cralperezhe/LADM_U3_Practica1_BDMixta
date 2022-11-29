package mx.edu.ittepic.ladm_u3_practica1_bdmixta

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    val baseDatos = BaseDatos(this,"Tecnologico",null,1)
    var idSeleccionado = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        idSeleccionado = intent.extras!!.getString("idSeleccionado")!!

        val cursor = baseDatos.readableDatabase.query("CANDIDATO", arrayOf("*"),"ID=?", arrayOf(idSeleccionado),null,null,null)

        if(cursor.moveToFirst()){
            act_nombre.setText(cursor.getString(1))
            act_escuela.setText(cursor.getString(2))
            act_telefono.setText(cursor.getInt(3).toString())
            act_carrera1.setText(cursor.getString(4))
            act_carrera2.setText(cursor.getString(5))
            act_correo.setText(cursor.getString(6))

        }else{
            act_nombre.setText("No hay datos")
            act_escuela.setText("No hay datos")
            act_telefono.setText("No hay datos")
            act_carrera1.setText("No hay datos")
            act_carrera2.setText("No hay datos")
            act_correo.setText("No hay datos")
            act_nombre.isEnabled=false
            act_escuela.isEnabled=false
            act_telefono.isEnabled=false
            act_carrera1.isEnabled=false
            act_carrera2.isEnabled=false
            act_correo.isEnabled=false
            actualizar.isEnabled=false
        }

        actualizar.setOnClickListener {
            val datos = ContentValues()
            datos.put("NOMBRE",act_nombre.text.toString())
            datos.put("ESCUELAPROCEDENCIA",act_escuela.text.toString())
            datos.put("TELEFONO",act_telefono.text.toString().toInt())
            datos.put("CARRERA1",act_carrera1.text.toString())
            datos.put("CARRERA2",act_carrera2.text.toString())
            datos.put("CORREO",act_correo.text.toString())
            val res = baseDatos.writableDatabase.update("CANDIDATO",datos,"ID=?", arrayOf(idSeleccionado))
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

                actualizar.setText("SE ACTUALIZO")

            }

        } // ACTUALIZAR
        regresar.setOnClickListener { finish() }


    }// ON CREATE
}