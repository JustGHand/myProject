package com.pw.codeset.utils

import android.content.Context
import android.content.Intent
import com.pw.codeset.abilities.notes.NotesEditActivity
import com.pw.codeset.abilities.schedule.ScheduleEdit

object IntentUtils {
    fun toScheduleEdit(context: Context) {
        context.startActivity(Intent(context, ScheduleEdit::class.java))
    }
    fun toNotesEdit(context: Context) {
        context.startActivity(Intent(context, NotesEditActivity::class.java))
    }
}