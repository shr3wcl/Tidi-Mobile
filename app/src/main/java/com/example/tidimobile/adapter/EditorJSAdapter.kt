package com.example.tidimobile.adapter

import android.content.Context
import work.upstarts.editorjskit.ui.EditorJsAdapter
import work.upstarts.editorjskit.ui.theme.EJStyle

class EditorJSAdapter(context: Context) {
    private val rvAdapter: EditorJsAdapter by lazy {
        EditorJsAdapter(EJStyle.create(context))
    }
}