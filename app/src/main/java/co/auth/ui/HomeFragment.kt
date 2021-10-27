package co.auth.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import co.auth.MainActivity
import co.auth.User
import co.auth.databinding.FragmentHomeBinding
import co.auth.databinding.FragmentRegisterBinding
import co.auth.getUser
import co.auth.restartApp
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = getUser()

        binding.signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            restartApp(requireContext(), requireActivity() as MainActivity)
        }

        setUser()
    }

    private fun setUser() {
        Glide.with(requireContext()).load(user.image).into(binding.img)
        binding.txtName.text = user.name
    }
}