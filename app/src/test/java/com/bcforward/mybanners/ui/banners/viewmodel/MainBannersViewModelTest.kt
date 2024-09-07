package com.bcforward.mybanners.ui.banners.viewmodel

import com.bcforward.mybanners.ui.banners.data.FakeBannerService
import com.bcforward.mybanners.ui.banners.data.FakeBannersRepository
import com.bcforward.mybanners.util.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.IOException

@ExperimentalCoroutinesApi
class MainBannersViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()
    private val fakeBannerService = FakeBannerService(shouldReturnError = false)
    private lateinit var bannersRepository: FakeBannersRepository
    private lateinit var mainBannersViewModel: MainBannersViewModel

    @Before
    fun setup() {
        bannersRepository = FakeBannersRepository(fakeBannerService)
        mainBannersViewModel = MainBannersViewModel(bannersRepository)
    }

    @Test
    fun `should fetch banners successfully and update ui state`() = runTest {
        val bannersResponse = bannersRepository.getBannerList().body()!!
        bannersRepository.setBannersResponse(bannersResponse)

        mainBannersViewModel.retryFetchBanners()

        expectThat(mainBannersViewModel.uiState.first()).isEqualTo(
            MainBannersViewModel.UiState.Success(bannersResponse)
        )
    }

    @Test
    fun `should set ui state to error when fetching banners fails`() = runTest {
        bannersRepository.setFetchBannersException(IOException("Network Error"))

        mainBannersViewModel.retryFetchBanners()

        expectThat(mainBannersViewModel.uiState.first()).isEqualTo(
            MainBannersViewModel.UiState.Error("Failed to load banners. Please try again.")
        )
    }

    @Test
    fun `should retry fetching banners on error`() = runTest {
        bannersRepository.setFetchBannersException(IOException("Network Error"))
        mainBannersViewModel.retryFetchBanners() // First attempt

        expectThat(mainBannersViewModel.uiState.first()).isEqualTo(
            MainBannersViewModel.UiState.Error("Failed to load banners. Please try again.")
        )

        // Simulate a successful fetch on the second attempt
        val bannersResponse = bannersRepository.getBannerList().body()!!
        bannersRepository.setBannersResponse(bannersResponse)

        mainBannersViewModel.retryFetchBanners() // Second attempt

        expectThat(mainBannersViewModel.uiState.first()).isEqualTo(
            MainBannersViewModel.UiState.Success(bannersResponse)
        )
    }

    @Test
    fun `should reset ui state on retryFetchBanners call`() = runTest {
        bannersRepository.setFetchBannersException(IOException("Network Error"))
        mainBannersViewModel.retryFetchBanners()

        expectThat(mainBannersViewModel.uiState.first()).isEqualTo(
            MainBannersViewModel.UiState.Error("Failed to load banners. Please try again.")
        )

        // Now we call retry again to reset the state
        mainBannersViewModel.retryFetchBanners()

        expectThat(mainBannersViewModel.uiState.first()).isEqualTo(
            MainBannersViewModel.UiState.Loading
        )
    }
}
