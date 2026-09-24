import android.content.Context

// BranchManager.kt
object BranchManager {
    private const val PREF_NAME = "branch_prefs"
    private const val KEY_BRANCH_NAME = "branch_name"
    private const val KEY_BRANCH_ADDRESS = "branch_address"
    private const val KEY_BRANCH_PHONE = "branch_phone"
    private const val KEY_BRANCH_ID = "branch_id"

    fun saveBranch(context: Context, name: String, address: String, phone: String, id: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().apply {
            putString(KEY_BRANCH_NAME, name)
            putString(KEY_BRANCH_ADDRESS, address)
            putString(KEY_BRANCH_PHONE, phone)
            putInt(KEY_BRANCH_ID, id)
            apply()
        }
    }

    fun getBranchName(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_BRANCH_NAME, null) ?: "Выберите филиал"
    }

    fun getBranchAddress(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_BRANCH_ADDRESS, "") ?: ""
    }

    fun getBranchPhone(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_BRANCH_PHONE, "") ?: ""
    }

    fun getBranchId(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_BRANCH_ID, -1)
    }

    fun hasBranch(context: Context): Boolean {
        return getBranchId(context) != -1
    }
}