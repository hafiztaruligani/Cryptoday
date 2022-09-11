package com.hafiztaruligani.cryptoday.presentation.login

import androidx.lifecycle.ViewModel
import com.hafiztaruligani.cryptoday.domain.usecase.GetUserUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.SetUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getUserNameUseCase: GetUserUseCase,
    private val setUserNameUseCase: SetUserNameUseCase
) : ViewModel() {
    fun getUserName() = getUserNameUseCase.invoke()
    suspend fun setUserName(value: String) = setUserNameUseCase.invoke(value)
}