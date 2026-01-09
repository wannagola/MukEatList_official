package com.example.mukeatlist.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.mukeatlist.data.model.Restaurant

@Composable
fun RestaurantDetailDialog(
    restaurant: Restaurant,
    isVisited: Boolean,
    onVisitToggle: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier.height(200.dp)) {
                    AsyncImage(
                        model = restaurant.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Visit Badge Overlay (Optional visual cue)
                    if (isVisited) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xCCFFFFFF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "방문완료",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFFD32F2F),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = restaurant.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Visit Toggle Button
                        IconButton(onClick = onVisitToggle) {
                            Icon(
                                imageVector = if (isVisited) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                contentDescription = "방문 체크",
                                tint = if (isVisited) Color(0xFFD32F2F) else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "메인 메뉴: ${restaurant.mainMenu}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "주소: ${restaurant.address}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "태그",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                         // Simple tag display (taking first 3 for brevity if many)
                         restaurant.tags.take(4).forEach { tag ->
                             Surface(
                                 color = Color(0xFFEEEEEE),
                                 shape = RoundedCornerShape(4.dp),
                                 modifier = Modifier.padding(2.dp)
                             ) {
                                 Text(
                                     text = "#$tag",
                                     modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                     style = MaterialTheme.typography.labelSmall
                                 )
                             }
                         }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800020))
                    ) {
                        Text("닫기")
                    }
                }
            }
        }
    }
}
