package com.dergoogler.mmrl.ui.screens.repositories.screens.repository

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.model.online.OnlineModule
import com.dergoogler.mmrl.model.state.OnlineState
import com.dergoogler.mmrl.ui.component.Cover
import com.dergoogler.mmrl.ui.component.LabelItem
import com.dergoogler.mmrl.ui.component.TextWithIcon
import com.dergoogler.mmrl.ui.component.card.Card
import com.dergoogler.mmrl.ui.component.card.CardDefaults
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences
import com.dergoogler.mmrl.ext.fadingEdge
import com.dergoogler.mmrl.ext.isNotNullOrEmpty
import com.dergoogler.mmrl.ext.nullable
import com.dergoogler.mmrl.utils.toFormattedDateSafely

@Composable
fun ModuleItemDetailed(
    module: OnlineModule,
    state: OnlineState,
    alpha: Float = 1f,
    onClick: () -> Unit = {},
    decoration: TextDecoration = TextDecoration.None,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    val userPreferences = LocalUserPreferences.current
    val menu = userPreferences.repositoryMenu
    val hasLabel =
        (state.hasLicense && menu.showLicense) || state.installed || (module.track.hasAntifeatures && menu.showAntiFeatures)
    val isVerified = module.isVerified && menu.showVerified


    Card(
        enabled = enabled,
        modifier = {
            column = Modifier.padding(0.dp)
        },
        style = CardDefaults.cardStyle.copy(
            boxContentAlignment = Alignment.Center,
        ),
        onClick = onClick
    ) {
        module.cover.nullable(menu.showCover) {
            if (it.isNotEmpty()) {
                Cover(
                    modifier = Modifier.fadingEdge(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black,
                            ),
                            startY = Float.POSITIVE_INFINITY,
                            endY = 0f
                        ),
                    ),
                    url = it,
                )
            }
        }

        Row(
            modifier = Modifier.padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .alpha(alpha = alpha)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                TextWithIcon(
                    style = MaterialTheme.typography.titleSmall.copy(
                        textDecoration = decoration
                    ),
                    text = module.name,
                    icon = isVerified nullable R.drawable.rosette_discount_check,
                    tint = MaterialTheme.colorScheme.surfaceTint,
                    rightIcon = true,
                    iconScalingFactor = 1.0f,
                    spacing = 8f,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(
                        id = R.string.module_version_author,
                        module.versionDisplay,
                        module.author
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = decoration,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (menu.showUpdatedTime) {
                    Text(
                        text = stringResource(
                            id = R.string.module_update_at,
                            state.lastUpdated.toFormattedDateSafely
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        textDecoration = decoration,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        Text(
            modifier = Modifier
                .alpha(alpha = alpha)
                .padding(end = 16.dp, bottom = 16.dp, start = 16.dp),
            text = module.description,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            textDecoration = decoration,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.weight(1f))

        if (hasLabel) {
            Row(
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 16.dp, start = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                module.categories.nullable {
                    if (it.isNotNullOrEmpty()) {
                        LabelItem(
                            icon = R.drawable.category,
                            text = it.first(),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                module.license.nullable(menu.showLicense) {
                    LabelItem(
                        icon = R.drawable.tag,
                        text = it
                    )
                }

                module.track.antifeatures.nullable(menu.showAntiFeatures) {
                    if (it.isNotEmpty()) {
                        LabelItem(
                            icon = R.drawable.alert_triangle,
                            containerColor = MaterialTheme.colorScheme.onTertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            text = stringResource(id = R.string.view_module_antifeatures)
                        )
                    }
                }

                when {
                    state.updatable ->
                        LabelItem(
                            text = stringResource(id = R.string.module_new),
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )

                    state.installed ->
                        LabelItem(text = stringResource(id = R.string.module_installed))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
