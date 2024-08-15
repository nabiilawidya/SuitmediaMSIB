package com.nabiilawidya.suitmediamsib.ui

import android.os.Bundle
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

        setupRecyclerView()
        fetchUsers()

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshUsers()
        }
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter()
        adapter.setClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DataItem) {
                updateUserOnSecondScreen(data)
            }
        })
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = adapter
        binding.rvUser.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && currentPage < totalPages) {
                    fetchUsers()
                }
            }
        })
    }

    private fun fetchUsers() {
        val parameters = hashMapOf(
            "page" to currentPage.toString(),
            "per_page" to "6"
        )
        val client = ApiConfig.getApiService().getUser(parameters)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    totalPages = userResponse?.totalPages ?: 1
                    currentPage = userResponse?.page ?: 1
                    val newUsers = userResponse?.data ?: emptyList()

                    if (newUsers.isEmpty() && currentPage == 1) {
                        showEmptyState()
                    } else {
                        adapter.setList(newUsers.filterNotNull())
                        currentPage++
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                showError()
            }
        })
    }

    private fun refreshUsers() {
        currentPage = 1
        totalPages = 1
        adapter.clearUsers()
        fetchUsers()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun showEmptyState() {
        Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show()
        binding.rvUser.visibility = RecyclerView.GONE
        binding.emptyState.visibility = RecyclerView.VISIBLE
    }

    private fun showError() {
        Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show()
    }

    private fun updateUserOnSecondScreen(data: DataItem) {
        val intent = intent
        intent.putExtra("selectedUserName", "${data.firstName} ${data.lastName}")
        setResult(RESULT_OK, intent)
        finish()
    }
}
