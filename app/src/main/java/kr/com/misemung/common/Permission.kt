package kr.com.misemung.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class Permission() {
    @RequiresApi(Build.VERSION_CODES.Q)
    val PERMISSION_MANDATORY = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    fun hasMandatoryPermission(ctx: Context): Boolean {
        for (permission in PERMISSION_MANDATORY) {
            val accessFineLocation = ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            // ACCESS_COARSE_LOCATION 권한이 true 이면, ACCESS_BACKGROUND_LOCATION 권한 PASS.
            if (ContextCompat.checkSelfPermission(ctx, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (accessFineLocation) {
                    continue
                } else {
                    return false
                }
            }
        }
        return true
    }
}