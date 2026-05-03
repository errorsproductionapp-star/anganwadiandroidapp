package com.example.anganwadiapp.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.anganwadiapp.presentation.main.dashboard.DashboardViewModel

private val Sky50 = Color(0xFFF0F9FF)
private val Sky100 = Color(0xFFE0F2FE)
private val Sky400 = Color(0xFF38BDF8)
private val Sky600 = Color(0xFF0284C7)
private val Sky700 = Color(0xFF0369A1)
private val Gray50 = Color(0xFFF9FAFB)
private val Gray100 = Color(0xFFF3F4F6)
private val Gray200 = Color(0xFFE5E7EB)
private val Gray300 = Color(0xFFD1D5DB)
private val Gray400 = Color(0xFF9CA3AF)
private val Gray500 = Color(0xFF6B7280)
private val Gray700 = Color(0xFF374151)
private val Gray900 = Color(0xFF0F172A)
private val Green400 = Color(0xFF4ADE80)
private val Green50 = Color(0xFFF0FDF4)
private val Green100 = Color(0xFFDCFCE7)
private val Green600 = Color(0xFF16A34A)
private val Orange400 = Color(0xFFFB923C)
private val Orange50 = Color(0xFFFFF7ED)
private val Orange100 = Color(0xFFFFEDD5)
private val Purple400 = Color(0xFFC084FC)
private val Purple50 = Color(0xFFFAF5FF)
private val Purple100 = Color(0xFFF3E8FF)
private val Rose400 = Color(0xFFFB7185)
private val Rose50 = Color(0xFFFFF1F2)
private val Rose100 = Color(0xFFFFE4E6)
private val Indigo400 = Color(0xFF818CF8)
private val Indigo50 = Color(0xFFEEF2FF)
private val Indigo100 = Color(0xFFE0E7FF)

data class ReportCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val bgGradient: List<Color>,
    val tag: String
)

data class ReportSummary(
    val label: String,
    val value: String,
    val change: String,
    val isPositive: Boolean,
    val icon: ImageVector,
    val color: Color,
    val bg: Color
)

enum class ReportPeriod(val label: String) {
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    YEARLY("Yearly")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val anganwadiNumber by viewModel.anganwadiCenterId.collectAsStateWithLifecycle()
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.MONTHLY) }
    var selectedReportTag by remember { mutableStateOf<String?>(null) }

    val reports = listOf(
        ReportCard("Attendance Report", "Daily & monthly attendance trends", Icons.Default.CheckCircle, Green600, listOf(Green50, Green100), "attendance"),
        ReportCard("Enrollment Report", "New admissions & dropout analysis", Icons.Default.PersonAdd, Sky600, listOf(Sky50, Sky100), "enrollment"),
        ReportCard("Nutrition Report", "Meal consumption & nutrition data", Icons.Default.Restaurant, Orange400, listOf(Orange50, Orange100), "nutrition"),
        ReportCard("Stock Report", "Inventory usage & balance", Icons.Default.Inventory2, Purple400, listOf(Purple50, Purple100), "stock"),
        ReportCard("Health Report", "Growth tracking & health records", Icons.Default.Favorite, Rose400, listOf(Rose50, Rose100), "health"),
        ReportCard("Progress Report", "Student progress & ratings", Icons.AutoMirrored.Filled.TrendingUp, Indigo400, listOf(Indigo50, Indigo100), "progress")
    )

    val summaries = listOf(
        ReportSummary("Total Students", "128", "+12", true, Icons.Default.Groups, Sky600, Sky100),
        ReportSummary("Avg Attendance", "87%", "+3%", true, Icons.Default.CheckCircle, Green600, Green100),
        ReportSummary("Meals Served", "2,450", "+180", true, Icons.Default.Restaurant, Orange400, Orange100),
        ReportSummary("Stock Balance", "78%", "-5%", false, Icons.Default.Inventory2, Purple400, Purple100)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Sky600, Sky700)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Reports & Analytics",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Center #$anganwadiNumber",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Icon(
                            Icons.Default.Assessment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp).size(22.dp)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Period Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReportPeriod.values().forEach { period ->
                        PeriodChip(
                            label = period.label,
                            isSelected = period == selectedPeriod,
                            onClick = { selectedPeriod = period }
                        )
                    }
                }
            }

            // Summary Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    summaries.take(2).forEach { summary ->
                        SummaryCard(
                            summary = summary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(10.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    summaries.takeLast(2).forEach { summary ->
                        SummaryCard(
                            summary = summary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Reports Grid Header
            item {
                Spacer(Modifier.height(20.dp))
                Text(
                    "Report Categories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray900,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(4.dp))
            }

            // Report Cards Grid
            items(reports.chunked(2).size) { rowIndex ->
                val rowReports = reports.chunked(2)[rowIndex]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowReports.forEach { report ->
                        ReportCardItem(
                            report = report,
                            isSelected = selectedReportTag == report.tag,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedReportTag = if (selectedReportTag == report.tag) null else report.tag
                            }
                        )
                    }
                    if (rowReports.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }

            // Expanded Report Detail
            item {
                selectedReportTag?.let { tag ->
                    val report = reports.find { it.tag == tag }
                    if (report != null) {
                        Spacer(Modifier.height(12.dp))
                        ExpandedReportDetail(report = report, period = selectedPeriod)
                    }
                }
            }

            // Export Button
            item {
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Sky600
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Share", fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Sky600)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Export PDF", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Sky600 else Color.White,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Gray200) else null,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color.White else Gray500
            )
        }
    }
}

@Composable
private fun SummaryCard(
    summary: ReportSummary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(summary.bg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(summary.icon, contentDescription = null, tint = summary.color, modifier = Modifier.size(18.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (summary.isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = if (summary.isPositive) Green600 else Rose400,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        summary.change,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (summary.isPositive) Green600 else Rose400
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                summary.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )
            Text(
                summary.label,
                fontSize = 11.sp,
                color = Gray500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ReportCardItem(
    report: ReportCard,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = report.bgGradient + listOf(Color.White),
                        startY = 0f,
                        endY = 300f
                    )
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(report.bgGradient[0]),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(report.icon, contentDescription = null, tint = report.color, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(
                        report.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        report.subtitle,
                        fontSize = 11.sp,
                        color = Gray500,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandedReportDetail(
    report: ReportCard,
    period: ReportPeriod
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(report.bgGradient[0]),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(report.icon, contentDescription = null, tint = report.color, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(report.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Gray900)
                        Text("${period.label} Report", fontSize = 12.sp, color = Gray500)
                    }
                }
                Icon(Icons.Default.Close, contentDescription = null, tint = Gray400, modifier = Modifier
                    .size(24.dp)
                    .clickable { /* handled by parent */ })
            }

            Spacer(Modifier.height(16.dp))

            // Placeholder data rows
            val dataRows = when (report.tag) {
                "attendance" -> listOf(
                    "Avg Daily Attendance" to "87%",
                    "Total Present Days" to "22/25",
                    "Highest Attendance" to "96% (Mon)",
                    "Lowest Attendance" to "72% (Sat)"
                )
                "enrollment" -> listOf(
                    "Total Enrolled" to "128",
                    "New Admissions" to "+12",
                    "Dropouts" to "3",
                    "Active Students" to "125"
                )
                "nutrition" -> listOf(
                    "Meals Served" to "2,450",
                    "Avg Daily Meals" to "110",
                    "Compliance Rate" to "94%",
                    "Supplement Given" to "128 children"
                )
                "stock" -> listOf(
                    "Total Items Received" to "45 units",
                    "Total Items Utilized" to "35 units",
                    "Current Balance" to "78%",
                    "Pending Orders" to "2"
                )
                "health" -> listOf(
                    "Health Check-ups" to "128",
                    "Growth Monitored" to "95%",
                    "Immunization Complete" to "89%",
                    "Referrals Made" to "5"
                )
                "progress" -> listOf(
                    "Students Rated" to "120",
                    "Above Average" to "45%",
                    "Average" to "40%",
                    "Needs Support" to "15%"
                )
                else -> listOf("Data" to "N/A")
            }

            dataRows.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, fontSize = 13.sp, color = Gray700)
                    Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Gray900)
                }
                if (index < dataRows.size - 1) {
                    Divider(color = Gray100)
                }
            }
        }
    }
}
