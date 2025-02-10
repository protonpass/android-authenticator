package proton.android.authenticator.ui.home.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import proton.android.authenticator.domain.Entry

@Composable
fun EntryItem(modifier: Modifier = Modifier, entry: Entry) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID: ${entry.id}")
            Text("Model: ${entry.model}")
            Text("Created At: ${entry.createdAt}")
            Text("Modified At: ${entry.modifiedAt}")
        }
    }
}
