package ru.studymushrooms.ui.notes

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.studymushrooms.MainActivity
import ru.studymushrooms.R
import ru.studymushrooms.service_locator.ServiceLocator

class ViewNoteFragment : Fragment(R.layout.fragment_view_note) {

    @Suppress("UNCHECKED_CAST")
    private val viewModel: NotesViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotesViewModel(ServiceLocator.noteRepository) as T
            }

        }
    }

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

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
        titleEditText = view.findViewById(R.id.title_note_edittext)
        contentEditText = view.findViewById(R.id.content_note_edittext)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_delete_menu, menu)
        super.onCreateOptionsMenu(menu, requireActivity().menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.submit) {
            viewModel.updateNote(titleEditText.text.toString(), contentEditText.text.toString())
            findNavController().popBackStack()
            return true
        } else if (item.itemId == R.id.delete) {
            viewModel.deleteNote(titleEditText.text.toString(), contentEditText.text.toString())
            findNavController().popBackStack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomNav()
    }

    override fun onDetach() {
        super.onDetach()
        (activity as MainActivity).showBottomNav()
    }
}