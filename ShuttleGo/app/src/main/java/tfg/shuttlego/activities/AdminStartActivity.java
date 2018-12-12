package tfg.shuttlego.activities;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.logic.MyAdapter;
import tfg.shuttlego.logic.events.Event;
import tfg.shuttlego.logic.events.EventDispatcher;
import tfg.shuttlego.logic.origin.Origin;
import tfg.shuttlego.logic.person.Person;


public class AdminStartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<Origin> listOrigins;
    private NavigationView navigationView;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_start);

        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        setCredencials();
        loadOriginList();
        createList();

        setSupportActionBar(toolbar);

        //BOTON DE AÑADIR
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*DATA EXAMPLE: {user:{email:"admin@gmail.com",password:"123"},origin:{name:"Barajas T5"}}*/

                JSONObject dataUser = new JSONObject();
                JSONObject dataOrigin = new JSONObject();
                JSONObject createOrigin = new JSONObject();

                try {

                    dataUser.put("email", "admin@gmail.com");
                    dataUser.put("password", "123");
                    dataOrigin.put("name", "Barajas T8");
                    createOrigin.put("user", dataUser);
                    createOrigin.put("origin", dataOrigin);

                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(), "ERROR. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                }


                // CONTROLAR MAS ERRORES --> ESTO ES SOLO UNA PRUEBA DE SERVIDOR.
                EventDispatcher.getInstance(getApplicationContext())
                .dispatchEvent(Event.CREATEORIGIN, createOrigin)
                .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
                    @Override
                    public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                        if (!task.isSuccessful() || task.getResult() == null) {
                            Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();

                        } else if (task.getResult().containsKey("error"))
                            switch (Objects.requireNonNull(task.getResult().get("error"))) {
                                case "server":
                                    Toast.makeText(getApplicationContext(), "Error del servidor", Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    Toast.makeText(getApplicationContext(), "Error desconocido: " + task.getResult().get("error"), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        else {

                            Object result = task.getResult();

                        }//else
                    }//onComplete
                });
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void createList() {

    }

    private void loadOriginList() {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINS, null)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {
                    Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();

                } else if (task.getResult().containsKey("error"))
                        switch (Objects.requireNonNull(task.getResult().get("error"))) {
                            case "server":
                                Toast.makeText(getApplicationContext(), "Error del servidor", Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                Toast.makeText(getApplicationContext(), "Error desconocido: " + task.getResult().get("error"), Toast.LENGTH_SHORT).show();
                                break;
                        }
                else {

                    HashMap<?, ?> result = task.getResult();
                    ArrayList<HashMap<?,?>> list = (ArrayList<HashMap<?,?>>)result.get("origins");
                    listOrigins = new ArrayList<>();
                    Origin origin = new Origin();

                    //Controlar que la lista no llegue vacia ¿assert?

                    for (int i = 0; i < list.size(); ++i){
                        origin.setId((String) list.get(i).get("id"));
                        origin.setName((String) list.get(i).get("name"));
                        listOrigins.add(origin);
                    }//for

                    RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    RecyclerView.Adapter mAdapter = new MyAdapter(listOrigins);
                    mRecyclerView.setAdapter(mAdapter);

                }//else
            }//onComplete
        });
    }//loadOriginList


    @SuppressLint("SetTextI18n")
    private void setCredencials() {
        View hView =  navigationView.getHeaderView(0);
        TextView nav_name_text = hView.findViewById(R.id.name_admin_text);
        TextView nav_email_text = hView.findViewById(R.id.email_admin_text);
        nav_name_text.setText(user.getName() + " " + user.getSurname());
        nav_email_text.setText(user.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
