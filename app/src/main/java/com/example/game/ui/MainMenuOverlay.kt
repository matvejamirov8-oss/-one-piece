package com.example.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.game.sprites.ChopperSpriteSheet
import com.example.game.sprites.LuffySpriteSheet
import com.example.game.sprites.NamiSpriteSheet
import com.example.game.sprites.SanjiSpriteSheet
import com.example.game.sprites.UsoppSpriteSheet
import com.example.game.sprites.ZoroSpriteSheet
import com.example.ui.theme.HudTextLight
import com.example.ui.theme.HudTextMuted
import com.example.ui.theme.LuffyRed
import com.example.ui.theme.PixelInk
import com.example.ui.theme.PixelPanelBg
import com.example.ui.theme.PixelPanelDark
import com.example.ui.theme.StrawGold
import com.example.ui.theme.SurfCyan

private val MenuSunsetAccent = Color(0xFFFF7A2F)
private val MenuZoroGreen = Color(0xFF38C958)
private val MenuNamiCyan = Color(0xFF5CE1FF)
private val MenuUsoppOlive = Color(0xFFE6C24C)
private val MenuChopperPink = Color(0xFFFF78B4)

@Composable
fun MainMenuOverlay(
    luffySprites: LuffySpriteSheet,
    zoroSprites: ZoroSpriteSheet,
    sanjiSprites: SanjiSpriteSheet,
    namiSprites: NamiSpriteSheet,
    usoppSprites: UsoppSpriteSheet,
    chopperSprites: ChopperSpriteSheet,
    compact: Boolean,
    onSelectLevel: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA0C0A16))
            .displayCutoutPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxWidth()
                .background(PixelPanelDark.copy(alpha = 0.96f))
                .border(3.dp, PixelInk)
                .padding(3.dp)
                .border(2.dp, StrawGold)
                .padding(horizontal = if (compact) 14.dp else 22.dp, vertical = if (compact) 14.dp else 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 12.dp)
        ) {
            Text(
                text = stringResource(R.string.menu_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = if (compact) 18.sp else 22.sp
                ),
                color = StrawGold,
                textAlign = TextAlign.Center
            )

            // Playable 6-Hero Straw Hat Crew Showcase (Luffy, Zoro, Sanji, Nami, Usopp, Chopper)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuHeroPortrait(luffySprites.hudPortrait, StrawGold, Color(0xFF19589E))
                MenuHeroPortrait(zoroSprites.hudPortrait, MenuZoroGreen, Color(0xFF14422A))
                MenuHeroPortrait(sanjiSprites.hudPortrait, MenuSunsetAccent, Color(0xFF1A2238))
                MenuHeroPortrait(namiSprites.hudPortrait, MenuNamiCyan, Color(0xFF163D6B))
                MenuHeroPortrait(usoppSprites.hudPortrait, MenuUsoppOlive, Color(0xFF3A2B18))
                MenuHeroPortrait(chopperSprites.hudPortrait, MenuChopperPink, Color(0xFF4D1E3B))
            }

            Text(
                text = stringResource(R.string.menu_heroes_line),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = if (compact) 8.sp else 9.sp
                ),
                color = HudTextMuted,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.menu_subtitle),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = if (compact) 10.sp else 11.sp
                ),
                color = HudTextLight
            )

            // Side-by-side Level Select Cards for Landscape Play
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LevelSelectCard(
                    title = stringResource(R.string.menu_level1_name),
                    subtitle = stringResource(R.string.menu_level1_desc),
                    badge = "1",
                    accentColor = SurfCyan,
                    bgTint = Color(0xFF132D4F),
                    compact = compact,
                    testTag = "menu_level_1_button",
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectLevel(1) }
                )
                LevelSelectCard(
                    title = stringResource(R.string.menu_level2_name),
                    subtitle = stringResource(R.string.menu_level2_desc),
                    badge = "2",
                    accentColor = MenuSunsetAccent,
                    bgTint = Color(0xFF3E1828),
                    compact = compact,
                    testTag = "menu_level_2_button",
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectLevel(2) }
                )
                LevelSelectCard(
                    title = stringResource(R.string.menu_level3_name),
                    subtitle = stringResource(R.string.menu_level3_desc),
                    badge = "3",
                    accentColor = Color(0xFF47DBFF),
                    bgTint = Color(0xFF10314A),
                    compact = compact,
                    testTag = "menu_level_3_button",
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectLevel(3) }
                )
            }
        }
    }
}

@Composable
private fun MenuHeroPortrait(
    bitmap: ImageBitmap,
    borderColor: Color,
    bgColor: Color
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(bgColor)
            .border(1.5.dp, borderColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(30.dp)) {
            drawImage(
                image = bitmap,
                dstOffset = IntOffset.Zero,
                dstSize = IntSize(size.width.toInt().coerceAtLeast(1), size.height.toInt().coerceAtLeast(1)),
                filterQuality = FilterQuality.None
            )
        }
    }
}

@Composable
private fun LevelSelectCard(
    title: String,
    subtitle: String,
    badge: String,
    accentColor: Color,
    bgTint: Color,
    compact: Boolean,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .background(bgTint)
            .border(2.dp, PixelInk)
            .padding(2.dp)
            .border(1.5.dp, accentColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(accentColor)
                    .border(1.dp, PixelInk)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "LVL $badge",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = PixelInk
                )
            }
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = if (compact) 9.sp else 11.sp
            ),
            color = StrawGold,
            maxLines = 1
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = if (compact) 8.sp else 10.sp
            ),
            color = HudTextLight,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PixelPanelBg)
                .border(1.dp, LuffyRed.copy(alpha = 0.65f))
                .padding(vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ИГРАТЬ",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = accentColor
            )
        }
    }
}
