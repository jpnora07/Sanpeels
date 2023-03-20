package com.example.tracking

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import kotlin.math.min

class Home_Rider : AppCompatActivity() {
    private var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_rider)

        val signoutbtn = findViewById<CardView>(R.id.cardLogout)
        val profilebtn = findViewById<CardView>(R.id.cardProfile)
        val locationbtn = findViewById<CardView>(R.id.cardlocation)
        val calendarbtn = findViewById<CardView>(R.id.cardCalendar)
        val notifbtn = findViewById<CardView>(R.id.cardNotification)
        val aboutbtn = findViewById<CardView>(R.id.cardAboutus)

        profilebtn.setOnClickListener {
            val intent = Intent(this, Manageprofile::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or FLAG_ACTIVITY_PREVIOUS_IS_TOP)
            startActivity(intent)

        }

        calendarbtn.setOnClickListener {
            val intent = Intent(this, CalendarViewer_Rider::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or FLAG_ACTIVITY_PREVIOUS_IS_TOP)
            startActivity(intent)
        }

        signoutbtn.setOnClickListener {
            val intent = Intent(this, login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        locationbtn.setOnClickListener {
            val intent = Intent(this, MapActivity_Rider::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or FLAG_ACTIVITY_PREVIOUS_IS_TOP)
            startActivity(intent)
        }

        notifbtn.setOnClickListener {
            val intent = Intent(this, Notification_Rider::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or FLAG_ACTIVITY_PREVIOUS_IS_TOP)
            startActivity(intent)

        }

        aboutbtn.setOnClickListener {

            // Specifying the viewGroup as a parent to the inflater makes no difference.
            val dialogView =
                layoutInflater.inflate(R.layout.activity_about_us, null, false) as ConstraintLayout
            (dialogView.findViewById(R.id.textView) as TextView).text = getString(R.string.mission)

            val alertDialog = AlertDialog.Builder(this).setView(dialogView).create()

            val decorView = alertDialog.window!!.decorView
            decorView.setBackgroundResource(R.drawable.border)

            // We need a layout pass to determine how big everything is and needs to be. Place a hook
            // at the end of the layout process to examine the layout before display.
            decorView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    // Find out how much of the scrolling view is usable by its child.
                    val scrollingView: NestedScrollView = decorView.findViewById(R.id.filter_scroll)
                    val scrollingViewPadding =
                        scrollingView.paddingTop + scrollingView.paddingBottom
                    val scrollingUsableHeight = scrollingView.height - scrollingViewPadding

                    // If the child view fits in the scrolling view, then we are done.
                    val childView = scrollingView.getChildAt(0)
                    if (childView.height <= scrollingUsableHeight) {
                        return
                    }

                    // Child doesn't currently fit in the scrolling view. Resize the top-level
                    // view so the child either fits or is forced to scroll because the maximum
                    // height is reached. First, find out how much space is allowed by the decor view.
                    val displayRectangle = Rect()
                    decorView.getWindowVisibleDisplayFrame(displayRectangle)
                    val decorViewPadding = decorView.paddingTop + decorView.paddingBottom
                    val decorUsableHeight =
                        displayRectangle.height() - decorViewPadding - scrollingViewPadding

                    // Compute the height of the dialog that will 100% fit the scrolling content and
                    // reduce it if it won't fit in the maximum allowed space.
                    val heightToFit = dialogView.height + childView.height - scrollingUsableHeight
                    dialogView.minHeight = min(decorUsableHeight, heightToFit)
                }
            })
            val buttonOk: Button = dialogView.findViewById(R.id.dialog_primary_button)
            buttonOk.setOnClickListener { alertDialog.dismiss() }
            //buttonOk = dialogView.findViewById(R.id.dialog_secondary_button)
            //buttonOk.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
        }
    }

    override fun onBackPressed() {

        if (backPressedTime + 3000 > System.currentTimeMillis()) {

            @Suppress("DEPRECATION")
            super.onBackPressed()

            finish()

        } else {

            Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG).show()

        }

        backPressedTime = System.currentTimeMillis()

    }
}