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
        val imagePart = ImageUtils.uriToMultipartBodyPart(context, imageUri)
            ?: return@withContext Result.failure(Exception("Failed to prepare image for upload"))

        profileRepository.uploadProfileImage(imagePart)
    }
}
