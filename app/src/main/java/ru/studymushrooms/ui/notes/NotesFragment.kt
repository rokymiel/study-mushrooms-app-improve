package ru.studymushrooms.ui.notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import ru.studymushrooms.R
import ru.studymushrooms.service_locator.ServiceLocator

class NotesFragment : Fragment(R.layout.notes_fragment) {
    @Suppress("UNCHECKED_CAST")
    private val notesViewModel: NotesViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotesViewModel(ServiceLocator.noteRepository) as T
            }

        }
    }

    private lateinit var recyclerView: RecyclerView
    private val adapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
    private val items: ArrayList<NoteItem> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<FloatingActionButton>(R.id.note_fab)

        recyclerView = requireView().findViewById(R.id.notes_recyclerview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        notesViewModel.notes.observe(viewLifecycleOwner) { notes ->
            for (note in notes) {
                items.add(NoteItem(note, navController = findNavController()))
            }
            adapter.addAll(items)
        }

        notesViewModel.showErrorToastEvents.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(
                context,
                getString(R.string.notes_error_template, errorMessage),
                Toast.LENGTH_LONG
            ).show()
        }

        button.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_note_creation)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        notesViewModel.loadData()
    }

    override fun onDetach() {
        super.onDetach()
        items.clear()
        adapter.clear()
    }
}
