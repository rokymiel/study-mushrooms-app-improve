package ru.studymushrooms.ui.catalog

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import ru.studymushrooms.MainActivity
import ru.studymushrooms.R
import ru.studymushrooms.service_locator.ServiceLocator
import ru.studymushrooms.ui.auth.LoginViewModel
import java.util.*
import kotlin.collections.ArrayList

class CatalogFragment : Fragment(R.layout.fragment_catalog) {
    private val loginViewModel: LoginViewModel by activityViewModels { ServiceLocator.viewModelFactory }

    private val catalogViewModel: CatalogViewModel by viewModels { ServiceLocator.viewModelFactory }

    private val items: ArrayList<CatalogItem> = ArrayList()
    private lateinit var catalogRecyclerView: RecyclerView
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        loginViewModel.authenticationState.observe(viewLifecycleOwner) { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    (activity as MainActivity).showBottomNav()
                    observeToastEvents()
                    loadData()
                }
                else -> {
                    (activity as MainActivity).hideBottomNav()
                    navController.navigate(R.id.navigate_to_login_fragment)
                }
            }
        }
    }

    private fun observeToastEvents() {
        catalogViewModel.showErrorToastEvents.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(
                context,
                getString(R.string.mushrooms_error_template, errorMessage),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun loadData() {
        catalogViewModel.loadData()

        adapter = GroupAdapter()
        catalogRecyclerView = requireView().findViewById(R.id.catalog_recyclerview)
        catalogRecyclerView.adapter = adapter
        catalogRecyclerView.layoutManager = GridLayoutManager(context, 2)

        catalogViewModel.mushrooms.observe(viewLifecycleOwner) { mushrooms ->
            for (mushroom in mushrooms) {
                items.add(CatalogItem(mushroom))
            }
            adapter.addAll(items)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val search: MenuItem = menu.findItem(R.id.search)
        val searchView: SearchView = search.actionView as SearchView
        search(searchView)
        super.onCreateOptionsMenu(menu, requireActivity().menuInflater)

    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    val newItems = items.filter {
                        it.mushroom.name.lowercase(Locale.getDefault())
                            .contains(query.lowercase(Locale.getDefault()))
                    }
                    if (newItems.isEmpty()){
                        Toast.makeText(
                                context,
                                "Ничего не найдено",
                                Toast.LENGTH_LONG
                        ).show()
                        adapter.updateAsync(items, true, null)
                    }
                    else
                        adapter.updateAsync(newItems, true, null)
                }
                else
                    adapter.updateAsync(items, true, null)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val newItems = items.filter {
                        it.mushroom.name.lowercase(Locale.getDefault())
                            .contains(newText.lowercase(Locale.getDefault()))
                    }
                    adapter.updateAsync(newItems, true, null)
                } else {
                    adapter.updateAsync(items, true, null)
                }
                return true
            }
        })
    }
}
