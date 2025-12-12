package com.group.mobileparkingchain.features.profile.domain.usecase

import android.content.Context
import android.net.Uri
import com.group.mobileparkingchain.features.auth.domain.model.User
import com.group.mobileparkingchain.features.profile.domain.repository.IProfileRepository
import com.group.mobileparkingchain.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UploadProfileImageUseCase(
    private val profileRepository: IProfileRepository,
    private val context: Context
) {
    suspend operator fun invoke(imageUri: Uri): Result<User> = withContext(Dispatchers.IO) {
        // Convert to base64
        val base64Image = ImageUtils.uriToBase64(context, imageUri)
            ?: return@withContext Result.failure(Exception("Failed to convert image to base64"))
        
        // Upload using base64
        (profileRepository as? com.group.mobileparkingchain.features.profile.data.repository.ProfileRepositoryImpl)
            ?.uploadProfileImageBase64(base64Image)
            ?: Result.failure(Exception("Repository not available"))
    }
}
