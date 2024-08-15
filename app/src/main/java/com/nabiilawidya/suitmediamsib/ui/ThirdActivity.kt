package com.nabiilawidya.suitmediamsib.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nabiilawidya.suitmediamsib.data.ApiConfig
import com.nabiilawidya.suitmediamsib.data.DataItem
import com.nabiilawidya.suitmediamsib.data.UserAdapter
import com.nabiilawidya.suitmediamsib.data.UserResponse
import com.nabiilawidya.suitmediamsib.databinding.ActivityThirdBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ThirdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThirdBinding
    private lateinit var adapter: UserAdapter
    private var currentPage = 1
    private var totalPages = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Third Screen"

        setupRecyclerView()
        fetchUsers(false)

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshUsers()
        }
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter()
        adapter.setClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DataItem) {
                val intent = Intent()
                intent.putExtra("selectedUserName", "${data.firstName} ${data.lastName}")
                setResult(RESULT_OK, intent)
                finish()
            }
        })

        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = adapter
    }

    private fun fetchUsers(isRefresh: Boolean = false) {
        if (!isRefresh) {
            binding.progressbar.visibility = View.VISIBLE
        }

        val params = HashMap<String, String>()
        params["page"] = currentPage.toString()

        ApiConfig.getApiService().getUser(params).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                binding.progressbar.visibility = View.GONE
                if (response.isSuccessful) {
                    response.body()?.let { userResponse ->
                        totalPages =
                            userResponse.totalPages ?: 1
                        val listUsers =
                            userResponse.data?.filterNotNull() ?: emptyList()

                        if (listUsers.isNotEmpty()) {
                            if (isRefresh) {
                                adapter.setList(listUsers)
                            } else {
                                adapter.addUsers(listUsers)
                            }
                        } else {
                            showEmptyState()
                        }
                    }
                } else {
                    showError()
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                binding.progressbar.visibility = View.GONE
                showError()
            }
        })
    }


    private fun refreshUsers() {
        currentPage = 1
        adapter.clearUsers()
        fetchUsers(true)
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun showEmptyState() {
        Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show()
        binding.rvUser.visibility = RecyclerView.GONE
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun showError() {
        Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
