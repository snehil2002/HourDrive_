package com.example.hourdrive.screens.screencomponents


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    searchQuery: MutableState<String>,
    placeholder: String,
    onSearchQueryChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchQuery.value,
        onValueChange = {searchQuery.value=it},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (searchQuery.value.isNotEmpty()) {
                IconButton(onClick = { searchQuery.value="" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        placeholder = { Text(text = placeholder) },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFEBEDED),
            focusedContainerColor = Color(0xFFEBEDED),
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { }),
        shape = RoundedCornerShape(12.dp)
    )
}

