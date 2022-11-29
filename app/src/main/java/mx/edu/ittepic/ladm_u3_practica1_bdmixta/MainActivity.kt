package mx.edu.ittepic.ladm_u3_practica1_bdmixta

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Instant


class MainActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    val baseDatos = BaseDatos(this,"Tecnologico",null,1)
    var IDs = ArrayList<Int>()


    @RequiresApi(Build.VERSION_CODES.O) //Se requiere para obtener la fecha y hora del dispositivo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mostrarTodos()
        conectada()



       insertar.setOnClickListener {

           val dateTime = LocalDateTime.now()
               .format(DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss: a"))


           var alumno = baseDatos.writableDatabase
           var datos = ContentValues()

           if(nombre.text.toString()=="" || escuela.text.toString()=="" || telefono.text.toString()=="" || carrera1.text.toString()==""
               || carrera2.text.toString()=="" || correo.text.toString()==""){
               Toast.makeText(this,"COMPLETE TODOS LOS CAMPOS",Toast.LENGTH_LONG).show()
           }else{
           datos.put("NOMBRE",nombre.text.toString())
           datos.put("ESCUELAPROCEDENCIA",escuela.text.toString())
           datos.put("TELEFONO",telefono.text.toString().toInt())
           datos.put("CARRERA1",carrera1.text.toString())
           datos.put("CARRERA2",carrera2.text.toString())
           datos.put("CORREO",correo.text.toString())
           datos.put("FECHA",dateTime.toString())

           var resultado = alumno.insert("CANDIDATO","ID",datos)
           if(resultado ==-1L){
               AlertDialog.Builder(this).setTitle("ERROR")
                   .setMessage("NO SE PUDO GUARDAR").show()
           }else{
               Toast.makeText(this,"SE INSERTO CORRECTAMENTE",Toast.LENGTH_LONG)
                   .show()
               nombre.setText("")
               escuela.setText("")
               telefono.setText("")
               carrera1.setText("")
               carrera2.setText("")
               correo.setText("")
               mostrarTodos()

                }
           }

       }//INSERTAR


    }


    fun mostrarTodos() {
        var alumno = baseDatos.readableDatabase
        val lista = ArrayList<String>()


        IDs.clear()
        var resultado = alumno.query("CANDIDATO", arrayOf("*"), null, null, null, null, null)
        if (resultado.moveToFirst()) {
            do {
                val data = resultado.getString(1) + "\n" + resultado.getString(2) + " | " +
                        resultado.getInt(3)+  " | " + resultado.getString(4) + " | " + resultado.getString(5)+
                " | " + resultado.getString(6) +  " | " + resultado.getString(7)
                lista.add(data)
                IDs.add(resultado.getInt(0))
            } while (resultado.moveToNext())
        } else {
            lista.add("LA TABLA ESTA VACIA")
        }
        listaAlumnos.adapter = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, lista)

        listaAlumnos.setOnItemClickListener {adapterView, view, i, l ->
            val idSeleccionado = IDs.get(i)
            var nombre = lista.get(i)
            nombre = nombre.substring(0, nombre.indexOf("\n")).uppercase()

            AlertDialog.Builder(this)
                .setTitle("ATENCION")
                .setMessage("QUE DESEAS HACER CON: ${nombre}?")
                .setPositiveButton("NADA"){d,i->}
                .setNegativeButton("ELIMINAR"){d,i->
                    eliminar(idSeleccionado)
                }
                .setNeutralButton("ACTUALIZAR"){d,i->
                    actualizar(idSeleccionado)
                }
                .show()

        }

    }//MOSTRAR TODOS

    private fun actualizar(idSeleccionado: Int){
        val otraVentana = Intent(this,MainActivity2:: class.java)
        otraVentana.putExtra("idSeleccionado", idSeleccionado.toString())
        startActivity(otraVentana)

    }

    private fun eliminar(idSeleccionado: Int) {
        val resultado = baseDatos.writableDatabase.delete("CANDIDATO",
            "ID=?", arrayOf(idSeleccionado.toString()))

        if(resultado == 0){
            AlertDialog.Builder(this)
                .setMessage("ERROR NO SE BORRO")
                .show()
        }else{
            Toast.makeText(this,"SE BORRO CON EXITO", Toast.LENGTH_LONG).show()
            mostrarTodos()
        }

    }//ELIMINAR

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

            }//CONECTADA


    override fun onRestart(){
        super.onRestart()
        mostrarTodos()

    }//on restart



    }


