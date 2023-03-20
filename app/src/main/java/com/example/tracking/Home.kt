package com.example.tracking

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
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

class Home : AppCompatActivity() {
    private var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val signoutbtn = findViewById<CardView>(R.id.cardLogout)
        val profilebtn = findViewById<CardView>(R.id.cardProfile)
        val locationbtn = findViewById<CardView>(R.id.cardlocation)
        val calendarbtn = findViewById<CardView>(R.id.cardCalendar)
        val notifbtn = findViewById<CardView>(R.id.cardNotification)
        val aboutbtn = findViewById<CardView>(R.id.cardAboutus)

        profilebtn.setOnClickListener {
            val intent = Intent(this, Manageprofile::class.java)
            startActivity(intent)
        }

        calendarbtn.setOnClickListener {
            val intent = Intent(this, CalendarViewer::class.java)
            startActivity(intent)
        }

        signoutbtn.setOnClickListener{
                val intent = Intent(this, login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        locationbtn.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        notifbtn.setOnClickListener{
            val intent = Intent(this, Notification::class.java)
            startActivity(intent)

        }

        //about us button dialog
        aboutbtn.setOnClickListener{
            // Specifying the viewGroup as a parent to the inflater makes no difference.
            val dialogView = layoutInflater.inflate(R.layout.activity_about_us, null, false) as ConstraintLayout
            (dialogView.findViewById(R.id.textView) as TextView).text = getString(R.string.mission)
            val alertDialog = AlertDialog.Builder(this).setView(dialogView).create()
            val decorView = alertDialog.window!!.decorView
            decorView.setBackgroundResource(R.drawable.border)
            decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    // Find out how much of the scrolling view is usable by its child.
                    val scrollingView: NestedScrollView = decorView.findViewById(R.id.filter_scroll)
                    val scrollingViewPadding = scrollingView.paddingTop + scrollingView.paddingBottom
                    val scrollingUsableHeight = scrollingView.height - scrollingViewPadding

                    // If the child view fits in the scrolling view, then we are done.
                    val childView = scrollingView.getChildAt(0)
                    if (childView.height <= scrollingUsableHeight) {
                        return}
                    val displayRectangle = Rect()
                    decorView.getWindowVisibleDisplayFrame(displayRectangle)
                    val decorViewPadding = decorView.paddingTop + decorView.paddingBottom
                    val decorUsableHeight = displayRectangle.height() - decorViewPadding - scrollingViewPadding

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