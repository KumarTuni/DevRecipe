package app.devrecipie.devrecipe;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Administrator on 11/3/2016.
 */

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener{
    RecyclerView recipeRV;
    DBHelper dbh;
    ProgressBar list_pb;
    int flag;
    ArrayList<RecipeDetailsBean> pbAL,filterAl;
    int resumeflag=1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View home_view=inflater.inflate(R.layout.home_layout,container,false);
        FloatingActionButton fab = (FloatingActionButton)home_view.findViewById(R.id.fab);
        recipeRV=(RecyclerView)home_view.findViewById(R.id.home_rv);
        list_pb=(ProgressBar)home_view.findViewById(R.id.list_pb);
        /*ImageView image=(ImageView)home_view.findViewById(R.id.bg_image);
        Glide.with(this).load(R.drawable.background_image).into(image);*/
        dbh = new DBHelper(getActivity());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent save_intent=new Intent(getActivity(),SaveActivity.class);
                Bundle save_bundle=new Bundle();
                save_bundle.putString("flag","1");
                save_intent.putExtra("Flag",save_bundle);
                startActivity(save_intent);

            }
        });


        flag =1;
        new HomeListAsync(getActivity(),flag).execute();
        return home_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resumeflag == 2) {
            flag = 1;
            new HomeListAsync(getActivity(), flag).execute();
        }
        resumeflag = 2;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.setVisible(true);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        flag = 2;
        new HomeListAsync(getActivity(),flag,newText).execute();

        return false;
    }

    /*@Override
    public boolean onClose() {
        Toast.makeText(getActivity(),"Search Closed",Toast.LENGTH_SHORT).show();
        flag = 3;
        new HomeListAsync(getActivity(),flag).execute();
        return false;
    }*/

    private class HomeListAsync extends AsyncTask<Void,Void,Void> {
        Context contxt;
        int mflag;
        String searchtext;

        public HomeListAsync(Activity activity, int flag) {
            contxt=activity;
            mflag=flag;
        }
        public HomeListAsync(Activity activity, int flag, String text) {
            contxt=activity;
            mflag=flag;
            searchtext = text;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (mflag == 1){
            try {
                pbAL = dbh.getAllDetails();
            }catch (Exception e){
                pbAL=null;
            }

            }else if (mflag == 2){
                filterAl=new ArrayList<>();
                if (pbAL.get(0).getFlag().equalsIgnoreCase("true")){
                for (int i=0;i<pbAL.size();i++){
                    if (pbAL.get(i).getRecipeName().toLowerCase().contains(searchtext.toLowerCase())|| pbAL.get(i).getRecipeType().toLowerCase().contains(searchtext.toLowerCase())){
                        filterAl.add(pbAL.get(i));
                    }
                  }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list_pb.setVisibility(View.VISIBLE);
            recipeRV.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            list_pb.setVisibility(View.GONE);

            if (mflag == 1) {

                if (pbAL == null) {
                    Toast.makeText(contxt, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                } else if (pbAL.get(0).getFlag().equalsIgnoreCase("false")) {
                    Toast.makeText(contxt, "No Data", Toast.LENGTH_SHORT).show();
                } else if (pbAL.get(0).getFlag().equalsIgnoreCase("true")) {
                    recipeRV.setVisibility(View.VISIBLE);
                    recipeRV.setAdapter(new HomeListAdapter(contxt, pbAL));
                    recipeRV.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
            }else if (mflag == 2){
                if (filterAl.size() == 0){
                    Toast.makeText(contxt, "Nothing Found", Toast.LENGTH_SHORT).show();
                }else {
                    recipeRV.setVisibility(View.VISIBLE);
                    recipeRV.setAdapter(new HomeListAdapter(contxt, filterAl));
                    recipeRV.setLayoutManager(new LinearLayoutManager(getActivity()));
                }

            }
        }
    }

}
