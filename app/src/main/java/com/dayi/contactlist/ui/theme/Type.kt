package com.dayi.contactlist.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dayi.contactlist.R

val RulukoRegular = FontFamily(Font(R.font.ruluko_regular))
val NobileBold = FontFamily((Font(R.font.nobile_bold)))
val PavanamRegular = FontFamily(Font(R.font.pavanam_regular))
val PuritanBold = FontFamily(Font(R.font.puritan_bold))
val PuritanRegular = FontFamily(Font(R.font.puritan_regular))
val NobileRegular = FontFamily(Font(R.font.nobile_regular))

// Set of Material typography styles to start with
val Typography = Typography(

    bodyLarge = TextStyle(
        fontFamily = PavanamRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PavanamRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = NobileRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PavanamRegular,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PuritanRegular,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

)
