package tfg.shuttlego.activities.origin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.admin.AdminMain;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.person.Person;

public class OriginEdit extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Person user;
    private Origin originEditOriginObject;
    private ProgressBar originEditProgress;
    private LinearLayout originEditLinear;
    private Button originEditEditButton, originEditCancelButton;
    private EditText originEditText;
    private DrawerLayout originEditDrawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.origin_edit);

        originEditOriginObject = (Origin)Objects.requireNonNull(getIntent().getExtras()).getSerializable("origin");
        user = Session.getInstance(getApplicationContext()).getUser();

        inicializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();
        originEditText.append(originEditOriginObject.getName());
        removeProgressBar();
        listeners();
    }

    /**
     * Inicializate the componentes of this view
     */
    private void inicializateView() {

        originEditLinear = findViewById(R.id.origin_edit_linear);
        originEditProgress = findViewById(R.id.origin_edit_progress);
        originEditEditButton = findViewById(R.id.origin_edit_edit_btn);
        originEditCancelButton = findViewById(R.id.origin_edit_cancel_btn);
        originEditText = findViewById(R.id.origin_edit_text);
        originEditDrawer = findViewById(R.id.origin_edit_drawer);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar () {

        originEditProgress.setVisibility(View.VISIBLE);
        originEditLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar () {

        originEditProgress.setVisibility(View.GONE);
        originEditLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view
     */
    private void setMenuDrawer() {

        navigationView = findViewById(R.id.origin_edit_nav);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.origin_edit_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, originEditDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        originEditDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user
     */
    private void setCredencials() {

        View hView =  navigationView.getHeaderView(0);
        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);
        nav_name_text.setText(user.getName() + " " + user.getSurname());
        nav_email_text.setText(user.getEmail());
    }

    /**
     * Have the listeners to the action components in the view
     */
    private void listeners() {

        originEditEditButton.setOnClickListener(this);
        originEditCancelButton.setOnClickListener(this);
    }

    /**
     * Build a JSON for to allow make a modify in the current origin
     *
     * @param nameOrigin New name to modigy current origin
     *
     * @return JSON with information to modify origin
     */
    private JSONObject buildJson(CharSequence nameOrigin) {

        JSONObject dataUser = new JSONObject();
        JSONObject dataOrigin = new JSONObject();
        JSONObject editOrigin = new JSONObject();

        try {

            dataUser.put("email", Session.getInstance(getApplicationContext()).getUser().getEmail());
            dataUser.put("password", Session.getInstance(getApplicationContext()).getUser().getPassword());
            dataOrigin.put("id", originEditOriginObject.getId());
            dataOrigin.put("name", nameOrigin);
            editOrigin.put("user", dataUser);
            editOrigin.put("origin", dataOrigin);

        }
        catch (JSONException e) { throwToast(R.string.err); }

        return editOrigin;
    }

    /**
     * Throw the event that allow modify the current origin
     *
     * @param origin JSON with information to modify origin
     */
    private void throwEventModifyOrigin(JSONObject origin) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.MODIFYORIGIN, origin)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
            else if (task.getResult().containsKey("error")) throwToast(R.string.errServer);
            else {

                HashMap<?,?> result = task.getResult();
                if ((Boolean) result.get("modified")) {

                    Intent intent = new Intent(OriginEdit.this, OriginMain.class);
                    intent.putExtra("origin", originEditOriginObject.getId());
                    startActivity(intent);
                    throwToast(R.string.editOriginSuccesful);
                    finish();
                }
                else throwToast(R.string.errOriginAlreadyExists);
                removeProgressBar();
            }
        });
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.origin_edit_edit_btn:
                if (originEditText.getText().toString().isEmpty()) throwToast(R.string.errDataEmpty);
                else if (originEditText.getText().toString().equals(originEditOriginObject.getName())) throwToast(R.string.errOriginAlreadyExists);
                else {

                    setProgressBar();
                    throwEventModifyOrigin(buildJson(originEditText.getText()));
                }
                break;

            default: finish(); break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.admin_drawer_list:
                startActivity(new Intent(OriginEdit.this, OriginList.class));
                finish();
                break;
                
            case R.id.admin_drawer_home:
                startActivity(new Intent(OriginEdit.this, AdminMain.class));
                finish();
                break;
        }

        originEditDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (originEditDrawer.isDrawerOpen(GravityCompat.START)) originEditDrawer.closeDrawer(GravityCompat.START);
        else finish();
    }

}
