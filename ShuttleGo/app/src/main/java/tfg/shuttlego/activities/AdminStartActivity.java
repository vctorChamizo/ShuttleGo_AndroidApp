package tfg.shuttlego.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.adapters.OriginAdapter;
import tfg.shuttlego.logic.events.Event;
import tfg.shuttlego.logic.events.EventDispatcher;
import tfg.shuttlego.logic.origin.Origin;
import tfg.shuttlego.logic.person.Person;


public class AdminStartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<Origin> listOrigins;
    private NavigationView navigationView;
    private Person user;
    private RecyclerView recycler;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        setMenuDrawer();

        setCredencials();

        loadOriginList();
    }

    private void setMenuDrawer() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }//setMenuDrawer

    private void setCredencials() {
        View hView =  navigationView.getHeaderView(0);
        TextView nav_name_text = hView.findViewById(R.id.name_admin_text);
        TextView nav_email_text = hView.findViewById(R.id.email_admin_text);
        nav_name_text.setText(user.getName() + " " + user.getSurname());
        nav_email_text.setText(user.getEmail());
    }//setCredencials

    private void loadOriginList() {

        //Activar el progressBar aqui

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

                    //Controlar que la lista no llegue vacia ¿assert?

                    for (int i = 0; i < list.size(); ++i){
                        Origin origin = new Origin();
                        origin.setId((String) list.get(i).get("id"));
                        origin.setName((String) list.get(i).get("name"));
                        listOrigins.add(origin);
                    }//for

                    createListView(); // --> mejorar la forma, haciendo que se espere hasta que se obtenga el resultado.

                }//else
            }//onComplete
        });
    }//loadOriginList

    private void createListView() {

        recycler = findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        adapter = new OriginAdapter(listOrigins, user);
        recycler.setAdapter(adapter);

        //Quitar el progress bar y mostrar la lista.
    }//createListView

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_add_origin) {

            Intent logIntent = new Intent(AdminStartActivity.this, AddOriginActivity.class);
            logIntent.putExtra("user", user);
            startActivity(logIntent);
            overridePendingTransition(R.anim.left_out, R.anim.left_in);
        }
        else if (id == R.id.nav_settings_admin) { }
        else if (id == R.id.nav_signout_admin) { }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }//onNavigationItemSelected
}
