@file:Suppress("unused")

package com.akshay.harsoda.android.base.helper.base.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlin.math.roundToInt

//<editor-fold desc="For View Data">
/**
 * Show the view  (visibility = View.VISIBLE)
 */
inline val View.visible: View
    get() {
        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }
        return this
    }

/**
 * Hide the view. (visibility = View.INVISIBLE)
 */
inline val View.invisible: View
    get() {
        if (visibility != View.INVISIBLE) {
            visibility = View.INVISIBLE
        }
        return this
    }

/**
 * Remove the view (visibility = View.GONE)
 */
inline val View.gone: View
    get() {
        if (visibility != View.GONE) {
            visibility = View.GONE
        }
        return this
    }

/**
 * Remove the view (View.setEnable(true))
 */
inline val View.enable: View
    get() {
        isEnabled = true
        return this
    }

/**
 * Remove the view (View.setEnable(false))
 */
inline val View.disable: View
    get() {
        isEnabled = false
        return this
    }
inline val EditText.convertTextView: View
    get() {
        this.isFocusable = false
        this.isFocusableInTouchMode = false
        this.isClickable = false
        this.isClickable = false
        this.isContextClickable = false
        this.isLongClickable = false
        this.linksClickable = false
        return this
    }
inline val EditText.convertEditText: View
    get() {
        this.isFocusable = true
        this.isFocusableInTouchMode = true
        this.isClickable = true
        this.isClickable = true
        this.isContextClickable = true
        this.isLongClickable = true
        this.linksClickable = true
        return this
    }

fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) invisible else visible

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) visible else gone

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

/**
 * Extension method to get LayoutInflater
 */
inline val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)
//</editor-fold>

//<editor-fold desc="For get Display Data">
/**
 * Extension method to get theme for Context.
 */
inline val Context.isDarkTheme: Boolean get() = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * Extension method to find a device width in pixels
 */
inline val Context.displayWidth: Int get() = resources.displayMetrics.widthPixels

/**
 * Extension method to find a device height in pixels
 */
inline val Context.displayHeight: Int get() = resources.displayMetrics.heightPixels

/**
 * Extension method to find a device density
 */
inline val Context.displayDensity: Float get() = resources.displayMetrics.density

/**
 * Extension method to find a device density in DPI
 */
inline val Context.displayDensityDpi: Int get() = resources.displayMetrics.densityDpi

/**
 * Extension method to find a device DisplayMetrics
 */
inline val Context.displayMetrics: DisplayMetrics get() = resources.displayMetrics
//</editor-fold>

//<editor-fold desc="For Text Entity">
inline val String.toEditable: Editable get() = Editable.Factory.getInstance().newEditable(this)

inline val String.removeMultipleSpace: String get() = this.trim().replace("\\s+".toRegex(), " ")

inline val String.getFromHtml: Spanned
    get() {
//        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        return Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    }
//</editor-fold>

//<editor-fold desc="For KeyBord">
/**
 * Extension method for Hide Key Bord And Clear Focus
 */
inline val View.hideKeyBord: Unit
    get() {
        this.clearFocus()
        (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(this.windowToken, 0)
    }

/**
 * Extension method for Hide Key Bord And Don't Clear Focus
 */
inline val EditText.hideKeyBordWithOutClearFocus: Unit
    get() {
        this.requestFocus()

        (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(this.windowToken, 0)

        this.showSoftInputOnFocus = false
    }

/**
 * Extension method for Showing Key Bord
 */
inline val View.showKeyBord: Unit
    get() {
        this.requestFocus()
        (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, 0)
    }
//</editor-fold>

//<editor-fold desc="For Default Display Bar Height">
/**
 * Extension method for get StatusBar Height
 */
inline val Activity.statusBarHeight: Int
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    get() {
        val resourceId =
            baseContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

/**
 * Extension method for get NavigationBar Height
 */
inline val Activity.navigationBarHeight: Int
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    get() {
        val resourceId =
            baseContext.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
//</editor-fold>

inline val Double.roundToHalf: Double get() = ((this * 2).roundToInt() / 2.0)

inline val Context.isValidContextForGlide: Boolean
    get() {
        return !(this is Activity && this.isFinishing)
    }

fun View.onGlobalLayout(callback: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            callback()
        }
    })
}

inline val String.isDigitOnly: Boolean get() = this.matches("-?[0-9]+?".toRegex())
