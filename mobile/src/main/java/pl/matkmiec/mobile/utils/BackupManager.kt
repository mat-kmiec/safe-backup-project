package pl.matkmiec.mobile.utils

import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import android.provider.Telephony
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ContactData(val name: String, val phone: String)

data class SmsData(
    val address: String,
    val body: String,
    val date: Long,
    val type: Int,
    val status: Int
)

object BackupManager {

    fun isDefaultSmsApp(context: Context): Boolean {
        return Telephony.Sms.getDefaultSmsPackage(context) == context.packageName
    }

    fun backupContacts(context: Context): String {
        val list = mutableListOf<ContactData>()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            while (cursor.moveToNext()) {
                val name = if (nameIndex >= 0) cursor.getString(nameIndex) else "Unknown"
                val number = if (numIndex >= 0) cursor.getString(numIndex) else ""
                list.add(ContactData(name ?: "Unknown", number ?: ""))
            }
        }
        return Gson().toJson(list)
    }

    fun restoreContacts(context: Context, payload: String) {
        val typeInfo = object : TypeToken<List<ContactData>>() {}.type
        val currList: List<ContactData>? = try { Gson().fromJson(payload, typeInfo) } catch (e: Exception) { null }
        if (currList.isNullOrEmpty()) return

        for (contact in currList) {
            val ops = ArrayList<ContentProviderOperation>()
            val rawContactInsertIndex = ops.size

            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )

            // Name
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.name)
                    .build()
            )

            // Phone
            if (contact.phone.isNotBlank()) {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.phone)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build()
                )
            }

            try {
                context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun backupSms(context: Context): String {
        val list = mutableListOf<SmsData>()
        val uri = Telephony.Sms.CONTENT_URI
        val projection = arrayOf(
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.TYPE,
            Telephony.Sms.STATUS
        )

        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val addrIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE)
            val typeIndex = cursor.getColumnIndex(Telephony.Sms.TYPE)
            val statusIndex = cursor.getColumnIndex(Telephony.Sms.STATUS)

            while (cursor.moveToNext()) {
                list.add(
                    SmsData(
                        address = if (addrIndex >= 0) cursor.getString(addrIndex) ?: "" else "",
                        body = if (bodyIndex >= 0) cursor.getString(bodyIndex) ?: "" else "",
                        date = if (dateIndex >= 0) cursor.getLong(dateIndex) else 0L,
                        type = if (typeIndex >= 0) cursor.getInt(typeIndex) else Telephony.Sms.MESSAGE_TYPE_INBOX,
                        status = if (statusIndex >= 0) cursor.getInt(statusIndex) else -1
                    )
                )
            }
        }
        return Gson().toJson(list)
    }

    fun restoreSms(context: Context, payload: String) {
        val typeInfo = object : TypeToken<List<SmsData>>() {}.type
        val currList: List<SmsData>? = try { Gson().fromJson(payload, typeInfo) } catch (e: Exception) { null }
        if (currList.isNullOrEmpty()) return

        for (sms in currList) {
            val values = ContentValues().apply {
                put(Telephony.Sms.ADDRESS, sms.address)
                put(Telephony.Sms.BODY, sms.body)
                put(Telephony.Sms.DATE, sms.date)
                put(Telephony.Sms.TYPE, sms.type)
                put(Telephony.Sms.STATUS, sms.status)
                put(Telephony.Sms.READ, 1) // default restored as read
            }
            try {
                // Restoring SMS works seamlessly only on very old Android versions, 
                // or if the app is set as Default SMS App on Android 4.4+
                context.contentResolver.insert(Telephony.Sms.CONTENT_URI, values)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
