package tfg.shuttlego.activities.driver;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.model.adapters.ListViewAdapterOrigin;
import tfg.shuttlego.model.adapters.RecyclerViewAdapterOrigin;
import tfg.shuttlego.model.events.Event;
import tfg.shuttlego.model.events.EventDispatcher;
import tfg.shuttlego.model.transfers.origin.Origin;
import tfg.shuttlego.model.transfers.person.Person;

/**
 *
 */
public class DriverMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private NavigationView navigationView;
    private Person user;

    private ListView list;
    private ListViewAdapterOrigin adapter;
    private SearchView editsearch;
    private ArrayList<Origin> originList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main);

        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        setMenuDrawer();
        setCredencials();
        setSearchSpinner();

        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

    }//onCreate

    /**
     *
     */
    private void setSearchSpinner() {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINS, null)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {
                    //changeVisibility();
                    throwToast("Error de conexion");
                } else if (task.getResult().containsKey("error")) {

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
                    originList = new ArrayList<>();

                    assert list != null;
                    for (int i = 0; i < list.size(); ++i) {
                        Origin origin = new Origin();
                        origin.setId((String) list.get(i).get("id"));
                        origin.setName((String) list.get(i).get("name"));
                        originList.add(origin);
                    }//for

                    createListView();
                }//else
            }//onComplete
        });
    }//setSearchSpinner

    /**
     *
     */
    private void createListView() {

        list = findViewById(R.id.driver_main_content_listview);
        adapter = new ListViewAdapterOrigin(this, originList);
        editsearch = findViewById(R.id.driver_main_content_search);
        editsearch.setOnQueryTextListener(this);
    }//createListView

    @Override
    public boolean onQueryTextSubmit(String query) { return false; }

    @Override
    public boolean onQueryTextChange(String newText) {

        String text = newText;
        list.setVisibility(View.VISIBLE);
        list.setAdapter(adapter);
        adapter.filter(text);

        return false;
    }


    /**
     *
     */
    private void setMenuDrawer() {

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = findViewById(R.id.driver_main);
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
     * @param msg
     */
    private void throwToast(String msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.driver_main);

        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }//onBackPressed

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_settings_admin) { }
        else if (id == R.id.nav_signout_admin) { }

        DrawerLayout drawer = findViewById(R.id.driver_main);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }//onNavigationItemSelected
}
