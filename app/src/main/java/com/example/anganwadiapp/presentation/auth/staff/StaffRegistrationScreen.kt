package com.example.anganwadiapp.presentation.auth.staff

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.model.Staff
import kotlin.math.cos
import kotlin.math.sin

// ─── Design tokens (shared with StaffLoginScreen) ─────────────────────────────

private val Navy900    = Color(0xFF0A0F1E)
private val Navy800    = Color(0xFF0D1526)
private val Blue500    = Color(0xFF0056D2)
private val Blue400    = Color(0xFF1E6FE8)
private val Blue300    = Color(0xFF4D94FF)
private val Slate600   = Color(0xFF64748B)
private val Slate400   = Color(0xFF94A3B8)
private val White      = Color(0xFFFFFFFF)

// ─── Animated orb background (same helper as login) ──────────────────────────

private fun DrawScope.drawMeshBackground(animValue: Float) {
    drawRect(brush = Brush.verticalGradient(listOf(Navy900, Navy800)))
    val orb1X = size.width * (0.78f + 0.05f * cos(animValue))
    val orb1Y = size.height * (0.12f + 0.04f * sin(animValue * 0.7f))
    drawCircle(
        brush  = Brush.radialGradient(
            listOf(Blue500.copy(alpha = 0.30f), Color.Transparent),
            center = Offset(orb1X, orb1Y), radius = size.width * 0.55f
        ),
        radius = size.width * 0.55f, center = Offset(orb1X, orb1Y)
    )
    val orb2X = size.width * (0.18f + 0.04f * sin(animValue * 0.5f))
    val orb2Y = size.height * (0.75f + 0.05f * cos(animValue * 0.8f))
    drawCircle(
        brush  = Brush.radialGradient(
            listOf(Blue300.copy(alpha = 0.15f), Color.Transparent),
            center = Offset(orb2X, orb2Y), radius = size.width * 0.5f
        ),
        radius = size.width * 0.5f, center = Offset(orb2X, orb2Y)
    )
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun StaffRegistrationScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: StaffViewModel = hiltViewModel()
) {
    var name     by remember { mutableStateOf("") }
    var mobile   by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var centerId by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val registrationState by viewModel.registrationState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(registrationState) {
        if (registrationState is Result.Success) {
            Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
            viewModel.resetRegistrationState()
        } else if (registrationState is Result.Error) {
            Toast.makeText(context, (registrationState as Result.Error).message, Toast.LENGTH_LONG).show()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val bgAnim by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(16000, easing = LinearEasing)),
        label         = "orb"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { drawMeshBackground(bgAnim) }
    ) {
        // ── Back button ─────────────────────────────────────────────────────
        IconButton(
            onClick  = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 8.dp, start = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.08f))
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            // ── Badge + headline ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(Blue400, Blue500))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = White, modifier = Modifier.size(30.dp))
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Create Account",
                fontSize   = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = White
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Set up your staff profile",
                fontSize = 14.sp,
                color    = Slate400
            )

            Spacer(Modifier.height(32.dp))

            // ── Step indicator (visual only) ─────────────────────────────────
            StepIndicator(currentStep = 1, totalSteps = 2)

            Spacer(Modifier.height(24.dp))

            // ── Form card ────────────────────────────────────────────────────
            Surface(
                shape    = RoundedCornerShape(24.dp),
                color    = White.copy(alpha = 0.06f),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(White.copy(0.18f), White.copy(0.04f))
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Section label
                    SectionLabel("Personal Details")

                    RegField(
                        value         = name,
                        onValueChange = { name = it },
                        label         = "Full Name",
                        icon          = Icons.Default.Person,
                        keyboardType  = KeyboardType.Text
                    )

                    RegField(
                        value         = mobile,
                        onValueChange = { mobile = it },
                        label         = "Mobile Number",
                        icon          = Icons.Default.Phone,
                        keyboardType  = KeyboardType.Phone
                    )

                    RegField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = "Email Address",
                        icon          = Icons.Default.Email,
                        keyboardType  = KeyboardType.Email,
                        isOptional    = true
                    )

                    Spacer(Modifier.height(4.dp))
                    SectionLabel("Security")

                    RegField(
                        value           = password,
                        onValueChange   = { password = it },
                        label           = "Password",
                        icon            = Icons.Default.Lock,
                        keyboardType    = KeyboardType.Password,
                        isPassword      = true,
                        passwordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible }
                    )

                    Spacer(Modifier.height(4.dp))
                    SectionLabel("Centre Details")

                    RegField(
                        value         = centerId,
                        onValueChange = { centerId = it },
                        label         = "Anganwadi Centre ID",
                        icon          = Icons.Default.Business,
                        keyboardType  = KeyboardType.Number,
                        hint          = "Link your profile to your specific centre"
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── CTA button ───────────────────────────────────────────────────
            RegisterButton(
                isLoading = registrationState is Result.Loading,
                enabled   = registrationState !is Result.Loading,
                onClick   = {
                    if (name.isBlank() || mobile.isBlank() || password.isBlank() || centerId.isBlank()) {
                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.register(
                            Staff(
                                id                = "",
                                name              = name,
                                mobileNumber      = mobile,
                                email             = email,
                                anganwadiCenterId = centerId
                            ),
                            password
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Already have account
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Already registered?", fontSize = 13.sp, color = Slate400)
                Spacer(Modifier.width(4.dp))
                Text(
                    "Sign in",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Blue300,
                    modifier   = Modifier.clickable(onClick = onNavigateToLogin)
                )
            }

            Spacer(Modifier.height(32.dp))
            Text("Anganwadi Connect · Staff Access", fontSize = 12.sp, color = Slate600)
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Step indicator ───────────────────────────────────────────────────────────

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val active = index + 1 == currentStep
            val done   = index + 1 < currentStep
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(if (active) 28.dp else 16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            active -> Blue400
                            done   -> Blue500.copy(alpha = 0.5f)
                            else   -> White.copy(alpha = 0.15f)
                        }
                    )
            )
        }
    }
}

// ─── Section label ────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(3.dp, 14.dp)
                .clip(CircleShape)
                .background(Blue400)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text       = text.uppercase(),
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            color      = Slate400,
            letterSpacing = 1.2.sp
        )
    }
}

// ─── Registration field ───────────────────────────────────────────────────────

@Composable
private fun RegField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType   = KeyboardType.Text,
    isOptional: Boolean          = false,
    isPassword: Boolean          = false,
    passwordVisible: Boolean     = false,
    onTogglePassword: (() -> Unit)? = null,
    hint: String?                = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue   = if (isFocused) Blue400 else White.copy(alpha = 0.12f),
        animationSpec = tween(200),
        label         = "border"
    )
    val labelColor by animateColorAsState(
        targetValue   = if (isFocused) Blue300 else Slate400,
        animationSpec = tween(200),
        label         = "label"
    )

    Column {
        // Label row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = labelColor)
//            if (isOptional) {
//                Text(
//                    "Optional",
//                    fontSize = 10.sp,
//                    color    = Slate600,
//                    fontWeight = FontWeight.Medium
//                )
//            }
        }

        // Input row
        val visualTransform = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None

        BasicTextField(
            value             = value,
            onValueChange     = onValueChange,
            interactionSource = interactionSource,
            singleLine        = true,
            cursorBrush       = SolidColor(Blue300),
            keyboardOptions   = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransform,
            textStyle         = TextStyle(color = White, fontSize = 15.sp),
            modifier          = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(White.copy(alpha = 0.07f))
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
        ) { inner ->
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = Slate400, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(label, color = Slate600, fontSize = 14.sp)
                    }
                    inner()
                }
                if (isPassword && onTogglePassword != null) {
                    IconButton(onClick = onTogglePassword, modifier = Modifier.size(20.dp)) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            null,
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Hint text
        if (hint != null) {
            Spacer(Modifier.height(5.dp))
            Text(
                hint,
                fontSize = 11.sp,
                color    = Slate600,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

// ─── Register button ──────────────────────────────────────────────────────────

@Composable
private fun RegisterButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (enabled)
                    Brush.horizontalGradient(listOf(Blue400, Blue500))
                else
                    Brush.horizontalGradient(listOf(Slate600, Slate600))
            )
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = isLoading, label = "btn") { loading ->
            if (loading) {
                CircularProgressIndicator(
                    color       = White,
                    modifier    = Modifier.size(22.dp),
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.HowToReg,
                        null,
                        tint     = White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Save Profile & Register",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = White
                    )
                }
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
fun StaffRegistrationPreview() {
    StaffRegistrationScreen(
        onRegisterSuccess = {},
        onBack = {},
        onNavigateToLogin = {}
    )
}