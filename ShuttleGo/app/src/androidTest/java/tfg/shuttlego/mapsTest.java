package tfg.shuttlego;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.Test;
import org.junit.runner.RunWith;

import tfg.shuttlego.model.maps.Maps;

@RunWith(AndroidJUnit4.class)
public class mapsTest extends AppCompatActivity {
    Context appContext=this;
    @Test
    public void getTarea() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Maps.getInstance(appContext).getPlace("Av. del Ensanche de Vallecas, 37").addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        System.out.print(task.getResult());
                    }
                });
            }
        });
    }
}
