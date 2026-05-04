package com.example.anganwadiapp.presentation.health

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.model.Gender

private val Rose400 = Color(0xFFFB7185)
private val Rose500 = Color(0xFFF43F5E)
private val Gray50 = Color(0xFFF9FAFB)
private val Gray100 = Color(0xFFF3F4F6)
private val Gray200 = Color(0xFFE5E7EB)
private val Gray300 = Color(0xFFD1D5DB)
private val Gray400 = Color(0xFF9CA3AF)
private val Gray500 = Color(0xFF6B7280)
private val Gray700 = Color(0xFF374151)
private val Gray900 = Color(0xFF0F172A)
private val Sky400 = Color(0xFF38BDF8)
private val Sky600 = Color(0xFF0284C7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordScreen(
    onBack: () -> Unit = {},
    onStudentClick: (Child) -> Unit = {},
    viewModel: HealthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredStudents = state.students.filter { student ->
        searchQuery.isBlank() || student.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        Text(
            "${state.students.size} students",
            fontSize = 13.sp,
            color = Gray500,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name...", color = Gray400) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Gray400) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Rose400,
                        unfocusedBorderColor = Gray200
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            if (state.isLoading && state.students.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Rose400, strokeWidth = 3.dp)
                            Spacer(Modifier.height(12.dp))
                            Text("Loading students...", color = Gray500, fontSize = 14.sp)
                        }
                    }
                }
            } else if (filteredStudents.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, tint = Gray300, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No students found", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Gray500)
                    }
                }
            } else {
                items(filteredStudents, key = { it.id }) { student ->
                    StudentHealthCard(
                        student = student,
                        onClick = { onStudentClick(student) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun StudentHealthCard(
    student: Child,
    onClick: () -> Unit
) {
    val genderColor = when (student.gender) {
        Gender.MALE -> Sky600
        Gender.FEMALE -> Color(0xFFEC4899)
        Gender.OTHER -> Color(0xFF8B5CF6)
    }

    val avatarGradient = when (student.gender) {
        Gender.MALE -> listOf(Sky400, Sky600)
        Gender.FEMALE -> listOf(Color(0xFFF472B6), Color(0xFFEC4899))
        Gender.OTHER -> listOf(Color(0xFFA78BFA), Color(0xFF8B5CF6))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(avatarGradient))
                    .border(2.dp, genderColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    student.name.take(2).uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    student.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (student.age.isNotEmpty()) {
                        Text("Age: ${student.age}", fontSize = 11.sp, color = Gray500)
                    }
                    if (student.bloodGroup.isNotEmpty()) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Rose400.copy(alpha = 0.12f)) {
                            Text(
                                student.bloodGroup,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Rose500,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Gray400,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
