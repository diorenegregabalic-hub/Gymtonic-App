package com.falyrion.gymtonicapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.falyrion.gymtonicapp.R;
import com.falyrion.gymtonicapp.models.AuthResponse;
import com.falyrion.gymtonicapp.models.WorkoutPlan;
import com.falyrion.gymtonicapp.network.RetrofitClient;
import com.falyrion.gymtonicapp.recyclerview.Adapter_Item_General_001;
import com.falyrion.gymtonicapp.recyclerview.Item_General_001;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Workout_EditPlans extends AppCompatActivity implements Adapter_Item_General_001.Interface_Item_Edit {

    private ArrayList<Item_General_001> plansList;
    private ArrayList<WorkoutPlan> serverPlansList; // Mag-gunit sa kompleto nga modelo gikan sa server
    private Adapter_Item_General_001 adapterWorkoutPlans;
    private int currentUserId; // Alang sa logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_editplans);

        // 1. KUHAON ANG USER ID GIKAN SA SHARED PREFERENCES (Gi-save kini pag-login)
        SharedPreferences sharedPreferences = getSharedPreferences("GymTonicPrefs", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("userId", -1);

        plansList = new ArrayList<>();
        serverPlansList = new ArrayList<>();

        adapterWorkoutPlans = new Adapter_Item_General_001(plansList, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPlans);
        recyclerView.setAdapter(adapterWorkoutPlans);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        // Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbarActivityEditPlans);
        toolbar.setTitle(getResources().getString(R.string.button_edit_plans));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 2. SIGUROHA NGA MAKA-LOAD KUNG NAAY SESSION
        if (currentUserId != -1) {
            loadPlansFromApi();
        } else {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
        }

        // Create Button
        Button buttonCreatePlan = findViewById(R.id.buttonCreatePlan);
        buttonCreatePlan.setOnClickListener(view -> showDialogCreatePlan());
    }

    /**
     * GET: Pagkuha sa listahan sa plans gikan sa API base sa user_id
     */
    private void loadPlansFromApi() {
        RetrofitClient.getClient().getWorkoutPlans(currentUserId).enqueue(new Callback<List<WorkoutPlan>>() {
            @Override
            public void onResponse(Call<List<WorkoutPlan>> call, Response<List<WorkoutPlan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    plansList.clear();
                    serverPlansList.clear();
                    serverPlansList.addAll(response.body());

                    // I-convert ang matag data ngadto sa string para sa RecyclerView adapter
                    for (WorkoutPlan plan : serverPlansList) {
                        plansList.add(new Item_General_001(plan.getPlanName()));
                    }
                    adapterWorkoutPlans.notifyDataSetChanged();
                } else {
                    Toast.makeText(Activity_Workout_EditPlans.this, "Failed to load plans.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WorkoutPlan>> call, Throwable t) {
                Log.e("RETROFIT_GET", t.getMessage());
                Toast.makeText(Activity_Workout_EditPlans.this, "Server connection failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * POST: Pag-add og bag-ong plan sa database pinaagi sa API
     */
    private void showDialogCreatePlan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edittext, null);
        builder.setView(view);

        EditText editTextPlanName = view.findViewById(R.id.dialogEditText);
        editTextPlanName.setHint("Enter plan name");

        builder.setTitle("Create New Workout Plan");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Create plan", (dialogInterface, i) -> {
            String text = editTextPlanName.getText().toString().trim();

            if (text.isEmpty()) return;

            // Gamiton ang constructor nga nagkinahanglan og user_id ug plan_name
            WorkoutPlan newPlan = new WorkoutPlan(currentUserId, text);

            RetrofitClient.getClient().createWorkoutPlan(newPlan).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(Activity_Workout_EditPlans.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        loadPlansFromApi(); // I-refresh ang list aron masulod ang bag-ong ID gikan sa MySQL
                    } else {
                        Toast.makeText(Activity_Workout_EditPlans.this, "Failed to create plan.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(Activity_Workout_EditPlans.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.create().show();
    }

    /**
     * PUT: Pag-update sa ngalan sa plan gamit ang ID niini
     */
    private void showDialogEditPlanName(String currentPlanName, int itemPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edittext, null);
        builder.setView(view);

        EditText editTextPlanName = view.findViewById(R.id.dialogEditText);
        editTextPlanName.setText(currentPlanName);

        builder.setTitle("Edit plan name");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Save", (dialogInterface, i) -> {
            String newPlanName = editTextPlanName.getText().toString().trim();

            if (newPlanName.isEmpty()) return;

            // Kuhaon ang ID sa plan gikan sa server database model arraylist
            int planId = serverPlansList.get(itemPosition).getId();
            WorkoutPlan updatedPlan = new WorkoutPlan(newPlanName);

            RetrofitClient.getClient().updateWorkoutPlan(planId, updatedPlan).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(Activity_Workout_EditPlans.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        // I-sync ang UI ug Local list aron dili na kinahanglan mag-load usab sa internet
                        plansList.get(itemPosition).setTitle(newPlanName);
                        serverPlansList.get(itemPosition).setPlanName(newPlanName);
                        adapterWorkoutPlans.notifyItemChanged(itemPosition);
                    } else {
                        Toast.makeText(Activity_Workout_EditPlans.this, "Update failed.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(Activity_Workout_EditPlans.this, "Network error updating plan.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.create().show();
    }

    private void showDialogError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogStyle);
        builder.setTitle("You must have at least 1 workout plan!");
        builder.setPositiveButton("Okay", null);
        builder.create().show();
    }

    // Interface list clicks

    @Override
    public void onItemClicked(int itemPosition) {
        showDialogEditPlanName(plansList.get(itemPosition).getTitle(), itemPosition);
    }

    /**
     * DELETE: Pag-delete sa plan gamit ang ID sa plan gikan sa database
     */
    @Override
    public void onButtonRemoveClicked(int itemPosition) {
        if (plansList.size() == 1) {
            showDialogError();
            return;
        }

        int planId = serverPlansList.get(itemPosition).getId();

        RetrofitClient.getClient().deleteWorkoutPlan(planId).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(Activity_Workout_EditPlans.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    // Kuhaon sa listahan ug i-update ang UI sa RecyclerView
                    plansList.remove(itemPosition);
                    serverPlansList.remove(itemPosition);
                    adapterWorkoutPlans.notifyItemRemoved(itemPosition);
                } else {
                    Toast.makeText(Activity_Workout_EditPlans.this, "Failed to delete plan.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(Activity_Workout_EditPlans.this, "Network error during delete.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}