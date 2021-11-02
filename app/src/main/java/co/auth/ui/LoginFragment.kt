package co.auth.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import co.auth.R
import co.auth.databinding.FragmentLoginBinding
import co.auth.databinding.FragmentRegisterBinding
import co.auth.hide
import co.auth.mssg
import co.auth.show
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener { login() }
        binding.btnRegistro.setOnClickListener { findNavController().navigate(R.id.action_loginFragment_to_registerFragment) }

    }

    private fun login() {

        val email = binding.txtEmail.text.trim().toString()
        val password = binding.txtPassword.text.trim().toString()

        if(email.isEmpty()) {
            binding.txtEmail.error = "Ingrese un correo"
            return
        }

        if(password.isEmpty()) {
            binding.txtPassword.error = "Ingrese una contraseña"
            return
        }

        binding.progressBar.show()
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener {
                binding.progressBar.hide()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }catch (e: Exception) {
            mssg("No se logro iniciar sesion, revisa tus credenciales", requireContext())
        }
    }
}