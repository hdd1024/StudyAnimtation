package com.example.studyanimtation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_draw_paint.*

class DrawPaintActivity : AppCompatActivity() {
    val TAG: String = "DrawPaintActivity.class"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_paint)

        bnt_repeal.setOnClickListener {
            Log.d(TAG, "点击撤销画笔了")
            dpw_draw.drawRestore()
        }

    }
}
