package com.example.dishdelight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {
    private lateinit var recipeRecycler : RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize RecyclerView
        recipeRecycler = view.findViewById(R.id.recommendationsRecyclerView)
        val userid = FirebaseAuth.getInstance().currentUser!!.uid

        // Set layout manager
        recipeRecycler.layoutManager = LinearLayoutManager(requireContext())

        // Fetch recipes from the API
        fetchRecipes(userid)
    }
    private fun fetchRecipes( userid : String) {
        RetrofitClient.getClient().create(RecipeApiService::class.java)
            .getRecipes().enqueue(object : Callback<List<Recipe>> {
                override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                    if(response.isSuccessful){
                        response.body()?.let {allRecipes ->
                            // Randomly shuffle the recipes and take the first 20
                            val randomRecipes = allRecipes.shuffled().take(20)
                            recipeAdapter = RecipeAdapter(userid, randomRecipes)
                            recipeRecycler.adapter = recipeAdapter
                        }
                    } else{
                        Toast.makeText(requireContext(), "Failed to fetch recipes", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}