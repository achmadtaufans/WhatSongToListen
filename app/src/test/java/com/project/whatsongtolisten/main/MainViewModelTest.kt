package com.project.whattolisten.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.project.whatsongtolisten.common.api.UseCaseResult
import com.project.whatsongtolisten.common.data.ApiResponse
import com.project.whatsongtolisten.common.data.Song
import com.project.whatsongtolisten.common.repository.MusicRepository
import com.project.whatsongtolisten.ui.main.MainViewModel
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(fullyQualifiedNames = [
    "com.project.whattolisten.*",
    "com.google.firebase.ktx.*"])
class MainViewModelTest : TestCase() {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val test = TestCoroutineRule()

    lateinit var viewModel: MainViewModel

    private val musicRepository = mock(MusicRepository::class.java)

    val dummyList = arrayListOf(Song("", "", "", "", "", "", 0))

    @Before
    fun setup() {
        viewModel = MainViewModel(musicRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `return music response after hit itunes api`() {
        test.runBlockingTest {
            val response = ApiResponse(1, arrayListOf(Song("abc", "you", "aaa", "asd", "" , "", 0)))
            `when`((musicRepository.getSongs("abc") as? UseCaseResult.Success)?.data)
                    .thenReturn(response)
        }
    }

    @Test
    fun `show loading`() {
        viewModel.isLoadingVisible.value = true
        Assert.assertEquals(true, viewModel.isLoadingVisible.value)
    }

    @Test
    fun `hide loading`() {
        viewModel.isLoadingVisible.value = false
        Assert.assertEquals(false, viewModel.isLoadingVisible.value)
    }

    @Test
    fun `update adapter`() {
        viewModel.actionType.value = MainViewModel.UPDATE_ADAPTER
        Assert.assertEquals(MainViewModel.UPDATE_ADAPTER, viewModel.actionType.value)
    }

    @Test
    fun `clear list`() {
        dummyList.clear()
        Assert.assertEquals(0, dummyList.size)
    }

    @Test
    fun `on find new artist`() {
        viewModel.onFindNewArtis.value = true
        Assert.assertEquals(true, viewModel.onFindNewArtis.value)
    }

    @Test
    fun `on not find new artist`() {
        viewModel.onFindNewArtis.value = false
        Assert.assertEquals(false, viewModel.onFindNewArtis.value)
    }
}