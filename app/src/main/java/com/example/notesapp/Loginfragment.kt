package com.example.notesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notesapp.databinding.FragmentLoginfragmentBinding
import com.example.notesapp.models.UserRequest
import com.example.notesapp.utils.NetworkResult
import com.example.notesapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Loginfragment : Fragment() {

    private var _binding : FragmentLoginfragmentBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginfragmentBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener{
            findNavController().navigate(R.id.action_loginfragment_to_mainFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val validationResult = validateUserInput()
            if(validationResult.first){
                val userRequest = getUserRequest()

                authViewModel.loginUser(userRequest)
            }
            else{
                binding.txtError.text = validationResult.second
            }

        }

        binding.btnSignUp.setOnClickListener {
            findNavController().popBackStack()
        }

        bindObserver()


    }

    private fun getUserRequest(): UserRequest{
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        return UserRequest(email, password, "")
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val userRequest = getUserRequest()

        return authViewModel.validate(userRequest.username, userRequest.email, userRequest.password, true)
    }

    private fun bindObserver() {
        authViewModel.userResponseLivedata.observe(viewLifecycleOwner, {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    //save the received token
                    tokenManager.saveToken(it.data!!.token)
                    findNavController().navigate(R.id.action_loginfragment_to_mainFragment)
                }

                is NetworkResult.Error -> {
                    binding.txtError.text = it.message
                }

                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}