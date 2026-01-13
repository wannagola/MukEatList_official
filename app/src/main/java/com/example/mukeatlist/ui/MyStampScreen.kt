package com.example.mukeatlist.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mukeatlist.R
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextAlign

// 데이터 클래스는 그대로 유지
data class StampInfo(
    val id: Int,
    val x: Float,
    val y: Float,
    val isCompleted: Boolean,
    val categoryId: String
)

@Composable
fun MyStampScreen(
    paddingValues: PaddingValues,
    visitCounts: Map<String, Int>,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    // [추가됨] 현재 선택된 스탬프를 저장하는 상태 (null이면 팝업 안 뜸)
    var selectedStamp by remember { mutableStateOf<StampInfo?>(null) }

    // ID와 방문 횟수 체크 로직
    fun checkCondition(id: Int, currentCount: Int): Boolean {
        return when (id) {
            in 1..3 -> currentCount >= 10
            in 4..6 -> currentCount >= 5
            in 7..9 -> currentCount >= 2
            in 10..12 -> currentCount >= 1
            else -> false
        }
    }

    // 데이터 생성
    val stampList = listOf(
        StampInfo(1, -0.71f, -0.34f, checkCondition(1, visitCounts["blackwhitechef"] ?: 0), "blackwhitechef"),
        StampInfo(2, -0.0f, -0.34f, checkCondition(2, visitCounts["michelin"] ?: 0), "michelin"),
        StampInfo(3, 0.72f, -0.34f, checkCondition(3, visitCounts["tzuyang"] ?: 0), "tzuyang"),
        StampInfo(4, -0.9f, -0.0f, checkCondition(4, visitCounts["blackwhitechef"] ?: 0), "blackwhitechef"),
        StampInfo(5, 0.15f, -0.0f, checkCondition(5, visitCounts["michelin"] ?: 0), "michelin"),
        StampInfo(6, 0.9f, -0.0f, checkCondition(6, visitCounts["tzuyang"] ?: 0), "tzuyang"),
        StampInfo(7, -0.9f, 0.35f, checkCondition(7, visitCounts["blackwhitechef"] ?: 0), "blackwhitechef"),
        StampInfo(8, -0.12f, 0.34f, checkCondition(8, visitCounts["michelin"] ?: 0), "michelin"),
        StampInfo(9, 0.87f, 0.35f, checkCondition(9, visitCounts["tzuyang"] ?: 0), "tzuyang"),
        StampInfo(10, -0.72f, 0.74f, checkCondition(10, visitCounts["blackwhitechef"] ?: 0), "blackwhitechef"),
        StampInfo(11, -0.0f, 0.74f, checkCondition(11, visitCounts["michelin"] ?: 0), "michelin"),
        StampInfo(12, 0.73f, 0.74f, checkCondition(12, visitCounts["tzuyang"] ?: 0), "tzuyang")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // 1층: 스크롤 영역
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Image(
                painter = painterResource(id = R.drawable.mystampscreen_background),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )

            stampList.forEach { stamp ->
                StampItem(
                    stamp = stamp,
                    modifier = Modifier.align(BiasAlignment(stamp.x, stamp.y)),
                    // [수정됨] 완료된 스탬프일 때만 클릭 시 상태 업데이트
                    onClick = {
                        if (stamp.isCompleted) {
                            selectedStamp = stamp
                        }
                    }
                )
            }
        }

        // 2층: 헤더
        TransparentStampHeader(
            modifier = Modifier.align(Alignment.TopCenter),
            onBackClick = onBackClick
        )
    }

    // [추가됨] 팝업 다이얼로그 표시
    if (selectedStamp != null) {
        StampDetailDialog(
            stamp = selectedStamp!!,
            onDismiss = { selectedStamp = null } // 닫기 처리
        )
    }
}

// ✨ 스탬프 상세 팝업 컴포넌트
@Composable
fun StampDetailDialog(
    stamp: StampInfo,
    onDismiss: () -> Unit
) {
    // 1. 카테고리 이름 매핑
    val categoryName = when (stamp.categoryId) {
        "blackwhitechef" -> "흑백요리사"
        "tzuyang" -> "쯔양"
        "michelin" -> "미슐랭"
        else -> "맛집"
    }

    // 2. 티어 이름 매핑 (ID 기반)
    val tierName = when (stamp.id) {
        in 1..3 -> "마스터"  // 보라
        in 4..6 -> "골드"    // 골드
        in 7..9 -> "실버"    // 실버
        else -> "브론즈"      // 10~12
    }

    val badgeDescription = when (stamp.id) {
        1 -> "흑백요리사 코스 완주.\n이제 요리는 작품으로 보임."
        2 -> "미슐랭 지도 들고 다녀도 될 레벨.\n별 수집가 최종 인증."
        3 -> "쯔양 맛집 코스 정복 완료.\n이제 나는 먹짱!"
        4 -> "핫한 트렌드 식당은\n이제 내 손바닥 안!"
        5 -> "별의 이유를 아는 사람.\n미슐랭 믿고 가도 됨."
        6 -> "내가 위만 컸어도 쯔양과 같이\n먹방했쯔양~>.<"
        7 -> "이건 그냥 맛이 아님.\n흑백요리사의 철학을 먹는 중."
        8 -> "이 집은 별 줄 만하네?\n미슐랭 감이 오기 시작함."
        9 -> "쯔양이 왜 이 집을 갔는지 아는 사람.\n실패 없는 맛집 감각."
        10 -> "TV에서 보던 그 셰프!\n요즘 핫한 흑백요리사 맛집 입장."
        11 -> "별 하나의 세계에 입문!\n미슐랭이 뭐길래… 일단 가봄."
        12 -> "먹방 유튜브계의 메시!\n쯔양이 방문한 맛집에서 먹쯔양~"
        else -> "화이팅"
    }
    val conditionText = when (stamp.id) {
        in 1..3 -> "누적 방문 10회 달성" // 마스터
        in 4..6 -> "누적 방문 5회 달성"  // 골드
        in 7..9 -> "누적 방문 2회 달성"  // 실버
        else -> "첫 방문 달성"           // 브론즈
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp), // 내부 여백
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 상단: 스탬프 모양 그대로 가져오기
                // (StampItem을 재사용하되 클릭 이벤트는 비활성화)
                StampItem(
                    stamp = stamp,
                    modifier = Modifier.size(80.dp), // 팝업이니까 조금 더 크게 (기본 56dp -> 80dp)
                    onClick = {} // 팝업 내에서는 클릭해도 아무 일 없음
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 하단: 제목 (카테고리 + 티어)
                Text(
                    text = "$categoryName $tierName",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // (선택) 닫기 안내 메시지
                Text(
                    text = badgeDescription,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp) // 구분선
                Spacer(modifier = Modifier.height(16.dp))

                // 조건 표시
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "획득 조건",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF660033) // 포인트 컬러 (자주색)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = conditionText,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
@Composable
fun TransparentStampHeader(
    modifier : Modifier = Modifier,
    onBackClick: () -> Unit
) {
    // Surface나 배경색 없이 Row만 배치하여 완전히 투명하게 만듭니다.
    // 만약 너무 배경이랑 섞여서 글씨가 안 보이면, 아래 Row modifier에
    // .background(Color.Black.copy(alpha = 0.3f)) 처럼 반투명 검은색을 깔아줄 수도 있습니다.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp) // 일반적인 앱바 높이
            //.padding(horizontal = 4.dp) // 좌우 여백
            .background(Color.DarkGray.copy(alpha = 0.7f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 뒤로 가기 버튼
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로 가기",
                // [중요] 배경이 지도이므로 아이콘이 잘 보이도록 흰색이나 밝은 색 추천
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
            )
        }

        Text(
            text = "마이페이지",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// 4. 스탬프 아이템 (그라데이션 적용 수정됨)
@Composable
fun StampItem(
    stamp: StampInfo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // 활성화/비활성화 아이콘 색상
    val activeIconColor = Color.White
    val inactiveIconColor = Color.Gray

    Box(
        modifier = modifier
            .size(56.dp)
            .border(width = 2.dp, color = Color.Black, shape = CircleShape)
            .clip(CircleShape)
            // [핵심 변경] 완료되었으면 ID에 맞는 그라데이션, 아니면 회색 단색 배경
            .background(
                if (stamp.isCompleted) getStampBrush(stamp.id)
                else SolidColor(Color.DarkGray) // Brush 타입 맞추기 위해 solidColor 사용
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (stamp.isCompleted) {
            // 완료됨: 원래 카테고리 아이콘 표시 (흰색 틴트)
            Icon(
                painter = painterResource(id = getCategoryIcon(stamp.categoryId)),
                contentDescription = "Completed Stamp icon",
                tint = Color.White,
                modifier = Modifier.fillMaxSize(0.75f)
            )
        } else {
            // 미완료: 잠금 아이콘 표시 (회색 틴트)
            Icon(
                imageVector = Icons.Default.Lock, // 기본 잠금 벡터 아이콘 사용
                contentDescription = "Locked Stamp icon",
                tint = Color.White,
                // 아이콘에 따라 크기 느낌이 다를 수 있어 약간 조절 (취향껏 변경)
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// [새로 추가된 함수] ID별 그라데이션 브러쉬 반환
@Composable
fun getStampBrush(id: Int): Brush {
    // 요청하신 색상 코드
    val colors = when (id) {
        in 1..3 -> listOf(Color(0xFFE056FD), Color(0xFF686DE0)) // 보라
        in 4..6 -> listOf(Color(0xFFFFD700), Color(0xFFDAA520)) // 골드
        in 7..9 -> listOf(Color(0xFFE1E1E1), Color(0xFF7F8C8D)) // 실버
        in 10..12 -> listOf(Color(0xFFCD7F32), Color(0xFF804A00)) // 브론즈
        else -> listOf(Color.Gray, Color.DarkGray) // 예외 처리1
    }

    // 위에서 아래로 떨어지는 그라데이션 (Vertical)
    return Brush.verticalGradient(colors)
}

// 5. 카테고리 아이콘 헬퍼 (기존 유지)
@Composable
fun getCategoryIcon(categoryId: String): Int {
    return when (categoryId) {
        "tzuyang" -> R.drawable.youtube_icon
        "michelin" -> R.drawable.michelin_icon
        "blackwhitechef" -> R.drawable.blackwhitechef_icon
        else -> android.R.drawable.btn_star_big_on
    }
}