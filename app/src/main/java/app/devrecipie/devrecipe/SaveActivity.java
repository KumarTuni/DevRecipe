package app.devrecipie.devrecipe;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Administrator on 11/3/2016.
 */

public class SaveActivity extends AppCompatActivity {
    private String recipeid,recipename, recipetype, ingredients, steps;
    EditText recipename_et, ingredints_et, steps_et;
    ProgressBar save_pb;
    DBHelper dbh;
    int flag=1;
    Bundle bundle;
    Button save_btn;
    TextView title_tv;
    Spinner recipetype_spn;
    ArrayList<String> recid_al,rectype_al;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.save_layout);
        recipename_et = (EditText) findViewById(R.id.recipe_edt);
        ingredints_et = (EditText) findViewById(R.id.ingredients_edt);
        steps_et = (EditText) findViewById(R.id.steps_edt);
        title_tv = (TextView) findViewById(R.id.title_tv);
        recipetype_spn=(Spinner)findViewById(R.id.recipetype_spn);
        save_btn=(Button)findViewById(R.id.save_btn);
        save_pb=(ProgressBar)findViewById(R.id.save_pb);

        dbh = new DBHelper(getApplication());

        recid_al= new ArrayList<>();
        rectype_al= new ArrayList<>();


       /* rectype_al.add("Vegetarian");
        rectype_al.add("Fast Food");
        rectype_al.add("Healthy");
        rectype_al.add("No-Cook");
        rectype_al.add("Make Ahead");
*/
        Intent ine = getIntent();
        bundle = ine.getBundleExtra("Flag");
        if (bundle != null) {

            flag = Integer.parseInt(bundle.getString("flag"));

            if (flag == 2) {

                recipeid = bundle.getString("recipeId");
                recipename = bundle.getString("recName");
                recipetype = bundle.getString("recType");
                ingredients = bundle.getString("ingredients");
                steps = bundle.getString("step");


                recipename_et.setText(recipename);
                recipename_et.setSelection(recipename.length());
                ingredints_et.setText(ingredients);
                steps_et.setText(steps);
                recipetype_spn.setSelection(rectype_al.indexOf(recipetype));
                // medicine_et.setText(medicine);
                save_btn.setText("Update");
                title_tv.setText("Update Recipe");

            }
        }



        try {
            InputStream is = getAssets().open("recipetypes.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("recipetype");

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;

                   // recid_al.add(getValue("recipetype", element2)+"\n");
                    rectype_al.add(getValue("name", element2)+"\n");



                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, rectype_al);
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipetype_spn.setAdapter(userTypeAdapter);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipename = recipename_et.getText().toString();
                recipetype = rectype_al.get(recipetype_spn.getSelectedItemPosition());
                ingredients = ingredints_et.getText().toString();
                steps = steps_et.getText().toString();


                Log.v("kjsahd",flag+"");


                if (recipename.trim().length() == 0) {
                    Toast.makeText(SaveActivity.this, "Enter RecipeName", Toast.LENGTH_SHORT).show();
                } else if (ingredients.trim().length() == 0) {
                    Toast.makeText(SaveActivity.this, "Enter Ingredients", Toast.LENGTH_SHORT).show();
                } else if (steps.trim().length() == 0) {
                    Toast.makeText(SaveActivity.this, "Enter Steps", Toast.LENGTH_SHORT).show();
                }else {
                    new SaveAsync(flag, SaveActivity.this).execute();

                }
            }
        });

    }
    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }


    private class SaveAsync extends AsyncTask<Void, Void, Void> {

        int mfalg;
        Context mcontxt;
        ArrayList<String> return_result = new ArrayList<>();
        String return_result_str;

        public SaveAsync(int flag, SaveActivity saveActivity) {
            mfalg = flag;
            mcontxt = saveActivity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (mfalg == 1) {
                    RecipeDetailsBean pdb = new RecipeDetailsBean();
                    pdb.setRecipeName(recipename);
                    pdb.setRecipeType(recipetype);
                    pdb.setIngredients(ingredients);
                    pdb.setSteps(steps);
                    return_result = dbh.addPersonalDetails(pdb);
                } else if (mfalg == 2) {
                    RecipeDetailsBean pdb = new RecipeDetailsBean();
                    pdb.setRecipeId(recipeid);
                    pdb.setRecipeName(recipename);
                    pdb.setRecipeType(recipetype);
                    pdb.setIngredients(ingredients);
                    pdb.setSteps(steps);
                    return_result_str = dbh.updateRecord(pdb);

                }
            } catch (Exception e) {
                return_result.add("null");
                return_result_str = "null";
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            save_btn.setVisibility(View.GONE);
            save_pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            save_btn.setVisibility(View.VISIBLE);
            save_pb.setVisibility(View.GONE);
            if (mfalg == 1) {
                if (return_result.get(0).equalsIgnoreCase("null")) {
                    Toast.makeText(mcontxt, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                } else if (return_result.get(0).equalsIgnoreCase("Saved")) {
                    Toast.makeText(mcontxt, "Data Saved", Toast.LENGTH_SHORT).show();
                    save_btn.setText("Update");
                    title_tv.setText("Update Details");
                    recipeid = return_result.get(0);
                    flag = 2;

                } else if (return_result.get(0).equalsIgnoreCase("Notsaved")) {
                    Toast.makeText(mcontxt, "Data Not Saved", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (return_result_str.equalsIgnoreCase("null")) {
                    Toast.makeText(mcontxt, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                } else if (return_result_str.equalsIgnoreCase("Updated")) {
                    Toast.makeText(mcontxt, "Data Updated", Toast.LENGTH_SHORT).show();
                } else if (return_result_str.equalsIgnoreCase("Notupdated")) {
                    Toast.makeText(mcontxt, "Data Not Updated", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


}
