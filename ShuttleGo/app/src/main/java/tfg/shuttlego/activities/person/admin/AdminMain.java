package tfg.shuttlego.activities.person.admin;

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
import tfg.shuttlego.activities.origin.AddOrigin;
import tfg.shuttlego.model.adapter.RecyclerViewAdapterOrigin;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.person.Person;

/**
 *
 */
public class AdminMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Origin> listOrigins;
    private NavigationView navigationView;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        setMenuDrawer();
        setCredencials();
        throwEvent();
    }

    /**
     *
     */
    private void setMenuDrawer() {

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = findViewById(R.id.admin_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }//setMenuDrawer

    /**
     *
     */
    private void setCredencials() {

        View hView =  navigationView.getHeaderView(0);
        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);
        nav_name_text.setText(user.getName() + " " + user.getSurname());
        nav_email_text.setText(user.getEmail());
    }//setCredencials

    /**
     *
     */
    private void throwEvent() {

        //Activar el progressBar aqui

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINS, null)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {
                    //changeVisibility();
                    throwToast("Error de conexion");
                } else if (task.getResult().containsKey("error")) {

                    //changeVisibility();

                    switch (Objects.requireNonNull(task.getResult().get("error"))) {
                        case "server":
                            throwToast("Error del servidor");
                            break;

                        default:
                            throwToast("Error desconocido: " + task.getResult().get("error"));
                            break;
                    }//switch
                } else {

                    HashMap<?, ?> result = task.getResult();
                    ArrayList<HashMap<?, ?>> list = (ArrayList<HashMap<?, ?>>) result.get("origins");
                    listOrigins = new ArrayList<>();

                    assert list != null;
                    for (int i = 0; i < list.size(); ++i) {
                        Origin origin = new Origin();
                        origin.setId((String) list.get(i).get("id"));
                        origin.setName((String) list.get(i).get("name"));
                        listOrigins.add(origin);
                    }//for

                    createListView();

                }//else
            }//onComplete
        });
    }//throwEvent

    /**
     *
     */
    private void createListView() {

        RecyclerView recycler = findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        RecyclerView.Adapter<RecyclerViewAdapterOrigin.OriginViewHolder> adapter = new RecyclerViewAdapterOrigin(listOrigins, user);
        recycler.setAdapter(adapter);

        //Quitar el progress bar y mostrar la lista.
    }//createListView

    /**
     *
     * @param msg
     */
    private void throwToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }//throwToast

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.admin_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }//onBackPressed

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_add_origin) {

            Intent logIntent = new Intent(AdminMain.this, AddOrigin.class);
            logIntent.putExtra("user", user);
            startActivity(logIntent);
            overridePendingTransition(R.anim.left_out, R.anim.left_in);
        }
        else if (id == R.id.nav_settings_admin) { }
        else if (id == R.id.nav_signout_admin) { }

        DrawerLayout drawer = findViewById(R.id.admin_main);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }//onNavigationItemSelected
}
