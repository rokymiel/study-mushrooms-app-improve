package ru.studymushrooms.ui.notes

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.studymushrooms.MainActivity
import ru.studymushrooms.R

class CreateNoteFragment : Fragment() {

    private val viewModel: NotesViewModel by activityViewModels()

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleEditText = view.findViewById(R.id.title_note_edittext)
        contentEditText = view.findViewById(R.id.content_note_edittext)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.submit_menu, menu)
        super.onCreateOptionsMenu(menu, requireActivity().menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.submit) {
            viewModel.saveNote(titleEditText.text.toString(), contentEditText.text.toString())
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