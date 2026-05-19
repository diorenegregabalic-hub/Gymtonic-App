package com.falyrion.gymtonicapp.network;

import com.falyrion.gymtonicapp.models.AuthResponse;
import com.falyrion.gymtonicapp.models.LoginRequest;
import com.falyrion.gymtonicapp.models.RegisterRequest;
import com.falyrion.gymtonicapp.models.WorkoutPlan;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiClient {

    @POST("api/auth/register")
    Call<AuthResponse> registerUser(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest request);

    @GET("api/workouts/plans/{user_id}")
    Call<List<WorkoutPlan>> getWorkoutPlans(@Path("user_id") int userId);
}
