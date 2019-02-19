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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import tfg.shuttlego.activities.origin.OriginMain;
import tfg.shuttlego.model.adapter.RecyclerViewAdapterOrigin;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.person.Person;

/**
 *
 */
public class AdminMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ArrayList<Origin> listOrigins;
    private NavigationView navigationView;
    private Person user;
    private ProgressBar adminMainProgress;
    private EditText adminMainEdit;
    private Button adminMainButton;
    private LinearLayout adminMainLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");
        incializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();
        removeProgressBar();
        listeners();
    }

    private void listeners() {

        adminMainButton.setOnClickListener(this);
    }

    private void incializateView() {

        adminMainProgress = findViewById(R.id.admin_main_content_progress);
        adminMainEdit = findViewById(R.id.admin_main_content_edittext);
        adminMainButton = findViewById(R.id.admin_main_content_button);
        adminMainLinear = findViewById(R.id.admin_main_content_linar1);
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
    private void setProgressBar () {

        adminMainProgress.setVisibility(View.VISIBLE);
        adminMainLinear.setVisibility(View.GONE);
    }//setProgressBar

    /**
     *
     */
    private void removeProgressBar () {

        adminMainProgress.setVisibility(View.GONE);
        adminMainLinear.setVisibility(View.VISIBLE);
    }//removeProgressBar

    /**
     *
     */
    private void createListView() {

        RecyclerView recycler = findViewById(R.id.origin_cardview_cardview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        RecyclerView.Adapter<RecyclerViewAdapterOrigin.OriginViewHolder> adapter = new RecyclerViewAdapterOrigin(listOrigins, user);
        recycler.setAdapter(adapter);

        //Quitar el progress bar y mostrar la lista.
    }//createListView

    /**
     *
     * @return
     */
    private JSONObject buildJson() {

        JSONObject dataUser = new JSONObject();
        JSONObject dataOrigin = new JSONObject();
        JSONObject createOrigin = new JSONObject();

        try {

            dataUser.put("email", user.getEmail());
            dataUser.put("password", user.getPassword());
            dataOrigin.put("name", adminMainEdit.getText());
            createOrigin.put("user", dataUser);
            createOrigin.put("origin", dataOrigin);

        } catch (JSONException e) { throwToast(R.string.err); }

        return createOrigin;
    }//buildJson

    /**
     *
     * @param createOrigin
     */
    private void throwEventAddOrigin(JSONObject createOrigin) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.CREATEORIGIN, createOrigin)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {

                    removeProgressBar();
                    throwToast(R.string.errConexion);
                } else if (task.getResult().containsKey("error")){

                    removeProgressBar();

                    switch (Objects.requireNonNull(task.getResult().get("error"))) {
                        case "badRequestForm": throwToast(R.string.errBadFormat); break;
                        case "originAlreadyExists": throwToast(R.string.errOriginExisit); break;
                        case "server": throwToast(R.string.errServer); break;
                    }
                }
                else {
                    Intent logIntent = new Intent(AdminMain.this, OriginMain.class);
                    startActivity(logIntent);
                }
            }
        });
    }//throwEvent

    /**
     *
     * @param msg
     */
    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.admin_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();

    }//onBackPressed

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_add_origin) {

            /*Intent logIntent = new Intent(AdminMain.this, AddOrigin.class);
            logIntent.putExtra("user", user);
            startActivity(logIntent);
            overridePendingTransition(R.anim.left_out, R.anim.left_in);*/
        }
        else if (id == R.id.nav_settings_admin) { }
        else if (id == R.id.nav_signout_admin) { }

        DrawerLayout drawer = findViewById(R.id.admin_main);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }//onNavigationItemSelected

    @Override
    public void onClick(View v) {

        boolean empty = false;

        switch (v.getId()) {

            case R.id.admin_main_content_button:
                if (adminMainEdit.getText().toString().isEmpty()) empty = true;

                if (!empty) {

                    setProgressBar();
                    throwEventAddOrigin(buildJson());
                }
                else throwToast(R.string.errDataEmpty);
                break;
        }
    }
}
