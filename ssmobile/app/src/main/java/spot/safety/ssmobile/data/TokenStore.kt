package spot.safety.ssmobile.data

import android.content.Context

class TokenStore(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString("jwt", null)
        set(value) {
            if (value == null) prefs.edit().remove("jwt").apply()
            else prefs.edit().putString("jwt", value).apply()
        }

    var userId: Long
        get() = prefs.getLong("userId", -1L)
        set(value) = prefs.edit().putLong("userId", value).apply()

    var classId: Long?
        get() = prefs.getLong("classId", -1L).takeIf { it != -1L }
        set(value) {
            if (value == null) prefs.edit().remove("classId").apply()
            else prefs.edit().putLong("classId", value).apply()
        }

    var username: String?
        get() = prefs.getString("username", null)
        set(value) {
            if (value == null) prefs.edit().remove("username").apply()
            else prefs.edit().putString("username", value).apply()
        }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
