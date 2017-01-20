package smarttouch.tekus_sensitive_move;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import smarttouch.tekus_sensitive_move.Adaptadores.Adapter_alarmas;
import smarttouch.tekus_sensitive_move.Base_datos_y_contrato.Contrato;

public class Alarmas extends AppCompatActivity implements  Adapter_alarmas.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    // variables del recycler view
    private RecyclerView listaUI;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_alarmas adapter_alarmas;
    private static final int LOADER_ALARMAS = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmas);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  proceso para iniciarlizar recycler view, linear layout

        // Preparar lista
        listaUI = (RecyclerView) findViewById(R.id.my_Recycler_view);
        listaUI.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this);
        listaUI.setLayoutManager(linearLayoutManager);

        adapter_alarmas = new Adapter_alarmas(this, this);
        listaUI.setAdapter(adapter_alarmas);

        // Iniciar loader
        getSupportLoaderManager().initLoader(LOADER_ALARMAS,null,this);

        ServicioAcelerometro.startActionFoo(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update) {

            WebApi_Volley Actualizar_lista = new WebApi_Volley();
            Actualizar_lista.Get_alarmas_all(getApplicationContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ALARMAS, null, this);
    }

    @Override
    public void onClick(Adapter_alarmas.ViewHolder holder, String idAlarmas) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, Contrato.Tekus_Alarmas.URI_CONTENIDO, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (adapter_alarmas != null) {
            adapter_alarmas.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        adapter_alarmas.swapCursor(null);
    }
}
