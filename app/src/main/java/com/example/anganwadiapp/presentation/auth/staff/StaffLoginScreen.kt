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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anganwadiapp.core.common.Result
import kotlin.math.cos
import kotlin.math.sin

// ─── Design tokens ─────────────────────────────────────────────────────────────

private val Navy900   = Color(0xFF0A0F1E)
private val Navy800   = Color(0xFF0D1526)
private val Navy700   = Color(0xFF111D35)
private val Blue500   = Color(0xFF0056D2)
private val Blue400   = Color(0xFF1E6FE8)
private val Blue300   = Color(0xFF4D94FF)
private val Blue100   = Color(0xFFD6E8FF)
private val Slate600  = Color(0xFF64748B)
private val Slate400  = Color(0xFF94A3B8)
private val Slate200  = Color(0xFFE2E8F0)
private val White     = Color(0xFFFFFFFF)
private val ErrorRed  = Color(0xFFEF4444)

// ─── Animated mesh background ──────────────────────────────────────────────────

/** Draws a subtle dark-gradient background with two soft glowing orbs */
private fun DrawScope.drawMeshBackground(animValue: Float) {
    // Deep navy base
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Navy900, Navy800)
        )
    )
    // Orb 1 — blue, top-right drift
    val orb1X = size.width * (0.75f + 0.05f * cos(animValue))
    val orb1Y = size.height * (0.18f + 0.04f * sin(animValue * 0.7f))
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Blue500.copy(alpha = 0.35f), Color.Transparent),
            center = Offset(orb1X, orb1Y),
            radius = size.width * 0.55f
        ),
        radius = size.width * 0.55f,
        center = Offset(orb1X, orb1Y)
    )
    // Orb 2 — lighter, bottom-left drift
    val orb2X = size.width * (0.2f + 0.04f * sin(animValue * 0.5f))
    val orb2Y = size.height * (0.72f + 0.06f * cos(animValue * 0.8f))
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Blue300.copy(alpha = 0.18f), Color.Transparent),
            center = Offset(orb2X, orb2Y),
            radius = size.width * 0.5f
        ),
        radius = size.width * 0.5f,
        center = Offset(orb2X, orb2Y)
    )
}

// ─── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffLoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: StaffViewModel = hiltViewModel()
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(loginState) {
        if (loginState is Result.Success) {
            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
            viewModel.resetLoginState()
        } else if (loginState is Result.Error) {
            Toast.makeText(context, (loginState as Result.Error).message, Toast.LENGTH_LONG).show()
        }
    }

    // Background orb animation
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val bgAnim by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation  = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { drawMeshBackground(bgAnim) }
    ) {
        // ── Back button ──────────────────────────────────────────────────────
        IconButton(
            onClick  = onBack,
            modifier = Modifier
                .padding(top = 52.dp, start = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.08f))
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint               = White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Logo / Badge ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(listOf(Blue400, Blue500))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Lock,
                    contentDescription = null,
                    tint               = White,
                    modifier           = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Headline ────────────────────────────────────────────────────
            Text(
                text       = "Staff Portal",
                fontSize   = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text     = "Sign in to your workspace",
                fontSize = 14.sp,
                color    = Slate400
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Card panel ──────────────────────────────────────────────────
            Surface(
                shape  = RoundedCornerShape(24.dp),
                color  = White.copy(alpha = 0.06f),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(White.copy(alpha = 0.18f), White.copy(alpha = 0.04f))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Email field
                    SaasTextField(
                        value       = email,
                        onValueChange = { email = it },
                        label       = "Email address",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field
                    SaasTextField(
                        value         = password,
                        onValueChange = { password = it },
                        label         = "Password",
                        leadingIcon   = Icons.Default.Lock,
                        keyboardType  = KeyboardType.Password,
                        isPassword    = true,
                        passwordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Forgot password link
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = { }) {
                            Text(
                                text     = "Forgot password?",
                                fontSize = 13.sp,
                                color    = Blue300,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Login button
                    LoginButton(
                        isLoading = loginState is Result.Loading,
                        enabled   = loginState !is Result.Loading,
                        onClick   = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Please enter email and password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.login(email, password)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Footer note ─────────────────────────────────────────────────
            Text(
                text     = "Anganwadi Connect · Staff Access",
                fontSize = 12.sp,
                color    = Slate600
            )
        }
    }
}

// ─── SaaS-style text field ─────────────────────────────────────────────────────

@Composable
private fun SaasTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
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
        Text(
            text       = label,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color      = labelColor,
            modifier   = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        BasicSaasField(
            value            = value,
            onValueChange    = onValueChange,
            leadingIcon      = leadingIcon,
            keyboardType     = keyboardType,
            isPassword       = isPassword,
            passwordVisible  = passwordVisible,
            onTogglePassword = onTogglePassword,
            borderColor      = borderColor,
            interactionSource = interactionSource
        )
    }
}

@Composable
private fun BasicSaasField(
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean,
    passwordVisible: Boolean,
    onTogglePassword: (() -> Unit)?,
    borderColor: Color,
    interactionSource: MutableInteractionSource
) {
    val visualTransformation = if (isPassword && !passwordVisible)
        PasswordVisualTransformation() else VisualTransformation.None

    BasicTextField(
        value             = value,
        onValueChange     = onValueChange,
        interactionSource = interactionSource,
        singleLine        = true,
        keyboardOptions   = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        textStyle         = androidx.compose.ui.text.TextStyle(
            color      = White,
            fontSize   = 15.sp,
            fontWeight = FontWeight.Normal
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(White.copy(alpha = 0.07f))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
    ) { innerTextField ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = leadingIcon,
                contentDescription = null,
                tint               = Slate400,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text  = "Enter Credentials…",
                        color = Slate600,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
            if (isPassword && onTogglePassword != null) {
                IconButton(
                    onClick  = onTogglePassword,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide" else "Show",
                        tint = Slate400,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ─── Login button ──────────────────────────────────────────────────────────────

@Composable
private fun LoginButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue   = if (enabled) 1f else 0.97f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (enabled)
                    Brush.horizontalGradient(listOf(Blue400, Blue500))
                else
                    Brush.horizontalGradient(listOf(Slate600, Slate600))
            )
            .then(
                if (enabled)
                    Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = isLoading, label = "btnContent") { loading ->
            if (loading) {
                CircularProgressIndicator(
                    color    = White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text       = "Sign in",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = White
                )
            }
        }
    }
}

// ─── Alias for BasicTextField (avoids extra import confusion) ─────────────────

//private typealias BasicTextField = androidx.compose.foundation.text.BasicTextField

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun StaffLoginScreenPreview() {
    // Preview without ViewModel
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val bgAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
        label = "orb"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { drawMeshBackground(bgAnim) }
    ) {
        IconButton(
            onClick  = {},
            modifier = Modifier
                .padding(top = 52.dp, start = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.08f))
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(Blue400, Blue500))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lock, null, tint = White, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text("Staff Portal", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = White)
            Spacer(Modifier.height(6.dp))
            Text("Sign in to your workspace", fontSize = 14.sp, color = Slate400)
            Spacer(Modifier.height(40.dp))

            Surface(
                shape    = RoundedCornerShape(24.dp),
                color    = White.copy(alpha = 0.06f),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.verticalGradient(listOf(White.copy(0.18f), White.copy(0.04f))),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Column(Modifier.padding(24.dp, 28.dp)) {
                    SaasTextField(email, { email = it }, "Email address", Icons.Default.Email, KeyboardType.Email)
                    Spacer(Modifier.height(16.dp))
                    SaasTextField(
                        password, { password = it }, "Password", Icons.Default.Lock,
                        KeyboardType.Password, true, passwordVisible, { passwordVisible = !passwordVisible }
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                        TextButton({}) { Text("Forgot password?", fontSize = 13.sp, color = Blue300) }
                    }
                    Spacer(Modifier.height(8.dp))
                    LoginButton(false, true) {}
                }
            }
            Spacer(Modifier.height(28.dp))
            Text("Anganwadi Connect · Staff Access", fontSize = 12.sp, color = Slate600)
        }
    }
}