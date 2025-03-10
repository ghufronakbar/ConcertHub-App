package com.example.concerthub.MainPageActivity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.concerthub.ApiService.ApiService
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.LoginPage
import com.example.concerthub.MainPageActivity.AccountPageActivity.EditPasswordPage
import com.example.concerthub.MainPageActivity.AccountPageActivity.EditProfilePage
import com.example.concerthub.R
import com.example.concerthub.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {
    companion object {
        private const val REQUEST_CODE_EDIT_PROFILE = 123
    }

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var textViewFullName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewPhone: TextView
    private lateinit var btnLogout: TextView
    private lateinit var btnEditPassword: TextView
    private lateinit var btn_edit: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_account, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        textViewFullName= view.findViewById(R.id.textViewFullName)
        textViewPhone= view.findViewById(R.id.textViewPhone)
        textViewEmail= view.findViewById(R.id.textViewEmail)

        btnEditPassword = view.findViewById(R.id.btnEditPassword)
        btnLogout = view.findViewById(R.id.btnLogout)
        btn_edit = view.findViewById(R.id.btn_edit)

        setupSwipeToRefresh()

        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        btnEditPassword.setOnClickListener {
            val intent = Intent(context, EditPasswordPage::class.java)
            startActivity(intent)
        }

        fetchProfile()







        return view
    }
    // Di AccountFragment, tangani hasil dari EditProfilePage
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == EditProfilePage.RESULT_UPDATED_PROFILE) {
            // Jika profil diperbarui, muat ulang profil
            fetchProfile()
        }
    }


    private fun setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh action
            fetchProfile()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin Keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun logout() {
        context?.let { TokenManager.clearToken(it) }
        val intent = Intent(context, LoginPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun fetchProfile() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.profile(token).enqueue(object :
                Callback<ApiService.profileResponse> {
                override fun onResponse(call: Call<ApiService.profileResponse>, response: Response<ApiService.profileResponse>) {
                    swipeRefreshLayout.isRefreshing = false
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val profile=it.values[0]
                            textViewFullName.setText(profile.fullname)
                            textViewEmail.setText(profile.email)
                            textViewPhone.setText(profile.phone)
                            btn_edit.setOnClickListener {
                                val intent = Intent(context, EditProfilePage::class.java).apply {
                                    putExtra("fullname", profile.fullname)
                                    putExtra("email", profile.email)
                                    putExtra("phone", profile.phone)
                                }
                                startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE)
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to load profile data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiService.profileResponse>, t: Throwable) {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(context, "Layanan Tidak Tersedia", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}