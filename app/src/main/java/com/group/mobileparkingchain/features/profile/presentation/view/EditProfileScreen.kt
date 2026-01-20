package com.group.mobileparkingchain.features.profile.presentation.view

import com.group.mobileparkingchain.core.Resource
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.group.mobileparkingchain.features.profile.presentation.components.editProfile.EditableField
import com.group.mobileparkingchain.features.profile.presentation.components.editProfile.ErrorCard
import com.group.mobileparkingchain.features.profile.presentation.components.editProfile.ProfileImagePicker
import com.group.mobileparkingchain.features.profile.data.UserProfile
import com.group.mobileparkingchain.features.profile.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userProfile: UserProfile,
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit
) {
    var firstName by remember { mutableStateOf(userProfile.firstName) }
    var lastName by remember { mutableStateOf(userProfile.lastName) }
    val email = userProfile.email // Read-only
    var phoneNumber by remember { mutableStateOf(userProfile.phoneNumber) }
    var currentImageUrl by remember { mutableStateOf(userProfile.profileImageUrl) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val updateProfileState by viewModel.updateProfileState.collectAsState()
    val uploadImageState by viewModel.uploadImageState.collectAsState()

    val cropLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            val resultUri = result.uriContent
            if (resultUri != null) {
                if (!com.group.mobileparkingchain.utils.ImageUtils.isImageSizeValid(context, resultUri)) {
                    errorMessage = "Image is too large. Please choose a smaller photo."
                    return@rememberLauncherForActivityResult
                }
                isUploading = true
                viewModel.uploadProfileImage(resultUri)
            } else {
                errorMessage = "Image crop failed. Please try again."
            }
        } else {
            errorMessage = result.error?.message ?: "Image crop failed. Please try again."
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val options = CropImageOptions().apply {
                guidelines = CropImageView.Guidelines.ON
                cropShape = CropImageView.CropShape.OVAL
                fixAspectRatio = true
                aspectRatioX = 1
                aspectRatioY = 1
                outputCompressFormat = Bitmap.CompressFormat.JPEG
                outputCompressQuality = 90
                activityTitle = "Crop Photo"
                toolbarColor = AndroidColor.BLACK
                activityMenuIconColor = AndroidColor.WHITE
                toolbarBackButtonColor = AndroidColor.WHITE
            }
            cropLauncher.launch(CropImageContractOptions(it, options))
        }
    }

    // Handle update profile state
    LaunchedEffect(updateProfileState) {
        when (updateProfileState) {
            is Resource.Success -> {
                val user = (updateProfileState as Resource.Success<com.group.mobileparkingchain.features.auth.domain.model.User>).data
                firstName = user.firstName
                lastName = user.lastName
                phoneNumber = user.phone
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateState()
            }
            is Resource.Error -> {
                errorMessage = (updateProfileState as Resource.Error).message
                viewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    // Handle upload image state
    LaunchedEffect(uploadImageState) {
        when (uploadImageState) {
            is Resource.Success -> {
                val user = (uploadImageState as Resource.Success<com.group.mobileparkingchain.features.auth.domain.model.User>).data
                currentImageUrl = com.group.mobileparkingchain.utils.ImageUtils.getFullImageUrl(user.profileImage)
                isUploading = false
                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                viewModel.resetUploadState()
            }
            is Resource.Error -> {
                errorMessage = (uploadImageState as Resource.Error).message
                isUploading = false
                viewModel.resetUploadState()
            }
            is Resource.Loading -> {
                isUploading = true
            }
            else -> {
                isUploading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontSize = ssp(20), fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(sdp(24)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                errorMessage?.let { msg ->
                    ErrorCard(message = msg, onDismiss = { errorMessage = null })
                    Spacer(modifier = Modifier.height(sdp(24)))
                }

                ProfileImagePicker(
                    imageUrl = currentImageUrl,
                    onImageSelected = {
                        // Trigger image picker
                        imagePickerLauncher.launch("image/*")
                    },
                    showError = { errorMessage = it }
                )

                if (isUploading) {
                    Spacer(modifier = Modifier.height(sdp(8)))
                    CircularProgressIndicator(
                        modifier = Modifier.padding(sdp(8)),
                        color = Color(0xFF4A90E2)
                    )
                }

                Spacer(modifier = Modifier.height(sdp(32)))

                EditableField("First Name", firstName, { firstName = it }, "Enter first name")
                Spacer(modifier = Modifier.height(sdp(16)))
                EditableField("Last Name", lastName, { lastName = it }, "Enter last name")
                Spacer(modifier = Modifier.height(sdp(16)))
                // Email is read-only
                EditableField("Email", email, {}, "Email", enabled = false)
                Spacer(modifier = Modifier.height(sdp(16)))
                EditableField("Phone Number", phoneNumber, { phoneNumber = it }, "Enter phone number")
                Spacer(modifier = Modifier.height(sdp(32)))

                val isUpdateLoading = updateProfileState is Resource.Loading

                Button(
                    onClick = {
                        // Validate inputs
                        when {
                            firstName.length < 2 || firstName.length > 50 -> {
                                errorMessage = "First name must be between 2 and 50 characters"
                            }
                            lastName.length < 2 || lastName.length > 50 -> {
                                errorMessage = "Last name must be between 2 and 50 characters"
                            }
                            phoneNumber.isNotBlank() && (phoneNumber.replace(Regex("[^0-9+]"), "").length < 9 
                                    || phoneNumber.replace(Regex("[^0-9+]"), "").length > 15) -> {
                                errorMessage = "Phone number must be between 9 and 15 digits"
                            }
                            else -> {
                                // Check if anything changed
                                val firstNameChanged = firstName != userProfile.firstName
                                val lastNameChanged = lastName != userProfile.lastName
                                val phoneChanged = phoneNumber != userProfile.phoneNumber
                                
                                if (firstNameChanged || lastNameChanged || phoneChanged) {
                                    viewModel.updateProfile(
                                        if (firstNameChanged) firstName else null,
                                        if (lastNameChanged) lastName else null,
                                        if (phoneChanged) phoneNumber.ifBlank { null } else null
                                    )
                                } else {
                                    Toast.makeText(context, "No changes to save", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(sdp(56)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(sdp(12)),
                    enabled = !isUpdateLoading && !isUploading
                ) {
                    if (isUpdateLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = sdp(8)),
                            color = Color.White
                        )
                    }
                    Text(
                        text = if (isUpdateLoading) "Saving..." else "Save Changes",
                        fontSize = ssp(16),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(sdp(24)))
            }
        }
    }
}
