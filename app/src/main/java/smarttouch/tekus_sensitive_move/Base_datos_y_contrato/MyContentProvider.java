package smarttouch.tekus_sensitive_move.Base_datos_y_contrato;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {

    // Comparador de URIs
    public static final UriMatcher uriMatcher ;


    // Casos
    public static final int ALARMA = 100;
    public static final int ALARMA_ID = 101;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(Contrato.AUTORIDAD, "alarmas", ALARMA);
        uriMatcher.addURI(Contrato.AUTORIDAD, "alarmas/*", ALARMA_ID);

    }

    private BaseDatos bd;
    private ContentResolver resolver;
    SQLiteDatabase SQLDB;

    public MyContentProvider() {
    }


    @Override
    public boolean onCreate() {
        bd = new BaseDatos(getContext());
        resolver = getContext().getContentResolver();
        SQLDB = bd.getWritableDatabase();
        return SQLDB != null && SQLDB.isOpen();
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = bd.getWritableDatabase();
        // Comparar Uri
        int match = uriMatcher.match(uri);

        Cursor c;

        switch (match) {

            // para consultar Alarmas
            case ALARMA:


                // Consultando todos los registros
                c = db.query(BaseDatos.Tabla.ALARMAS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Contrato.Tekus_Alarmas.URI_CONTENIDO);
                break;
            case ALARMA_ID:
                // Consultando un solo registro basado en el Id del Uri
                String idAlmacen = Contrato.Tekus_Alarmas.obtenerIdAlarmas(uri);
                c = db.query(BaseDatos.Tabla.ALARMAS, projection,
                        BaseColumns._ID + "=" + "\'" + idAlmacen + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALARMA:
                return Contrato.Tekus_Alarmas.MIME_COLECCION;
            case ALARMA_ID:
                return Contrato.Tekus_Alarmas.MIME_RECURSO;

            default:
                throw new IllegalArgumentException("Tipo desconocido: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // inicio insercion de Alaarma
        if(uriMatcher.match(uri)==ALARMA) {
            ContentValues contentValues;
            if (values != null) {
                contentValues = new ContentValues(values);
            } else {
                contentValues = new ContentValues();
            }

            // Inserción de nueva fila
            SQLDB = bd.getWritableDatabase();
            long rowId = SQLDB.insert(BaseDatos.Tabla.ALARMAS,
                    null, contentValues);
            if (rowId > 0) {
                Uri uri_actividad =
                        ContentUris.withAppendedId(
                                Contrato.Tekus_Alarmas.URI_CONTENIDO, rowId);
                getContext().getContentResolver().
                        notifyChange(uri_actividad, null);
                return uri_actividad;
            }
        }//  fin de insercione de Alarmas
        // error de insercion
        throw new SQLException("Falla al insertar fila en : " + uri);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int affected;
        switch (uriMatcher.match(uri)) {

            // Eliminar Almacenes
            case ALARMA:
                affected = SQLDB.delete(BaseDatos.Tabla.ALARMAS,
                        selection, selectionArgs);

                break;
            case ALARMA_ID:
                String idActividad = uri.getPathSegments().get(1);
                affected = SQLDB.delete(BaseDatos.Tabla.ALARMAS,
                        BaseColumns._ID + "=" + idActividad
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return affected;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int affected;
        switch (uriMatcher.match(uri)) {
            case ALARMA:
                affected = SQLDB.update(BaseDatos.Tabla.ALARMAS, values,
                        selection, selectionArgs);
                break;
            case ALARMA_ID:
                String idActividad = uri.getPathSegments().get(1);
                affected = SQLDB.update(BaseDatos.Tabla.ALARMAS, values,
                        BaseColumns._ID + "=" + idActividad
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return affected;
    }


}
