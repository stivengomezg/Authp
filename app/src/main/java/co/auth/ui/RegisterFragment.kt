package co.auth.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.util.PatternsCompat
import androidx.navigation.fragment.findNavController
import co.auth.*
import co.auth.databinding.FragmentRegisterBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private var imageProfile: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtHaveACount.setOnClickListener { findNavController().navigate(R.id.action_registerFragment_to_loginFragment) }
        binding.btnRegister.setOnClickListener { register() }
        binding.imgUserRegister.setOnClickListener { openGalleryForPicture() }
    }

    private fun register() {
        user = User(
            binding.txtName.text.toString(),
            binding.txtLastName.text.toString(),
            binding.txtEmail.text.toString(),
            binding.txtPassword.text.toString()
        )
        val confirmPassword = binding.txtConfirmPassword.text.toString()

        if(user.name.isEmpty()) {
            binding.txtName.error = "Ingresa tu nombre"
            return
        }

        if(user.email.isEmpty()) {
            binding.txtEmail.error = "Ingrese un email"
            return
        }

        if(!PatternsCompat.EMAIL_ADDRESS.matcher(user.email).matches()) {
            binding.txtEmail.error = "Email invalido"
            return
        }

        if(user.password.isEmpty()){
            binding.txtPassword.error = "Ingresa una contraseña"
            return
        }

        if(user.password.length < 6){
            binding.txtPassword.error = "La contraseña es demasiado corta"
            return
        }

        if (confirmPassword.isEmpty()){
            binding.txtConfirmPassword.error = "Confirma tu contraseña"
            return
        }

        if(user.password != confirmPassword) {
            binding.txtConfirmPassword.error = "Las contraseñas no coinciden"
            return
        }

        binding.progressBarSignUp.show()
        auth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            if (it.isSuccessful) {
                imageProfile?.let { saveImgUser() } ?: saveUser()
                mssg("Ya puedes autenticarte con tus credenciales", requireContext())
            } else {
                binding.progressBarSignUp.hide()
                mssg("No se logro registrar", requireContext())
            }
        }
    }

    private fun saveImgUser() {
        val path = "user/${auth.currentUser?.uid}"
        val storageRef = FirebaseStorage.getInstance().reference.child(path)
        val imageRef = storageRef.child("${user.email}.jpg")

        try {
            imageRef.putBytes(imageProfile!!.toByteArray()).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    user.image = it.toString()
                    saveUser()
                }
            }
        }catch (e: Exception) {
            mssg("No se logro guardar la imagen", requireContext())
            saveUser()
        }

    }

    private fun saveUser() {
        auth.currentUser?.uid?.let {
            FirebaseFirestore.getInstance().collection("users").document(it).set(user).addOnCompleteListener {
                binding.progressBarSignUp.hide()
                if (it.isSuccessful) {
                    setProfile()
                } else {
                    mssg("No se logro guardar los datos del usuario", requireContext())
                }
            }
        }
    }

    private fun setProfile() {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(user.name)
            .setPhotoUri(Uri.parse(user.image))
            .build()
        binding.progressBarSignUp.show()
        auth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener {
            binding.progressBarSignUp.hide()
            if (it.isSuccessful) {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            } else {
                mssg("No se logro asignar el perfil del usuario", requireContext())
            }
        }
    }

    val resultOpenGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let {
                setProfilePicture(it)
            }
        }
    }
    private fun openGalleryForPicture() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        try {
            resultOpenGallery.launch(intent)
        }catch (e: ActivityNotFoundException){
            mssg("No se pudo cargar la imagen", requireContext())
        }
    }

    private fun setProfilePicture(image: Uri) {
        try {
            Glide.with(requireContext()).load(image).centerCrop().into(binding.imgUserRegister)
            imageProfile = image.toBitmap(requireContext())
        }catch (e: Exception){
            mssg( "Algo salio mal al cargar la imagen.", requireContext())
        }
    }

}