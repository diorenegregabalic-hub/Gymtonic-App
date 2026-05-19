package com.falyrion.gymtonicapp.network;

import com.falyrion.gymtonicapp.models.AuthResponse;
import com.falyrion.gymtonicapp.models.LoginRequest;
import com.falyrion.gymtonicapp.models.RegisterRequest;
import com.falyrion.gymtonicapp.models.WorkoutPlan;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiClient {

    @POST("api/auth/register")
    Call<AuthResponse> registerUser(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest request);

    @GET("api/workouts/plans/{user_id}")
    Call<List<WorkoutPlan>> getWorkoutPlans(@Path("user_id") int userId);

    @POST("api/workouts/plans")
    Call<AuthResponse> createWorkoutPlan(@Body WorkoutPlan plan); // Pwede AuthResponse o GenericResponse ang gamiton basta naay success ug message

    @PUT("api/workouts/plans/{id}")
    Call<AuthResponse> updateWorkoutPlan(@Path("id") int id, @Body WorkoutPlan plan);

    @DELETE("api/workouts/plans/{id}")
    Call<AuthResponse> deleteWorkoutPlan(@Path("id") int id);
}
