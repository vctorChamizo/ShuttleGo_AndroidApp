package tfg.shuttlego.activities.person.admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.origin.OriginList;
import tfg.shuttlego.activities.origin.OriginMain;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.person.Person;

/**
 *
 */
public class AdminMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private NavigationView navigationView;
    private Person user;
    private ProgressBar adminMainProgress;
    private EditText adminMainEdit;
    private Button adminMainButton;
    private LinearLayout adminMainLinear;
    private DrawerLayout admiMainDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        user = Session.getInstance(getApplicationContext()).getUser();

        incializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();
        removeProgressBar();

        adminMainButton.setOnClickListener(this);
    }

    private void incializateView() {

        adminMainProgress = findViewById(R.id.admin_main_content_progress);
        adminMainLinear = findViewById(R.id.admin_main_content_linar1);
        adminMainEdit = findViewById(R.id.admin_main_content_edittext);
        adminMainButton = findViewById(R.id.admin_main_content_button);
        admiMainDrawer = findViewById(R.id.admin_main_drawer);

    }//incializateView

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
    private void setMenuDrawer() {

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, admiMainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        admiMainDrawer.addDrawerListener(toggle);
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
     * @return
     */
    private JSONObject buildJson(String originName) {

        JSONObject dataUser = new JSONObject();
        JSONObject dataOrigin = new JSONObject();
        JSONObject createOrigin = new JSONObject();

        try {

            dataUser.put("email", user.getEmail());
            dataUser.put("password", user.getPassword());
            dataOrigin.put("name", originName);
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
        .addOnCompleteListener(task -> {

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
                logIntent.putExtra("origin", task.getResult().get("id"));
                throwToast(R.string.createOriginSuccesful);
                startActivity(logIntent);
            }
        });
    }//throwEvent

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        if (adminMainEdit.getText().toString().isEmpty()) throwToast(R.string.errDataEmpty);
        else {

            setProgressBar();
            throwEventAddOrigin(buildJson(String.valueOf(adminMainEdit.getText())));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.admin_drawer_list:
                startActivity(new Intent(AdminMain.this, OriginList.class));
                break;

            case R.id.nav_settings_admin:
                break;

            case R.id.nav_signout_admin:
                break;

            default: break;
        }


        admiMainDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        if (admiMainDrawer.isDrawerOpen(GravityCompat.START)) admiMainDrawer.closeDrawer(GravityCompat.START);
        else {

        }
    }
}
