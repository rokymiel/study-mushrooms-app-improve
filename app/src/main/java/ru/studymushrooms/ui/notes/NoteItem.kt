package ru.studymushrooms.ui.notes

import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ru.studymushrooms.R
import ru.studymushrooms.api.NoteModel

class NoteItem(val noteModel: NoteModel, val notesViewModel: NotesViewModel) :
    Item<GroupieViewHolder>() {

    override fun getLayout(): Int = R.layout.note_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.note_title_textview).text = noteModel.title
        viewHolder.itemView.findViewById<TextView>(R.id.note_content_textview).text =
            noteModel.content
        viewHolder.itemView.findViewById<TextView>(R.id.note_date_textview).text =
            noteModel.date.toString()

        viewHolder.itemView.setOnClickListener{

        }
    }


}