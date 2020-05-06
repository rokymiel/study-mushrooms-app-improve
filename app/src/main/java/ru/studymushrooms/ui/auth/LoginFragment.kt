package ru.studymushrooms.ui.auth

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import ru.studymushrooms.MainActivity
import ru.studymushrooms.R

class LoginFragment : Fragment() {

    private lateinit var usernameEditTextLogin: TextInputEditText
    private lateinit var passwordEditTextLogin: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var emailEditTextRegister: TextInputEditText
    private lateinit var usernameEditTextRegister: TextInputEditText
    private lateinit var passwordEditTextRegister: TextInputEditText
    private lateinit var signupButton: Button


    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> navController.popBackStack()
                LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> showErrorMessage()
            }
        })

    }

    fun showErrorMessage() {
        AlertDialog.Builder(activity)
            .setMessage("Что-то пошло не так, вероятно, были предоставлены неверные данные.")
            .create().show()
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
