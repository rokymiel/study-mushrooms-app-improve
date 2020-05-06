package ru.studymushrooms.ui.notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import ru.studymushrooms.R

class NotesFragment : Fragment() {

    companion object {
        fun newInstance() = NotesFragment()
    }

    private val notesViewModel: NotesViewModel by activityViewModels()

    //    private lateinit var title: TextView
//    private lateinit var date: TextView
//    private lateinit var content: TextView
    private lateinit var recyclerView: RecyclerView
    private val adapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
    private val items: ArrayList<NoteItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.notes_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<FloatingActionButton>(R.id.note_fab)

        notesViewModel.notes.observe(viewLifecycleOwner, Observer {
            if (notesViewModel.notes.value != null) {
                for (i in notesViewModel.notes.value!!) {
                    items.add(NoteItem(i))
                }
                adapter.addAll(items)
                recyclerView = requireView().findViewById(R.id.notes_recyclerview)
                recyclerView.adapter = adapter
                recyclerView.layoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        })

        button.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_note_creation)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadData(context)
    }

    override fun onDetach() {
        super.onDetach()
        items.clear()
        adapter.clear()
    }

    fun loadData(context: Context) {
        notesViewModel.loadData(context)
    }
}
