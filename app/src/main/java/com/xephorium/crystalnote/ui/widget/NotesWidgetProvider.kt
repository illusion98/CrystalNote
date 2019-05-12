package com.xephorium.crystalnote.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast

import com.xephorium.crystalnote.R
import com.xephorium.crystalnote.data.NoteRepository
import com.xephorium.crystalnote.data.SharedPreferencesRepository
import com.xephorium.crystalnote.data.utility.NoteUtility
import com.xephorium.crystalnote.ui.IntentLibrary
import com.xephorium.crystalnote.ui.selection.SelectionActivity

/*
  NotesWidgetProvider                          05.11.2019
  Christopher Cruzen

    Defines appearance and view behavior of notes widget.
*/

class NotesWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {

        getWidgetInstances(context, appWidgetManager)?.let { widgets ->
            for (widgetInstance in widgets) {

                // Get Necessary Variables
                val widgetView = RemoteViews(context.packageName, R.layout.note_widget_layout)
                val sharedPreferencesRepository = SharedPreferencesRepository(context)
                val noteRepository = NoteRepository(context)
                val displayNote = sharedPreferencesRepository.getDisplayNoteName()?.let { name ->
                    NoteUtility.getNoteFromList(noteRepository.getNotes(), name)
                }

                // Populate Fields
                if (displayNote != null) {
                    widgetView.setTextViewText(R.id.note_widget_title, displayNote.name)
                    widgetView.setTextViewText(
                            R.id.note_widget_text,
                            noteRepository.readNoteContents(displayNote.name!!)
                    )
                } else {
                    val noteList = noteRepository.getNotes()
                    if (noteList.isNotEmpty()) {
                        widgetView.setTextViewText(R.id.note_widget_title, noteList[0].name)
                        widgetView.setTextViewText(
                                R.id.note_widget_text,
                                noteRepository.readNoteContents(noteList[0].name!!)
                        )
                    } else {
                        widgetView.setTextViewText(R.id.note_widget_title, noteList[0].name)
                        widgetView.setTextViewText(
                                R.id.note_widget_text,
                                noteRepository.readNoteContents(noteList[0].name!!)
                        )
                    }
                }

                // Set Listeners
                widgetView.setOnClickPendingIntent(
                        R.id.note_widget_button,
                        getOnClickPendingIntent(context, BUTTON_CLICK_INTENT)
                )
                widgetView.setOnClickPendingIntent(
                        R.id.note_widget_text,
                        getOnClickPendingIntent(context, TEXT_CLICK_INTENT)
                )
                widgetView.setOnClickPendingIntent(
                        R.id.note_widget_title,
                        getOnClickPendingIntent(context, TITLE_CLICK_INTENT)
                )

                appWidgetManager.updateAppWidget(widgetInstance, widgetView)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            BUTTON_CLICK_INTENT -> {
                val buttonIntent = Intent(context, SelectionActivity::class.java)
                buttonIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                buttonIntent.action = IntentLibrary.CHOOSE_NOTE_INTENT
                context.startActivity(buttonIntent)
            }

            TEXT_CLICK_INTENT -> Toast.makeText(context, "Text Clicked", Toast.LENGTH_SHORT).show()

            TITLE_CLICK_INTENT -> Toast.makeText(context, "Title Clicked", Toast.LENGTH_SHORT).show()

            IntentLibrary.UPDATE_NOTE_INTENT -> updateWidgets(context)
        }
    }

    private fun getOnClickPendingIntent(context: Context, intentAction: String): PendingIntent {
        val intent = Intent(context, this.javaClass)
        intent.action = intentAction
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    private fun updateWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
        val widgetInstances = getWidgetInstances(context, appWidgetManager)

        if (widgetInstances != null && widgetInstances.isNotEmpty())
            onUpdate(context, appWidgetManager, widgetInstances)
    }

    private fun getWidgetInstances(context: Context, appWidgetManager: AppWidgetManager): IntArray? {
        val widgetName = ComponentName(context, this.javaClass)
        return appWidgetManager.getAppWidgetIds(widgetName)
    }

    companion object {

        private const val BUTTON_CLICK_INTENT = "com.xephorium.crystalnote.widget.click.BUTTON"
        private const val TEXT_CLICK_INTENT = "com.xephorium.crystalnote.widget.click.TEXT"
        private const val TITLE_CLICK_INTENT = "com.xephorium.crystalnote.widget.click.TITLE"
    }
}