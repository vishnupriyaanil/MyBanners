package com.bcforward.mybanners.ui.banners.view

import BannersScreen
import SplitBannerScreen
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.bcforward.mybanners.R
import com.bcforward.mybanners.ui.banners.viewmodel.MainBannersViewModel
import com.bcforward.mybanners.ui.common.loading.LoadingColumn
import com.bcforward.mybanners.ui.common.error.ErrorColumn
import com.bcforward.mybanners.ui.navigation.BannerTypes

@Composable
fun MainBannersScreen() {
    val mainBannersViewModel = hiltViewModel<MainBannersViewModel>()
    val uiState by mainBannersViewModel.uiState.collectAsState()

    when (uiState) {
        is MainBannersViewModel.UiState.Loading -> {
            LoadingColumn(title = stringResource(R.string.loading_banners))
        }

        is MainBannersViewModel.UiState.Success -> {
            val banners = (uiState as MainBannersViewModel.UiState.Success).banners
            LazyColumn {
                items(banners) { section ->
                    when (section.sectionType) {
                        BannerTypes.BANNER -> {
                            section.items.forEach { item ->
                                BannersScreen(item = item)
                            }
                        }

                        BannerTypes.HORIZONTAL_FREE_SCROLL -> {
                            HorizontalFreeScrollScreen(items = section.items)
                        }

                        BannerTypes.SPLIT_BANNER -> {
                            SplitBannerScreen(items = section.items)
                        }

                        else -> {
                            Text(text = "Unknown section type: ${section.sectionType}")
                        }
                    }
                }
            }
        }

        is MainBannersViewModel.UiState.Error -> {
            val errorMessage = (uiState as MainBannersViewModel.UiState.Error).message
            ErrorColumn(message = errorMessage)
            Button(onClick = { mainBannersViewModel.retryFetchBanners() }) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}
