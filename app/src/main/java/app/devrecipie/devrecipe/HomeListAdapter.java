package app.devrecipie.devrecipe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Administrator on 11/4/2016.
 */

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.MyViewHolder> {
    Context adapContxt;
    ArrayList<RecipeDetailsBean> personDetailsAL;
    RecipeDetailsBean pdb;
    LayoutInflater mInflator;
    AlertDialog ad;
    String result_str;
    AlertDialog dialog;

    public HomeListAdapter(Context contxt, ArrayList<RecipeDetailsBean> pbAL) {
        personDetailsAL = pbAL;
        adapContxt = contxt;
        mInflator = LayoutInflater.from(contxt);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.recipe_details_adap, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        pdb = new RecipeDetailsBean();
        pdb = personDetailsAL.get(position);
        holder.recipename_tv.setText(pdb.getRecipeName());
        holder.recipetype_tv.setText(pdb.getRecipeType());
        holder.ingredients_tv.setText(pdb.getIngredients());
        holder.steps_tv.setText(pdb.getSteps());

        //holder.medicine_tv.setText(pdb.getMedicine());



        holder.delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(adapContxt,R.style.DialogTheme);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DeleteAsynch(adapContxt,position).execute();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });
                dialog=alert.create();
                dialog.setMessage("Do You Want Delete?");
                dialog.setCancelable(false);
                dialog.show();
            }
        });
        holder.edit_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent update_intent = new Intent(adapContxt, SaveActivity.class);
                Bundle up_bundle = new Bundle();
                up_bundle.putString("flag", "2");
                up_bundle.putString("recipeId", personDetailsAL.get(position).getRecipeId());
                up_bundle.putString("recName", personDetailsAL.get(position).getRecipeName());
                up_bundle.putString("recType", personDetailsAL.get(position).getRecipeType());
                up_bundle.putString("ingredients", personDetailsAL.get(position).getIngredients());
                up_bundle.putString("step", personDetailsAL.get(position).getSteps());
                update_intent.putExtra("Flag", up_bundle);
                adapContxt.startActivity(update_intent);

            }
        });
        holder.deatils_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent update_intent = new Intent(adapContxt, Details.class);
                Bundle up_bundle = new Bundle();
                up_bundle.putString("flag", "2");
                up_bundle.putString("recipeId", personDetailsAL.get(position).getRecipeId());
                up_bundle.putString("recName", personDetailsAL.get(position).getRecipeName());
                up_bundle.putString("recType", personDetailsAL.get(position).getRecipeType());
                up_bundle.putString("ingredients", personDetailsAL.get(position).getIngredients());
                up_bundle.putString("steps_name", personDetailsAL.get(position).getSteps());
                update_intent.putExtra("Flag", up_bundle);
                adapContxt.startActivity(update_intent);

            }
        });
        //  Toast.makeText(adapContxt,pdb.getFirstName()+" ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return personDetailsAL.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView recipename_tv, recipetype_tv, ingredients_tv, steps_tv;
        LinearLayout deatils_cv;
        ImageView delete_iv,edit_iv;

        public MyViewHolder(View itemView) {
            super(itemView);
            recipename_tv = (TextView) itemView.findViewById(R.id.recipename_tv);
            recipetype_tv = (TextView) itemView.findViewById(R.id.recipetype_tv);
            ingredients_tv = (TextView) itemView.findViewById(R.id.ingredients_tv);
            steps_tv = (TextView) itemView.findViewById(R.id.steps_tv);
            delete_iv=(ImageView)itemView.findViewById(R.id.delete_iv);
            edit_iv=(ImageView)itemView.findViewById(R.id.recip_edit_iv);
            deatils_cv=(LinearLayout)itemView.findViewById(R.id.detailsLL);

        }
    }

    private class DeleteAsynch extends AsyncTask<Void,Void,Void> {

        int delete_pos;
        Context mcontext;

        public DeleteAsynch(Context adapContxt, int position) {
            mcontext=adapContxt;
            delete_pos=position;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DBHelper dbh = new DBHelper(mcontext);
            try {
                result_str = dbh.deleteRecord(personDetailsAL.get(delete_pos).getRecipeId());
            } catch (Exception e) {
                result_str = "null";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (result_str.equalsIgnoreCase("null")){
                Toast.makeText(adapContxt,"Something Went Wrong", Toast.LENGTH_SHORT).show();
            }else if(result_str.equalsIgnoreCase("Deleted")){
                Toast.makeText(adapContxt,"Deleted", Toast.LENGTH_SHORT).show();
                personDetailsAL.remove(delete_pos);
                HomeListAdapter.this.notifyDataSetChanged();
            }else if (result_str.equalsIgnoreCase("Notdeleted")){
                Toast.makeText(adapContxt,"Not Deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
