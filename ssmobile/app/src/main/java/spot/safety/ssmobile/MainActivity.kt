package spot.safety.ssmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import spot.safety.ssmobile.ui.SafetySpotApp
import spot.safety.ssmobile.ui.theme.SsmobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SsmobileTheme {
                SafetySpotApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    SsmobileTheme {
        SafetySpotApp()
    }
}
