package com.uoko.frame.common

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Created by Administrator 可爱的路人 on 2018/9/18 0018.
 * Email:513421345@qq.com
 * TODO
 */
private var toast: Toast? = null

fun clearToast() {
    toast?.cancel()
    toast = null
}

/**
 * 短时间显示Toast【居下】
 * @param msg 显示的内容-字符串
 */
fun Context.showShortToast(msg: String?) {
    showToast(msg)
}

fun Fragment.showShortToast(msg: String?) {
    showToast(msg)
}

/**
 * 长时间显示Toast【居下】
 * @param msg 显示的内容-字符串
 */
fun Context.showLongToast(msg: String?) {
    showToast(msg, Toast.LENGTH_LONG)
}

fun Fragment.showLongToast(msg: String?) {
    showToast(msg, Toast.LENGTH_LONG)
}

fun View.click(Oncli:View.OnClickListener){


    this.setOnClickListener(Oncli)

}

/**
 * 长时间显示Toast【居下】
 * @param msg 显示的内容-字符串
 */
fun Context.showToast(msg: String? = null, durationShow: Int = Toast.LENGTH_SHORT) {
    msg ?: return
    toast?.cancel()
    toast = Toast.makeText(this, msg, durationShow)
    toast?.show()
}

fun Fragment.showToast(msg: String? = null, durationShow: Int = Toast.LENGTH_SHORT) {
    context?.showToast(msg, durationShow)
}

fun Fragment.showToast(msg: Int = 0, durationShow: Int = Toast.LENGTH_SHORT) {
    context?.showToast(context?.getString(msg), durationShow)
}

/**
 * Created by Administrator 可爱的路人 on 2018/8/31 0031.
 * Email:513421345@qq.com
 * TODO
 */
fun BigDecimal?.getPrice(): String {
    return this?.let {
        val remValue = it.rem(BigDecimal.ONE).setScale(2, BigDecimal.ROUND_HALF_UP).toString()
        return if (remValue == "0.00" || remValue == "0") {
            it.setScale(0, BigDecimal.ROUND_HALF_UP).toString()
        } else {
            it.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
        }
    } ?: "0"
}

fun BigDecimal?.getPercent(): String {
    return this?.let {
        "${(it * BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP)}%"
    } ?: "0%"
}

fun BigDecimal?.getPercentHundred(): String {
    return this?.let {
        "${it.setScale(0, BigDecimal.ROUND_HALF_UP)}%"
    } ?: "0%"
}

fun Double?.getPrice(): String {
    return this?.let {
        BigDecimal(it).getPrice()
    } ?: "0"
}

fun Float?.getPrice(): String {
    return this?.toDouble().getPrice()
}

fun BigDecimal?.getPrice2(): String {
    return this?.let {
        return it.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
    } ?: "0.00"
}

/**
 * 前面加人民币符号
 */
fun BigDecimal?.getSignPrice2(): String {
    return this?.let {
        return "￥" + it.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
    } ?: "￥0.00"
}

/**
 * 后面加人民币符号
 */
fun BigDecimal?.getPrice2Sign(): String {
    return this?.let {
        return it.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "￥"
    } ?: "0.00￥"
}


fun Double?.getPrice2(): String {
    return this?.let {
        BigDecimal(it).getPrice2()
    } ?: "0.00"
}

fun Float?.getPrice2(): String {
    return this?.toDouble().getPrice2()
}

/**
 * 转换成面积，带单位
 */
fun BigDecimal?.getArea(): String {
    return getPrice() + "m²"
}

fun Double?.normalFormat(): String {
    return this?.let {
        DecimalFormat.getNumberInstance().format(it)
    } ?: ""
}

/**
 * 重载bigDecimal的..操作符
 */
operator fun BigDecimal.rangeTo(other: BigDecimal): IntRange {
    return this.intValueExact()..other.intValueExact()
}

/**
 * Value of dp to value of px.
 *
 * @param dpValue The value of dp.
 * @return value of px
 */

fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * Value of px to value of dp.
 *
 * @param pxValue The value of px.
 * @return value of dp
 */
fun Context.px2dp(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * Value of sp to value of px.
 *
 * @param spValue The value of sp.
 * @return value of px
 */
fun Context.sp2px(spValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}

/**
 * Value of px to value of sp.
 *
 * @param pxValue The value of px.
 * @return value of sp
 */
fun Context.px2sp(pxValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

val dateFromatyyyy_MM_ddbHH_mm_ss by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE) }
val dateFromatyyyy_MM_dd by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE) }
/**
 * 2018-09-06 18:00:00变成2018-09-06,可以用{@link splitbget1}代替
 */
fun String?.parseyyyy_MM_ddbHH_mm_ssToyyyy_MM_dd(): String {
    return this?.let {
        dateFromatyyyy_MM_dd.format(dateFromatyyyy_MM_ddbHH_mm_ss.parse(it))
    } ?: ""
}

fun Date.toyyyy_MM_dd(): String {
    return dateFromatyyyy_MM_dd.format(this)
}

/**
 * 以空格切分字符串，取第0个
 */
fun String?.splitbget1(): String {
    return this?.let {
        it.split(" ")[0]
    } ?: ""
}

/**
 * 对象变map
 */
fun Any.transformDataClassToMap(): MutableMap<String, Any> {
    val dataClassFields = this::class.java.declaredFields
    val result = LinkedHashMap<String, Any>()
    dataClassFields?.forEach {
        it.isAccessible = true
        if (it.name != null && it.get(this) != null && it.name != "serialVersionUID") {
            result[it.name] = it.get(this)
        }
        it.isAccessible = false
    }
    return result
}

/**
 * MAP 变对象
 */
fun <T> Map<String, Any>.transformToData(clazz: Class<T>): T {
    val dataClassFields = clazz.declaredFields
    val result = clazz.newInstance()
    dataClassFields?.forEach {
        it.isAccessible = true
        if (it.name != null && it.get(this) != null && it.name != "serialVersionUID" && this.containsKey(it.name)) {
            it.set(result, this[it.name])
        }
        it.isAccessible = false
    }
    return result
}

private val random: Random by lazy { Random() }
fun getRandomInt(): Int {
    return random.nextInt()
}

/**
 * 过滤所有非打印字符
 */
fun String?.trimAllBlank(): String? {
    return this?.replace(Regex("\\s"), "")
}

/**
 * 过滤首尾空字符，包括空格，换行等
 */
private val blankTrimRegex = Regex("\\s")

fun String?.trimFirstAndEndBlank(): String? {
    return this?.trim { char -> blankTrimRegex.matches(char.toString()) }
}

fun String.isAfterCurrentTime(): Boolean? {
    try {
        val date = dateFromatyyyy_MM_ddbHH_mm_ss.parse(this)
        return date.time > System.currentTimeMillis()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Date.isAfterCurrentTime(): Boolean {
    return this.time > System.currentTimeMillis()
}

fun isBigYear(year: Int): Boolean {
    return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
}

/**
 * 某年某月有多少天
 */
fun daysOfDate(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        else -> if (isBigYear(year)) 29 else 28
    }
}

/**
 * 某年某月有多少天
 */
fun daysOfDate(date: Date): Int {
    return when (date.month + 1) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        else -> if (isBigYear(date.year + 1900)) 29 else 28
    }
}