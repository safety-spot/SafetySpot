package spot.safety.ssmobile.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spot.safety.ssmobile.ui.theme.BrandBlue
import spot.safety.ssmobile.ui.theme.BrandCyan
import spot.safety.ssmobile.ui.theme.BrandGreen
import spot.safety.ssmobile.ui.theme.CardBorder
import spot.safety.ssmobile.ui.theme.MutedText
import spot.safety.ssmobile.ui.theme.SsmobileTheme
import spot.safety.ssmobile.ui.theme.TrafficRed

private enum class AuthMode {
    ACTIONS,
    LOGIN,
    REGISTER
}

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel? = null,
    onAuthenticated: () -> Unit = {}
) {
    val uiState by (authViewModel?.uiState ?: MutableStateFlow(AuthUiState())).collectAsState()
    var mode by rememberSaveable { mutableStateOf(AuthMode.ACTIONS) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var schoolName by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    // Navigate when authenticated via ViewModel
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) onAuthenticated()
    }

    // Show API errors in the form
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            errorMessage = uiState.error
            authViewModel?.clearError()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEAF7FF), Color(0xFFEFFBF1))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(44.dp))
        SafetySpotLogo()
        Spacer(modifier = Modifier.height(24.dp))
        BrandTitle()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Lern. Sicher. Stark.",
            color = MutedText,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(70.dp))
        when (mode) {
            AuthMode.ACTIONS -> AuthActions(
                onLoginClick = {
                    errorMessage = null
                    mode = AuthMode.LOGIN
                },
                onRegisterClick = {
                    errorMessage = null
                    mode = AuthMode.REGISTER
                },
                onGuestClick = onAuthenticated
            )

            AuthMode.LOGIN -> AuthForm(
                title = "Anmelden",
                primaryLabel = if (uiState.isLoading) "..." else "Anmelden",
                username = username,
                password = password,
                errorMessage = errorMessage,
                isLoading = uiState.isLoading,
                onUsernameChange = {
                    username = it
                    errorMessage = null
                },
                onPasswordChange = {
                    password = it
                    errorMessage = null
                },
                onPrimaryClick = {
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Bitte Benutzername und Passwort eingeben."
                    } else if (authViewModel != null) {
                        errorMessage = null
                        authViewModel.login(username, password)
                    } else {
                        onAuthenticated()
                    }
                },
                onBackClick = {
                    errorMessage = null
                    mode = AuthMode.ACTIONS
                }
            )

            AuthMode.REGISTER -> AuthForm(
                title = "Registrieren",
                primaryLabel = if (uiState.isLoading) "..." else "Registrieren",
                username = username,
                password = password,
                schoolName = schoolName,
                errorMessage = errorMessage,
                isLoading = uiState.isLoading,
                showSchoolName = true,
                onUsernameChange = {
                    username = it
                    errorMessage = null
                },
                onPasswordChange = {
                    password = it
                    errorMessage = null
                },
                onSchoolNameChange = {
                    schoolName = it
                    errorMessage = null
                },
                onPrimaryClick = {
                    if (username.isBlank() || password.length < 4 || schoolName.isBlank()) {
                        errorMessage = "Bitte alle Felder ausfullen (mind. 4 Zeichen Passwort)."
                    } else if (authViewModel != null) {
                        errorMessage = null
                        authViewModel.register(username, password, schoolName)
                    } else {
                        onAuthenticated()
                    }
                },
                onBackClick = {
                    errorMessage = null
                    mode = AuthMode.ACTIONS
                }
            )
        }
    }
}

@Composable
private fun SafetySpotLogo() {
    Box(
        modifier = Modifier
            .size(118.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(BrandCyan, BrandGreen)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White.copy(alpha = 0.90f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "SS", color = BrandBlue, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun BrandTitle() {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = BrandBlue, fontWeight = FontWeight.Bold)) {
                append("Safety")
            }
            withStyle(SpanStyle(color = BrandGreen, fontWeight = FontWeight.Bold)) {
                append("Spot")
            }
        },
        style = MaterialTheme.typography.displaySmall
    )
}

@Composable
private fun AuthActions(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(99.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
        ) {
            Text(text = "Anmelden", style = MaterialTheme.typography.labelLarge)
        }
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(99.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = BrandGreen
            )
        ) {
            Text(text = "Registrieren", style = MaterialTheme.typography.labelLarge)
        }
        Text(
            text = "Als Gast ausprobieren",
            color = BrandBlue,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(99.dp))
                .clickable(onClick = onGuestClick)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun AuthForm(
    title: String,
    primaryLabel: String,
    username: String,
    password: String,
    errorMessage: String?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPrimaryClick: () -> Unit,
    onBackClick: () -> Unit,
    schoolName: String = "",
    showSchoolName: Boolean = false,
    isLoading: Boolean = false,
    onSchoolNameChange: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.92f))
            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = title, color = BrandBlue, style = MaterialTheme.typography.titleLarge)
        AuthTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = "Benutzername"
        )
        AuthTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Passwort",
            isPassword = true
        )
        if (showSchoolName) {
            AuthTextField(
                value = schoolName,
                onValueChange = onSchoolNameChange,
                label = "Schule"
            )
        }
        errorMessage?.let {
            Text(text = it, color = TrafficRed, style = MaterialTheme.typography.labelMedium)
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = BrandGreen
            )
        } else {
            Button(
                onClick = onPrimaryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text(text = primaryLabel, style = MaterialTheme.typography.labelLarge)
            }
        }
        Text(
            text = "Zurueck",
            color = BrandBlue,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(99.dp))
                .clickable(onClick = onBackClick)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandGreen,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    SsmobileTheme {
        AuthScreen()
    }
}
