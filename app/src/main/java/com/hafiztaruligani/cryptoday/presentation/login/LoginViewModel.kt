package com.hafiztaruligani.cryptoday.presentation.login

import androidx.lifecycle.ViewModel
import androidx.room.PrimaryKey
import com.hafiztaruligani.cryptoday.domain.usecase.GetUserNameUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.SetUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getUserNameUseCase: GetUserNameUseCase,
    private val setUserNameUseCase: SetUserNameUseCase
) : ViewModel() {
    fun getUserName() = getUserNameUseCase.invoke()
    suspend fun setUserName(value: String) = setUserNameUseCase.invoke(value)
}