
import com.group.mobileparkingchain.features.auth.data.remote.models.ApiResponse
import com.group.mobileparkingchain.features.auth.data.remote.models.LoginData
import com.group.mobileparkingchain.features.auth.data.remote.models.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>

}