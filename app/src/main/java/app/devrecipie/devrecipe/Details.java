package app.devrecipie.devrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Administrator on 1/5/2017.
 */

public class Details extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        TextView rec_name=(TextView)findViewById(R.id.rec_name);
        TextView ingredients_name=(TextView)findViewById(R.id.ingredients_name);
        TextView steps_name=(TextView)findViewById(R.id.steps_name);


        Intent ine = getIntent();
        Bundle bundle = ine.getBundleExtra("Flag");

        rec_name.setText(bundle.getString("recName"));
        ingredients_name.setText(bundle.getString("ingredients"));
        steps_name.setText(bundle.getString("steps_name"));
    }
}
