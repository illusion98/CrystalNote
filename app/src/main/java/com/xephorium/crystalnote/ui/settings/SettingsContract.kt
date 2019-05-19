package com.xephorium.crystalnote.ui.settings

import com.xephorium.crystalnote.data.SharedPreferencesRepository
import com.xephorium.crystalnote.data.model.DateType
import com.xephorium.crystalnote.ui.base.BasePresenter
import com.xephorium.crystalnote.ui.base.BaseView

interface SettingsContract {

    interface View : BaseView {
        fun populateNotePreviewLines(lines: Int)
        fun populateNoteDateType(dateType: DateType)
        fun populateNoteColorsCheckbox(checked: Boolean)
        fun populateTodayHeaderCheckbox(checked: Boolean)

        fun showNavigationDrawer()
        fun showDiscardChangesDialog()
        fun navigateBack()
    }

    abstract class Presenter : BasePresenter<View>() {
        lateinit var sharedPreferencesRepository: SharedPreferencesRepository
        var notePreviewLines: Int = 1
        lateinit var noteDateType: DateType
        var noteColorsEnabled: Boolean = true
        var todayHeaderEnabled: Boolean = true

        abstract fun handleMenuButtonClick()
        abstract fun handleNoteLinesChange(lines: Int)
        abstract fun handleNoteDateTypeChange(dateType: DateType)
        abstract fun handleNoteColorsToggle(checked: Boolean)
        abstract fun handleTodayHeaderToggle(checked: Boolean)
        abstract fun handleSaveClick()
        abstract fun handleBackClick()
        abstract fun handleBackConfirm()
    }
}
