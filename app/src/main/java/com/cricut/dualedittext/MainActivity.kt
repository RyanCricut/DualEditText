package com.cricut.dualedittext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : ComponentActivity() {
    private lateinit var firstEditText: AlwaysFocusedEditText
    private lateinit var secondEditText: HiddenEditText

    private lateinit var container: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dual_edit_text)

        container = findViewById(R.id.container)
        firstEditText = findViewById(R.id.et1)
        secondEditText = findViewById(R.id.et2)

        firstEditText.hiddenEditText = secondEditText
        secondEditText.alwaysFocusEditText = firstEditText

        secondEditText.addTextChangedListener(OtherEditTextTextWatcher(firstEditText))

        container.setOnClickListener {
            firstEditText.dropFocus()
            secondEditText.clearFocus()
        }
    }
}

class OtherEditTextTextWatcher(val other: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        other.text = s
        other.setSelection(s?.length ?: 0)
    }

}

class AlwaysFocusedEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : EditText(context, attrs) {
    private var forceFocus = false

    var hiddenEditText: HiddenEditText? = null

    init {
        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                forceFocus = true

                hiddenEditText?.let {
                    it.requestFocus()

                    val inputMethodManager =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

                    inputMethodManager?.showSoftInput(it.findFocus(), 0)
                }
            }
        }
    }

    override fun isFocused(): Boolean {
        return forceFocus
    }

    fun dropFocus() {
        forceFocus = false

        clearFocus()

        invalidate()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        inputMethodManager?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}

class HiddenEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : EditText(context, attrs) {

    var alwaysFocusEditText: AlwaysFocusedEditText? = null

    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        // No op
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        alwaysFocusEditText?.setSelection(selStart, selEnd)

        super.onSelectionChanged(selStart, selEnd)
    }
}