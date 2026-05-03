package com.example.anganwadiapp.presentation.auth.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

//val PrimaryBlue = Color(0xFF2563EB)
//val BgLight = Color(0xFFF8FAFC)
//val TextDark = Color(0xFF1E293B)
//val TextGray = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentLoginScreen(
    onLoginSuccess: (childId: String, centerId: String, childName: String) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ParentViewModel = hiltViewModel()
    val loginState by viewModel.loginState.collectAsState()

    var id by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf(TextFieldValue("")) }
    var dobError by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        if (loginState is ParentLoginState.Success) {
            val data = (loginState as ParentLoginState.Success).data
            val childId = data["childId"] as? String ?: ""
            val centerId = data["centerId"] as? String ?: ""
            val childName = data["name"] as? String ?: ""
            onLoginSuccess(childId, centerId, childName)
        }
    }

    fun formatDob(textFieldValue: TextFieldValue): TextFieldValue {
        val digits = textFieldValue.text.filter { it.isDigit() }.take(8)
        if (digits.isEmpty()) return TextFieldValue("")
        
        val builder = StringBuilder()
        var cursorPosition = textFieldValue.selection.start
        
        for (i in digits.indices) {
            builder.append(digits[i])
            if (builder.length == 2 || builder.length == 5) {
                builder.append("/")
                if (i < textFieldValue.selection.start) {
                    cursorPosition++
                }
            }
        }
        
        // Adjust cursor position
        val newCursorPos = minOf(cursorPosition, builder.length)
        
        return TextFieldValue(
            text = builder.toString(),
            selection = androidx.compose.ui.text.TextRange(newCursorPos)
        )
    }

    fun validateDob(date: String): Boolean {
        // Simple string format check for DD/MM/YYYY
        val parts = date.split("/")
        if (parts.size != 3) return false
        
        val dd = parts[0]
        val mm = parts[1]
        val yyyy = parts[2]
        
        // Check each part is exactly 2, 2, 4 digits
        return dd.length == 2 && mm.length == 2 && yyyy.length == 4 &&
                dd.all { it.isDigit() } && mm.all { it.isDigit() } && yyyy.all { it.isDigit() }
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Parent / Guardian Login", fontWeight = FontWeight.Bold, color = TextDark)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                Icons.Default.FamilyRestroom,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter Your Credentials",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Text(
                text = "Please provide your ID and date of birth to continue",
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = id,
                onValueChange = { 
                    id = it.filter { char -> char.isDigit() }.take(10)
                },
                label = { Text("Parent / Guardian ID") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dob,
                onValueChange = { newValue ->
                    dob = formatDob(newValue)
                    dobError = false
                },
                label = { Text("Date of Birth (DD/MM/YYYY)") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                placeholder = { Text("DD/MM/YYYY") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = dobError,
                supportingText = {
                    if (dobError) Text("Invalid date format. Use DD/MM/YYYY", color = MaterialTheme.colorScheme.error)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val dobText = dob.text
                    if (id.isNotEmpty() && validateDob(dobText)) {
                        viewModel.verifyCredentials(id, dobText)
                    } else {
                        dobError = !validateDob(dobText)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = loginState !is ParentLoginState.Loading
            ) {
                if (loginState is ParentLoginState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }

            if (loginState is ParentLoginState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (loginState as ParentLoginState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }
    }
}
