package ru.studymushrooms.ui.auth

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import ru.studymushrooms.MainActivity
import ru.studymushrooms.R

class LoginFragment : Fragment(R.layout.login_fragment) {

    private lateinit var usernameEditTextLogin: TextInputEditText
    private lateinit var passwordEditTextLogin: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var emailEditTextRegister: TextInputEditText
    private lateinit var usernameEditTextRegister: TextInputEditText
    private lateinit var passwordEditTextRegister: TextInputEditText
    private lateinit var signupButton: Button

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usernameEditTextLogin = requireView().findViewById(R.id.login_edittext)
        passwordEditTextLogin = requireView().findViewById(R.id.password_edittext)
        loginButton = requireView().findViewById(R.id.login_button)
        usernameEditTextRegister = requireView().findViewById(R.id.signup_login_edittext)
        passwordEditTextRegister = requireView().findViewById(R.id.signup_password_edittext)
        emailEditTextRegister = requireView().findViewById(R.id.signup_email_edittext)
        signupButton = requireView().findViewById(R.id.signup_button)

        loginButton.setOnClickListener {
            viewModel.authenticate(
                usernameEditTextLogin.text.toString(),
                passwordEditTextLogin.text.toString()
            )
        }

        signupButton.setOnClickListener {
            viewModel.register(
                usernameEditTextRegister.text.toString(),
                emailEditTextRegister.text.toString(),
                passwordEditTextRegister.text.toString()
            )
        }

        val navController = findNavController()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.refuseAuthentication()
        }

        viewModel.authenticationState.observe(viewLifecycleOwner) { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> navController.popBackStack()
                LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> showErrorMessage()
            }
        }

    }

    private fun showErrorMessage() {
        AlertDialog.Builder(activity)
            .setMessage(getString(R.string.login_error))
            .create()
            .show()
        viewModel.refuseAuthentication()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomNav()
    }

    override fun onDetach() {
        super.onDetach()
        (activity as MainActivity).showBottomNav()
    }
}
