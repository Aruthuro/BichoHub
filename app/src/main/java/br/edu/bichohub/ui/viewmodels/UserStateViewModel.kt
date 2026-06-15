package br.edu.bichohub.ui.viewmodels

import androidx.lifecycle.ViewModel
import br.edu.bichohub.repos.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserStateViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

}