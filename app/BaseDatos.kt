import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
): SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE CANDIDATO (ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE VARCHAR(400), ESCUELAPROCEDENCIA VARCHAR(300), TELEFONO VARCHAR(20),CARRERA1 VARCHAR(200), CARRERA2 VARCHAR(200),CORREO VARCHAR(300),FECHA DATE, HORA DATETIME)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}