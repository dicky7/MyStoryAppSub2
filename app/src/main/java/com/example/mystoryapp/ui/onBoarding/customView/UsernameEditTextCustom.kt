package com.example.mystoryapp.ui.onBoarding.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.mystoryapp.R

class UsernameEditTextCustom:  AppCompatEditText {

    private lateinit var usernameIconDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
    ) {
        init()
    }

    @SuppressLint("NewApi")
    private fun init() {
        usernameIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_24) as Drawable
        inputType = InputType.TYPE_CLASS_TEXT
        compoundDrawablePadding = 16

        setHint(R.string.username)
        setAutofillHints(AppCompatEditText.AUTOFILL_HINT_USERNAME)
        setDrawable(usernameIconDrawable)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Display error automatically if the username is empty
                if (s.isNullOrEmpty())
                    error = context.getString(R.string.error_username)
            }
        })
    }

    private fun setDrawable(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }
}