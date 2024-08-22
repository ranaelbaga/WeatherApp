package com.example.project2.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project2.adapters.SearchAdapter
import com.example.project2.databinding.FragmentSearchBinding
import com.example.project2.models.SearchViewModel
import com.example.project2.R

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter(emptyList()) { selectedCity ->
            Log.d("SearchFragment", "Navigating with selectedCity: $selectedCity")
            val action = SearchFragmentDirections.actionSearchFragmentToSavedFragment(selectedCity)
            findNavController().navigate(action)
        }

        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearch.adapter = searchAdapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    searchViewModel.searchLocation("aa5c10538bcf46b692f140709240208", query)
                } else {
                    searchAdapter.updateData(emptyList())
                }

                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    hideKeyboard()
                }
                handler.postDelayed(searchRunnable!!, 1000)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        searchViewModel.locationSearchResponse.observe(viewLifecycleOwner) { locations ->
            if (locations != null) {
                val regions = locations.map { it.region }
                searchAdapter.updateData(regions)
            }
        }

        searchViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }
}
