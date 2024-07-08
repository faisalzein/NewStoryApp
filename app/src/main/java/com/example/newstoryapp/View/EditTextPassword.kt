package com.example.newstoryapp.View

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.newstoryapp.R


class EditTextPassword : AppCompatEditText {


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


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Password" // Menetapkan hint sebagai "Password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START // Menetapkan perataan teks ke awal
    }


    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }


            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (!text.isNullOrBlank()) {
                    error = if (text!!.length < 8) {
                        resources.getString(R.string.message_pass)
                    } else {
                        null
                    }
                }
            }

            // Setelah teks diubah
            override fun afterTextChanged(s: Editable) {
            }
        })
    }
}