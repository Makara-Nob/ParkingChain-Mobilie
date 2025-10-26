import com.group.mobileparkingchain.features.auth.data.remote.models.LoginData
import com.group.mobileparkingchain.features.auth.data.remote.models.LoginRequest
import com.group.mobileparkingchain.network.datastore.TokenDataStore
import java.io.IOException

class AuthRepository(
    private val authService: AuthService,
    private val tokenDataStore: TokenDataStore
) {

    suspend fun login(email: String, password: String): Result<LoginData> {
        return try {
            val response = authService.login(LoginRequest(email, password))

            if (response.success && response.data != null) {
                // Save token
                tokenDataStore.saveToken(response.data.token)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: IOException) {
            // Handle network errors
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
}