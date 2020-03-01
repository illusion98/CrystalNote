package com.xephorium.crystalnote.ui.widget

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast

import com.xephorium.crystalnote.R
import com.xephorium.crystalnote.data.model.CrystalNoteTheme
import com.xephorium.crystalnote.data.model.WidgetState.Companion.TextSize
import com.xephorium.crystalnote.data.model.WidgetState.Companion.Transparency
import com.xephorium.crystalnote.data.repository.NoteRoomRepository
import com.xephorium.crystalnote.data.repository.SharedPreferencesRepository
import com.xephorium.crystalnote.ui.custom.NoteToolbar
import com.xephorium.crystalnote.ui.drawer.DrawerActivity
import kotlinx.android.synthetic.main.toolbar_activity_layout.*
import kotlinx.android.synthetic.main.widget_activity_layout.*


class WidgetActivity : DrawerActivity(), WidgetContract.View {


    /*--- Variable Declarations ---*/

    private lateinit var presenter: WidgetPresenter


    /*--- Lifecycle Methods ---*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityContent(R.layout.widget_activity_layout)

        presenter = WidgetPresenter()
        presenter.sharedPreferencesRepository = SharedPreferencesRepository(this)
        presenter.noteRoomRepository = NoteRoomRepository(this)

        setupToolbar()
        setupTextSizeSpinner()
        setupTransparencySpinner()
        setupColorOrbs()
        setupSaveButton()
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }


    /*--- View Manipulation Methods ---*/

    override fun setupWidgetSelector(widgetNames: List<String>) {
        val widgetAdapter = ArrayAdapter<String>(
            this,
            R.layout.settings_selector_item,
            widgetNames
        )
        widgetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectorWidgetSettingsSelection.adapter = widgetAdapter
        selectorWidgetSettingsSelection.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(a: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    presenter.handleWidgetChange(position)
                }
        }
        textWidgetSettingsSelectionLabel.setOnClickListener {
            selectorWidgetSettingsSelection.performClick()
        }
    }

    override fun populateWidgetSelector(index: Int) {
        selectorWidgetSettingsSelection.setSelection(index)
    }

    override fun populateTextSize(size: TextSize) {
        selectorWidgetSettingsTextSize.setSelection(size.ordinal)
    }

    override fun populateTransparency(transparency: Transparency) {
        selectorWidgetSettingsTransparency.setSelection(transparency.ordinal)
    }

    override fun populateBackgroundColor(color: Int) {
        colorOrbWidgetSettingsBackgroundColor.setColor(color)
    }

    override fun populateTitleColor(color: Int) {
        colorOrbWidgetSettingsTitleColor.setColor(color)
    }

    override fun populateTextColor(color: Int) {
        colorOrbWidgetSettingsTextColor.setColor(color)
    }

    override fun setPreviewTextSize(size: TextSize) {
        widgetSettingsPreview.setTextSize(size)
    }

    override fun setPreviewTransparency(transparency: Transparency) {
        widgetSettingsPreview.setTransparency(transparency)
    }

    override fun setPreviewBackgroundColor(color: Int) {
        widgetSettingsPreview.setBackgroundColor(color)
    }

    override fun setPreviewTitleColor(color: Int) {
        widgetSettingsPreview.setTitleColor(color)
    }

    override fun setPreviewTextColor(color: Int) {
        widgetSettingsPreview.setTextColor(color)
    }

    override fun showNoWidgetsMessage() {
        scrollViewWidgetSettings.visibility = View.GONE
        buttonSave.visibility = View.GONE
        textWidgetSettingsEmpty.visibility = View.VISIBLE
    }

    override fun hideNoWidgetsMessage() {
        scrollViewWidgetSettings.visibility = View.VISIBLE
        buttonSave.visibility = View.VISIBLE
        textWidgetSettingsEmpty.visibility = View.GONE
    }

    override fun showBackgroundColorPickerDialog() {
        Toast.makeText(this, "Background Orb Tap", Toast.LENGTH_SHORT).show()
    }

    override fun showTitleColorPickerDialog() {
        Toast.makeText(this, "Title Orb Tap", Toast.LENGTH_SHORT).show()
    }

    override fun showTextColorPickerDialog() {
        Toast.makeText(this, "Text Orb Tap", Toast.LENGTH_SHORT).show()
    }

    override fun showNavigationDrawer() {
        openDrawer()
    }


    /*--- Private Setup Methods ---*/

    private fun setupToolbar() {
        toolbar.isEditMode = false
        toolbar.setTitle(R.string.widgetTitle)
        toolbar.setLeftButtonImage(R.drawable.icon_menu)
        toolbar.setNoteToolbarListener(object : NoteToolbar.NoteToolbarListener {
            override fun onLeftButtonClick() = presenter.handleMenuButtonClick()
            override fun onRightButtonClick() = Unit
            override fun onTextChange(text: String) = Unit
        })
    }

    private fun setupTextSizeSpinner() {
        val textSizesAdapter =
            ArrayAdapter<String>(this, R.layout.settings_selector_item, TEXT_SIZES)
        textSizesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectorWidgetSettingsTextSize.adapter = textSizesAdapter
        selectorWidgetSettingsTextSize.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(a: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    presenter.handleTextSizeChange(TextSize.values()[position])
                }
            }
        textWidgetSettingsTextSizeLabel.setOnClickListener {
            selectorWidgetSettingsTextSize.performClick()
        }
    }

    private fun setupTransparencySpinner() {
        val transparencyAdapter = ArrayAdapter<String>(
            this,
            R.layout.settings_selector_item,
            TRANSPARENCY_VALUES
        )
        transparencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectorWidgetSettingsTransparency.adapter = transparencyAdapter
        selectorWidgetSettingsTransparency.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(a: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    presenter.handleTransparencyChange(Transparency.values()[position])
                }
            }
        textWidgetSettingsTransparencyLabel.setOnClickListener {
            selectorWidgetSettingsTransparency.performClick()
        }
    }

    fun setupColorOrbs() {

        // Retrieve Theme
        val theme = CrystalNoteTheme.fromCurrentTheme(this)

        // Background Color Orb
        layoutWidgetSettingsBackgroundColor.setOnClickListener {
            colorOrbWidgetSettingsBackgroundColor.performClick()
        }
        textWidgetSettingsBackgroundColorLabel.setOnClickListener {
            colorOrbWidgetSettingsBackgroundColor.performClick()
        }
        colorOrbWidgetSettingsBackgroundColor.setTheme(theme)
        colorOrbWidgetSettingsBackgroundColor.setOnClickListener {
            presenter.handleBackgroundColorClick()
        }

        // Title Color Orb
        layoutWidgetSettingsTitleColor.setOnClickListener {
            colorOrbWidgetSettingsTitleColor.performClick()
        }
        textWidgetSettingsTitleColorLabel.setOnClickListener {
            colorOrbWidgetSettingsTitleColor.performClick()
        }
        colorOrbWidgetSettingsTitleColor.setTheme(theme)
        colorOrbWidgetSettingsTitleColor.setOnClickListener {
            presenter.handleTitleColorClick()
        }

        // Text Color Orb
        layoutWidgetSettingsTextColor.setOnClickListener {
            colorOrbWidgetSettingsTextColor.performClick()
        }
        textWidgetSettingsTextColorLabel.setOnClickListener {
            colorOrbWidgetSettingsTextColor.performClick()
        }
        colorOrbWidgetSettingsTextColor.setTheme(theme)
        colorOrbWidgetSettingsTextColor.setOnClickListener {
            presenter.handleTextColorClick()
        }
    }

    private fun setupSaveButton() {
        buttonSave.setOnClickListener { presenter.handleSaveClick() }
    }

    override fun refreshWidgets() {
        NotesWidgetProvider.refreshWidgets(this, application)
    }


    /*--- Constants ---*/

    companion object {
        private val TEXT_SIZES = TextSize.values().map { it.displayName }
        private val TRANSPARENCY_VALUES = Transparency.values().map { it.displayName }
    }
}
