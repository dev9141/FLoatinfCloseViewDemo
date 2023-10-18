package com

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.firebase.floatinfcloseviewdemo.R
import java.lang.StringBuilder
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    private lateinit var floatingView: FrameLayout
    private lateinit var closeView: FrameLayout
    private var initialX: Float = 0F
    private var initialY: Float = 0F
    private var initialTouchX: Float = 0F
    private var initialTouchY: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingView = findViewById(R.id.floatingView)
        closeView = findViewById(R.id.closeView)

        floatingView.setOnTouchListener { _, event ->
            handleTouch(event, floatingView)
        }

        val loanAmount = 5000000.0
        val interestRate = 9.0 / 12 / 100 // Monthly interest rate
        val loanTenureMonths = 20.0 * 12 // Loan tenure in months

        val emi = calculateEMI(loanAmount, interestRate, loanTenureMonths)
        val totalInterest = emi * loanTenureMonths - loanAmount
        val totalPayment = emi * loanTenureMonths

        val result = StringBuilder()
        result.append("EMI: ₹${String.format("%.2f", emi)}")
        result.append("\n")
        result.append("Total Interest: ₹${String.format("%.2f", totalInterest)}")
        result.append("\n")
        result.append("Total Payment: ₹${String.format("%.2f", totalPayment)}")



        val number = 999999999999999
        val numberInWords = convertNumberToWords(number)
        result.append("$number in words is: $numberInWords")

        showResultDialog(result.toString())


    }

    private fun handleTouch(event: MotionEvent, view: View): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialX = view.x
                initialY = view.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - initialTouchX
                val dy = event.rawY - initialTouchY

                view.x = initialX + dx
                view.y = initialY + dy

                // Check if the floatingView is at the bottom of the screen
                if (view.y + view.height >= closeView.top) {
                    closeView.visibility = View.VISIBLE
                } else {
                    closeView.visibility = View.INVISIBLE
                }
            }
            MotionEvent.ACTION_UP -> {
                // Check if the floatingView is over the close view
                if (isViewOverlapping(floatingView, closeView)) {
                    closeApp()
                }
            }
        }
        return true
    }


    private fun isViewOverlapping(view1: View, view2: View): Boolean {
        val rect1 = Rect()
        val rect2 = Rect()
        view1.getGlobalVisibleRect(rect1)
        view2.getGlobalVisibleRect(rect2)
        return rect1.intersect(rect2)
    }

    private fun closeApp() {
        finish()
    }

    private fun calculateEMI(loanAmount: Double, interestRate: Double, loanTenureMonths: Double): Double {
        val emi = loanAmount * interestRate * (1 + interestRate).pow(loanTenureMonths) /
                ((1 + interestRate).pow(loanTenureMonths) - 1)
        return emi
    }

    fun showResultDialog(result: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("EMI Result")
        builder.setMessage("$result")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    fun convertNumberToWords(num: Long): String {
        var number = num
        val units = arrayOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine")
        val teens = arrayOf("Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
        val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

        if (number == 0L) {
            return "Zero"
        }

        if (number < 0) {
            return "Negative " + convertNumberToWords(-number)
        }

        var words = ""

        if (number / 1_000_000_000_000_000 > 0) {
            words += convertNumberToWords(number / 1_000_000_000_000_000) + " Quadrillion "
            number %= 1_000_000_000_000_000
        }

        if (number / 1_000_000_000_000 > 0) {
            words += convertNumberToWords(number / 1_000_000_000_000) + " Trillion "
            number %= 1_000_000_000_000
        }

        if (number / 1_000_000_000 > 0) {
            words += convertNumberToWords(number / 1_000_000_000) + " Billion "
            number %= 1_000_000_000
        }

        if (number / 1_000_000 > 0) {
            words += convertNumberToWords(number / 1_000_000) + " Million "
            number %= 1_000_000
        }

        if (number / 1_000 > 0) {
            words += convertNumberToWords(number / 1_000) + " Thousand "
            number %= 1_000
        }

        if (number / 100 > 0) {
            words += units[(number / 100).toInt()] + " Hundred "
            number %= 100
        }

        if (number >= 10 && number <= 19) {
            words += teens[(number - 10).toInt()]
        } else {
            words += tens[(number / 10).toInt()]
            if (number % 10 > 0) {
                val x = units[(number % 10).toInt()]
                words += " " + x.toLowerCase()
            }
        }

        return words.trim()
    }
}