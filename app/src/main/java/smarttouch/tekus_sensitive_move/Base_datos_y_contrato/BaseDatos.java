package smarttouch.tekus_sensitive_move.Base_datos_y_contrato;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by ASUS on 17/01/2017.
 */

public class BaseDatos extends SQLiteOpenHelper {

    private static final String name_bd = "Tekus4";
    private static final int version = 1;

    public BaseDatos(Context context) {
        super(context, name_bd, null, version);
    }

    public interface Tabla {
        String ALARMAS = "alarmas";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CrearTablaAlarmas(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        try {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Tabla.ALARMAS);

        } catch (SQLiteException e) {
            // Manejo de excepciones
        }
        onCreate(sqLiteDatabase);
    }


    private void CrearTablaAlarmas(SQLiteDatabase sqLiteDatabase) {

        //  creamos la tabla ALARMAS
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + Tabla.ALARMAS + "("
                        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Contrato.Tekus_Alarmas.NOTIFICATIONID + " TEXT NOT NULL,"
                        + Contrato.Tekus_Alarmas.DATE + " TEXT NOT NULL,"
                        + Contrato.Tekus_Alarmas.DURATION + " TEXT NOT NULL,"
                        + Contrato.Tekus_Alarmas.INFORMACION + " TEXT NOT NULL)");

        //Registroas iniciales
        ContentValues values = new ContentValues();
        values.put(Contrato.Tekus_Alarmas.NOTIFICATIONID,"1");
        values.put(Contrato.Tekus_Alarmas.DATE,"2017-01-17 15:40");
        values.put(Contrato.Tekus_Alarmas.DURATION,"5 seg");
        values.put(Contrato.Tekus_Alarmas.INFORMACION,"no hay informacion");
        sqLiteDatabase.insertOrThrow(Tabla.ALARMAS,null,values);

        //Registroas iniciales

        values.put(Contrato.Tekus_Alarmas.NOTIFICATIONID,"1");
        values.put(Contrato.Tekus_Alarmas.DATE,"2017-01-17 15:40");
        values.put(Contrato.Tekus_Alarmas.DURATION,"5 seg");
        values.put(Contrato.Tekus_Alarmas.INFORMACION,"no hay informacion");
        sqLiteDatabase.insertOrThrow(Tabla.ALARMAS,null,values);


    }



}
