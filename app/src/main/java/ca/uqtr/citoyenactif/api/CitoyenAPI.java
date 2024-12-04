package ca.uqtr.citoyenactif.api;

import java.util.List;

import ca.uqtr.citoyenactif.api.dto.CitoyenDTO;
import ca.uqtr.citoyenactif.api.dto.ReportCreateDTO;
import ca.uqtr.citoyenactif.api.dto.ReportDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CitoyenAPI {
    @GET("/reports")
    Call<List<ReportDTO>> fetchReports();

    @POST("/reports/sync")
    Call<Void> syncReports(@Body List<ReportDTO> reports);

    @GET("/users")
    Call<List<CitoyenDTO>> fetchUsers();

    @POST("/users/sync")
    Call<Void> syncUsers(@Body List<CitoyenDTO> users);

    @POST("/register")
    Call<Void> register();

    @POST("/report")
    Call<Void> createReport(@Body ReportCreateDTO id);

}
