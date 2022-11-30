package mx.edu.ittepic.ladm_u3_practica1_bdmixta

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    val baseDatos = BaseDatos(this,"Tecnologico",null,1)
    var IDs = ArrayList<Any?>()
    var nube = ""
    var datosFirebase: HashMap<String, Any>? = null

    fun conectada():Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return if (isConnected) {
            nube = "Firebase"
            Toast.makeText(this, "CONECTADO", Toast.LENGTH_LONG).show()
            true
        } else {
            nube = "SQLite"
            Toast.makeText(this, "DESCONECTADO", Toast.LENGTH_LONG).show()
            false
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O) //Se requiere para obtener la fecha y hora del dispositivo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        conectada()
        mostrarTodos(nube)

        var internet = NetworkConnection(applicationContext)
        internet.observe(this) { isConnected ->
            if (isConnected) {
                Toast.makeText(this, "CONECTADO", Toast.LENGTH_LONG).show()
                if(nube == "SQLite" && listaAlumnos.getItemAtPosition(0).toString() != "LA TABLA DE SQLITE ESTA VACIA" && conectada()){
                    AlertDialog.Builder(this)
                        .setTitle("IMPORTANTE")
                        .setMessage("Se detectó un cambio, ¿desea trasladar la información a la nube? $nube")
                        .setPositiveButton("Sí"){_, _ ->

                            var resultado = baseDatos.writableDatabase.rawQuery("SELECT * FROM CANDIDATO", null)
                            if(resultado!= null && resultado.count!= 0){
                                resultado.moveToFirst()
                                do{
                                    var datos = hashMapOf<String, Any>()
                                    datos["NOMBRE"] = resultado.getString(1)
                                    datos["ESCUELAPROCEDENCIA"] = resultado.getString(2)
                                    datos["TELEFONO"] = resultado.getString(3)
                                    datos["CARRERA1"] = resultado.getString(4)
                                    datos["CARRERA2"] = resultado.getString(5)
                                    datos["CORREO"] = resultado.getString(6)
                                    datos["FECHA"] = resultado.getString(7)
                                    db.collection("Candidatos")
                                        .document(resultado.getInt(0).toString())
                                        .set(datos as Any)
                                        .addOnSuccessListener {}
                                        .addOnFailureListener {
                                            Toast.makeText(this,it.message!!,Toast.LENGTH_LONG).show()
                                        }

                                    val eliminar = baseDatos.writableDatabase.delete("CANDIDATO","ID=?", arrayOf(resultado.getInt(0).toString()))

                                    if (eliminar == 0) {
                                        AlertDialog.Builder(this)
                                            .setMessage("ERROR NO SE BORRO")
                                            .show()
                                    } else mostrarTodos(nube)

                                }while(resultado.moveToNext())
                            }

                            mostrarTodos(nube)
                            nube = "Firebase"

                            AlertDialog.Builder(this)
                                .setTitle("AVISO")
                                .setMessage("Se trasladó toda la información a la nube correctamente!")
                                .setPositiveButton("Ok"){_,_ ->}
                                .show()
                        }
                        .setNegativeButton("No"){_,_ ->
                            mostrarTodos(nube)
                        }
                        .show()
                }else{
                    mostrarTodos(nube)
                    nube = "Firebase"
                }

            } else {
                nube = "SQLite"
                Toast.makeText(this, "DESCONECTADO", Toast.LENGTH_LONG).show()
                mostrarTodos(nube)
            }


        }

        insertar.setOnClickListener {
           val dateTime = LocalDateTime.now()
               .format(DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss: a"))
           var alumno = baseDatos.writableDatabase
           var datosSQLite = ContentValues()


           if(nombre.text.toString()=="" || escuela.text.toString()=="" || telefono.text.toString()=="" || carrera1.text.toString()==""
               || carrera2.text.toString()=="" || correo.text.toString()==""){
               Toast.makeText(this,"COMPLETE TODOS LOS CAMPOS",Toast.LENGTH_LONG).show()
           }else{
                var resultado = 0L
                if(nube == "Firebase"){
                    datosFirebase = hashMapOf("NOMBRE" to nombre.text.toString(), "ESCUELAPROCEDENCIA" to escuela.text.toString(),
                        "TELEFONO" to telefono.text.toString(), "CARRERA1" to carrera1.text.toString(),
                        "CARRERA2" to carrera2.text.toString(), "CORREO" to correo.text.toString(),
                        "FECHA" to dateTime)
                    db.collection("Candidatos")
                        .add(datosFirebase!!)
                        .addOnSuccessListener {
                            resultado = 1
                        }
                }else if(nube == "SQLite"){
                    datosSQLite.put("NOMBRE",nombre.text.toString())
                    datosSQLite.put("ESCUELAPROCEDENCIA",escuela.text.toString())
                    datosSQLite.put("TELEFONO",telefono.text.toString())
                    datosSQLite.put("CARRERA1",carrera1.text.toString())
                    datosSQLite.put("CARRERA2",carrera2.text.toString())
                    datosSQLite.put("CORREO",correo.text.toString())
                    datosSQLite.put("FECHA",dateTime.toString())
                    resultado = alumno.insert("CANDIDATO","ID",datosSQLite)
                }
                if(resultado ==-1L){
                    AlertDialog.Builder(this).setTitle("ERROR")
                        .setMessage("NO SE PUDO GUARDAR")
                        .setPositiveButton("Ok"){_,_->}
                        .show()
                }else{
                    Toast.makeText(this,"SE INSERTO CORRECTAMENTE",Toast.LENGTH_LONG)
                        .show()
                    nombre.setText("")
                    escuela.setText("")
                    telefono.setText("")
                    carrera1.setText("")
                    carrera2.setText("")
                    correo.setText("")
                    mostrarTodos(nube)
                }
           }//ELSE VALIDACION DE CAMPOS

       }//INSERTAR SET ON CLICK LISTENER


    }


    fun mostrarTodos(nube: String) {
        var lista = ArrayList<String>()
        IDs.clear()
        if(nube == "SQLite") {
            var alumno = baseDatos.readableDatabase
            var resultado = alumno.query("CANDIDATO", arrayOf("*"), null, null, null, null, null)
            if (resultado.moveToFirst()) {
                do {
                    val data = resultado.getString(1) + "\n" + resultado.getString(2) + " | " + resultado.getString(3) + " | " +
                            resultado.getString(4) + " | " + resultado.getString(5) + " | " + resultado.getString(6) + " | " + resultado.getString(7)
                    lista.add(data)
                    IDs.add(resultado.getInt(0))
                } while (resultado.moveToNext())
            } else {
                lista.add("LA TABLA DE SQLITE ESTA VACIA")
            }

        }else if(nube == "Firebase"){
            db.collection("Candidatos")
                .addSnapshotListener{ value, error ->
                    if(error != null){
                        AlertDialog.Builder(this)
                            .setMessage("No se pudo realizar la consulta")
                            .setPositiveButton("Ok"){_,_ ->}
                            .show()
                    }//IF ERROR
                    for (documento in value!!){
                        val cadena = documento.getString("NOMBRE") + "\n" + documento.getString("ESCUELAPROCEDENCIA")  + " | " +
                                documento.getString("TELEFONO") + " | " +  documento.getString("CARRERA1") + " | " +
                                documento.getString("CARRERA2") + " | " + documento.getString("CORREO") + " | " + documento.getString("FECHA")
                        lista.add(cadena)
                        Log.d("TEXTO","DOCUMENTO: $cadena")
                        IDs.add(documento.id)

                        listaAlumnos.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_list_item_1, lista
                        )
                    }//FOR
                }//ADD SNAPSHOTLISTENER
        }// IF NUBE
        listaAlumnos.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, lista
        )

        listaAlumnos.setOnItemClickListener { _, _, i, _ ->
            val idSeleccionado = IDs[i]
            var nombre = lista[i]
            nombre = nombre.substring(0, nombre.indexOf("\n")).uppercase()

            AlertDialog.Builder(this)
                .setTitle("ATENCION")
                .setMessage("QUE DESEAS HACER CON: ${nombre}?")
                .setPositiveButton("NADA") { _, _ -> }
                .setNegativeButton("ELIMINAR") { _, _ ->
                    eliminar(idSeleccionado.toString())
                }
                .setNeutralButton("ACTUALIZAR") { _, _ ->
                    actualizar(idSeleccionado.toString())
                }
                .show()

        }

    }//MOSTRAR TODOS

    private fun actualizar(idSeleccionado: String){
        val otraVentana = Intent(this,MainActivity2:: class.java)
        otraVentana.putExtra("idSeleccionado", idSeleccionado)
        otraVentana.putExtra("nube", nube)
        startActivity(otraVentana)
    }

    private fun eliminar(idSeleccionado: String) {
        if(nube == "SQLite") {
            val resultado = baseDatos.writableDatabase.delete(
                "CANDIDATO",
                "ID=?", arrayOf(idSeleccionado)
            )

            if (resultado == 0) {
                AlertDialog.Builder(this)
                    .setMessage("ERROR NO SE BORRO")
                    .show()
            } else {
                Toast.makeText(this, "Se elimino correctamente!", Toast.LENGTH_LONG).show()
                mostrarTodos(nube)
            }
        }else if(nube == "Firebase"){ //ELSE IF NUBE
            db.collection("Candidatos")
                .document(idSeleccionado)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this,"Se elimino correctamente!", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener{
                    AlertDialog.Builder(this)
                        .setMessage(it.message!!)
                        .setPositiveButton("Ok"){_,_ ->}
                        .show()
                }
        }//IF NUBE
        mostrarTodos(nube)

    }//ELIMINAR

    override fun onRestart(){
        super.onRestart()
        mostrarTodos(nube)
    }//on restart
}


