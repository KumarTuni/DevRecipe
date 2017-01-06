package app.devrecipie.devrecipe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Administrator on 11/4/2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "devrecipe";
    private static final String TABLE_NAME = "recipedetails";
    private static final String KEY_ID = "recipeId";
    private static final String KEY_RECIPE = "recipename";
    private static final String KEY_RECIPETYPE = "recipetype";
    private static final String KEY_INGREDIENTS= "ingredients";
    private static final String KEY_STEPS = "steps";
    //private static final String KEY_MEDICINE = "medicine";
    // SQLiteDatabase myDB;



    public DBHelper(Context applicationContext) {
        super(applicationContext, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        String patientDetailsTable = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_RECIPE + " TEXT,"+ KEY_RECIPETYPE + " TEXT," + KEY_INGREDIENTS + " TEXT," + KEY_STEPS + " TEXT)";
       // Log.v("patientDetailsTable==", patientDetailsTable);
        sqLiteDatabase.execSQL(patientDetailsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.v("patientDetailsTable==", "onUpgrade");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    ArrayList<String> addPersonalDetails(RecipeDetailsBean pdb) {
        ArrayList<String> retun_str=new ArrayList<>();
        SQLiteDatabase sdb = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_RECIPE, pdb.getRecipeName());
            values.put(KEY_RECIPETYPE, pdb.getRecipeType());
            values.put(KEY_INGREDIENTS, pdb.getIngredients());
            values.put(KEY_STEPS, pdb.getSteps());
            int save_return= (int) sdb.insert(TABLE_NAME, null, values);
            if (save_return != -1){
                retun_str.add("Saved");
                retun_str.add(String.valueOf(save_return));

            }else {
                retun_str.add("Notsaved");
            }


        return  retun_str;
    }

    ArrayList<RecipeDetailsBean> getAllDetails() {
        ArrayList<RecipeDetailsBean> personDetailsAL = new ArrayList<>();
        String listqry = "SELECT * FROM " + TABLE_NAME +" order by "+ KEY_ID +" DESC";
        SQLiteDatabase sdb = this.getWritableDatabase();
        Cursor cursor = sdb.rawQuery(listqry, null);
        while (cursor.moveToNext()) {
            RecipeDetailsBean pdb = new RecipeDetailsBean();
            pdb.setRecipeId(cursor.getString(0));
            pdb.setRecipeName(cursor.getString(1));
            pdb.setRecipeType(cursor.getString(2));
            pdb.setIngredients(cursor.getString(3));
            pdb.setSteps(cursor.getString(4));
            pdb.setFlag("true");
            personDetailsAL.add(pdb);
        }
        if (personDetailsAL.size()==0){
            RecipeDetailsBean pdb = new RecipeDetailsBean();
            pdb.setFlag("false");
            personDetailsAL.add(pdb);
        }
        return personDetailsAL;
    }


    String updateRecord(RecipeDetailsBean pdb){
        String retun_str;
        SQLiteDatabase sdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
       // values.put(KEY_ID, pdb.getRecipeId());
        values.put(KEY_RECIPE, pdb.getRecipeName());
        values.put(KEY_RECIPETYPE, pdb.getRecipeType());
        values.put(KEY_INGREDIENTS, pdb.getIngredients());
        values.put(KEY_STEPS, pdb.getSteps());
        int save_return= sdb.update(TABLE_NAME,values,KEY_ID + " = ?",new String[]{pdb.getRecipeId()});
        if (save_return != -1){
            retun_str="Updated";
        }else {
            retun_str="Notupdated";
        }
        return  retun_str;
    }
    String deleteRecord(String deleteid){
        String retun_str;
        SQLiteDatabase sdb = this.getWritableDatabase();
        int save_return= sdb.delete(TABLE_NAME,KEY_ID + " = ?",new String[]{deleteid});
        if (save_return != -1){
            retun_str="Deleted";
        }else {
            retun_str="Notdeleted";
        }
        return  retun_str;
    }

}
