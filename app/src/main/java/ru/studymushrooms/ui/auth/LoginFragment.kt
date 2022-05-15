package ru.studymushrooms.ui.auth

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import ru.studymushrooms.MainActivity
import ru.studymushrooms.R
import ru.studymushrooms.service_locator.ServiceLocator

class LoginFragment : Fragment(R.layout.login_fragment) {

    private lateinit var usernameEditTextLogin: TextInputEditText
    private lateinit var passwordEditTextLogin: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var emailEditTextRegister: TextInputEditText
    private lateinit var usernameEditTextRegister: TextInputEditText
    private lateinit var passwordEditTextRegister: TextInputEditText
    private lateinit var signupButton: Button

    @Suppress("UNCHECKED_CAST")
    private val viewModel: LoginViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(
                    ServiceLocator.authorizationRepository,
                    ServiceLocator.tokenHolder
                ) as T
            }
        }
    }

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
            val username = usernameEditTextLogin.text.toString()
            val password = passwordEditTextLogin.text.toString()

            viewModel.authenticate(
                username,
                password
            )
        }

        signupButton.setOnClickListener {
            val username = usernameEditTextRegister.text.toString()
            val password = passwordEditTextRegister.text.toString()
            val email = emailEditTextRegister.text.toString()

            viewModel.register(
                username,
                email,
                password
            )
        }

        val navController = findNavController()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.refuseAuthentication()
        }

        viewModel.showToastEvents.observe(viewLifecycleOwner) { toastEvent ->
            val toastString = when (toastEvent) {
                is ToastEvent.ErrorString -> getString(
                    R.string.validation_error_template,
                    toastEvent.errorMessage
                )
                is ToastEvent.ResString -> getString(toastEvent.res)
            }
            Toast.makeText(
                context,
                toastString,
                Toast.LENGTH_LONG
            ).show()
        }

        viewModel.authenticationState.observe(viewLifecycleOwner) { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> navController.popBackStack()
                LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> showErrorMessage()
            }
        }

        viewModel.registerEvents.observe(viewLifecycleOwner) { registerEvent ->
            val toastString = when (registerEvent) {
                is RegisterEvents.RegisterResult -> getString(
                    R.string.register_error_template,
                    registerEvent.message
                )
            }
            Toast.makeText(
                context,
                toastString,
                Toast.LENGTH_LONG
            ).show()
        }

        viewModel.loginEvents.observe(viewLifecycleOwner) { loginEvent ->
            val toastString = when (loginEvent) {
                is LoginEvents.LoginResult -> getString(
                    R.string.login_error_template,
                    loginEvent.message
                )
            }
            Toast.makeText(
                context,
                toastString,
                Toast.LENGTH_LONG
            ).show()
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
