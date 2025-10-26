package com.group.mobileparkingchain.features.profile.presentation.view

import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.group.mobileparkingchain.features.profile.presentation.components.editProfile.EditableField
import com.group.mobileparkingchain.features.profile.presentation.components.editProfile.ErrorCard
import com.group.mobileparkingchain.features.profile.presentation.components.editProfile.ProfileImagePicker
import com.group.mobileparkingchain.features.profile.data.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userProfile: UserProfile,
    onBackClick: () -> Unit,
    onSaveChanges: (UserProfile) -> Unit
) {
    var firstName by remember { mutableStateOf(userProfile.firstName) }
    var lastName by remember { mutableStateOf(userProfile.lastName) }
    var email by remember { mutableStateOf(userProfile.email) }
    var phoneNumber by remember { mutableStateOf(userProfile.phoneNumber) }
    var currentImageUrl by remember { mutableStateOf(userProfile.profileImageUrl) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            errorMessage?.let { msg ->
                ErrorCard(message = msg, onDismiss = { errorMessage = null })
                Spacer(modifier = Modifier.height(24.dp))
            }

            ProfileImagePicker(
                imageUrl = currentImageUrl,
                onImageSelected = { currentImageUrl = it },
                showError = { errorMessage = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            EditableField("First Name", firstName, { firstName = it }, "Enter first name")
            Spacer(modifier = Modifier.height(16.dp))
            EditableField("Last Name", lastName, { lastName = it }, "Enter last name")
            Spacer(modifier = Modifier.height(16.dp))
            EditableField("Email", email, { email = it }, "Enter email")
            Spacer(modifier = Modifier.height(16.dp))
            EditableField("Phone Number", phoneNumber, { phoneNumber = it }, "Enter phone number")
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val updatedProfile = UserProfile(
                        firstName, lastName, email, phoneNumber, currentImageUrl
                    )
                    onSaveChanges(updatedProfile)
                    Toast.makeText(context, "Profile updated successfully ✅", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

