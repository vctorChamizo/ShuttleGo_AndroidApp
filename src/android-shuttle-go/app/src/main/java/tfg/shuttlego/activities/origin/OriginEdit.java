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
import android.view.WindowManager;
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

    private DrawerLayout originEditDrawer;
    private NavigationView originEditNavigation;

    private ProgressBar originEditProgress;
    private LinearLayout originEditLinear;

    private Button originEditEditButton;
    private EditText originEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.origin_edit);

        this.originEditOriginObject = (Origin)Objects.requireNonNull(getIntent().getExtras()).getSerializable("origin");
        this.user = Session.getInstance().getUser();

        inicializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();

        this.originEditText.append(originEditOriginObject.getName());

        removeProgressBar();

        this.originEditEditButton.setOnClickListener(this);

        this.originEditNavigation.setNavigationItemSelectedListener(this);
    }

    /**
     * Inicializate the componentes of this view
     */
    private void inicializateView() {

        this.originEditLinear = findViewById(R.id.origin_edit_linear);
        this.originEditProgress = findViewById(R.id.origin_edit_progress);

        this.originEditNavigation = findViewById(R.id.origin_edit_nav);
        this.originEditDrawer = findViewById(R.id.origin_edit_drawer);

        this.originEditEditButton = findViewById(R.id.origin_edit_edit_btn);

        this.originEditText = findViewById(R.id.origin_edit_text);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar () {
        this.originEditProgress.setVisibility(View.VISIBLE);
        this.originEditLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar () {
        this.originEditProgress.setVisibility(View.GONE);
        this.originEditLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view
     */
    private void setMenuDrawer() {

        Toolbar toolbar = findViewById(R.id.origin_edit_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.originEditDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.originEditDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user
     */
    private void setCredencials() {

        View hView =  this.originEditNavigation.getHeaderView(0);

        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);

        String complete_name = this.user.getName() + " " + user.getSurname();
        nav_name_text.setText(complete_name);
        nav_email_text.setText(this.user.getEmail());
    }

    /**
     * Build a JSON for to allow make a modify in the current origin
     *
     * @param newNameOrigin New name to modigy current origin
     *
     * @return JSON with information to modify origin
     */
    private JSONObject buildJson(CharSequence newNameOrigin) {

        JSONObject dataUser = new JSONObject();
        JSONObject dataOrigin = new JSONObject();
        JSONObject editOrigin = new JSONObject();

        try {

            dataUser.put("email", Session.getInstance().getUser().getEmail());
            dataUser.put("password", Session.getInstance().getUser().getPassword());
            dataOrigin.put("id", this.originEditOriginObject.getId());
            dataOrigin.put("name", newNameOrigin);

            String coordinates = this.originEditOriginObject.getCoordinates().get(0).toString() + "," + this.originEditOriginObject.getCoordinates().get(1).toString();
            dataOrigin.put("coordinates", coordinates);

            editOrigin.put("user", dataUser);
            editOrigin.put("origin", dataOrigin);

        } catch (JSONException e) { throwToast(R.string.err); }

        return editOrigin;
    }

    /**
     * Throw the event that allow modify the current origin
     *
     * @param origin JSON with information to modify origin
     */
    @SuppressWarnings("ConstantConditions")
    private void throwEventModifyOrigin(JSONObject origin) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.MODIFYORIGIN, origin)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {

                removeProgressBar();
                throwToast(R.string.errConexion);
            }
            else if (task.getResult().containsKey("error")) {

                removeProgressBar();
                throwToast(R.string.errServer);
            }
            else {

                HashMap<?,?> result = task.getResult();

                if ((Boolean) result.get("modified")) {

                    throwToast(R.string.editOriginSuccesful);

                    Intent intent = new Intent(OriginEdit.this, OriginMain.class);
                    intent.putExtra("origin", this.originEditOriginObject.getId());
                    finish();
                    startActivity(intent);
                }
                else {

                    throwToast(R.string.errOriginAlreadyExists);
                    removeProgressBar();
                }
            }
        });
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.origin_edit_edit_btn:

                if (this.originEditText.getText().toString().isEmpty()) throwToast(R.string.errDataEmpty);
                else if (this.originEditText.getText().toString().equals(this.originEditOriginObject.getName())) throwToast(R.string.errOriginAlreadyExists);
                else {

                    setProgressBar();
                    throwEventModifyOrigin(buildJson(this.originEditText.getText()));
                }

                break;
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

        this.originEditDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        if (this.originEditDrawer.isDrawerOpen(GravityCompat.START)) this.originEditDrawer.closeDrawer(GravityCompat.START);
        else finish();
    }
}
