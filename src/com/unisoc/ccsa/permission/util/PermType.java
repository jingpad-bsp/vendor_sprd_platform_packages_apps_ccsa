package com.unisoc.ccsa.permission.util;

import java.util.HashMap;

import android.Manifest.permission;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.unisoc.ccsa.Log;
import com.unisoc.ccsa.R;

public class PermType {

    private static final String TAG = "PermType";
    public static final int PERM_GROUP_COMMICATION = 1;
    public static final int PERM_GROUP_SENSTIVE = 2;
    public static final int PERM_GROUP_INTERFACE = 3;

    public static final int BASE_NUMBER = 100;

    private static final String PERM_CALL_PHONE = "101";
    private static final String PERM_SEND_SMS = "102";
    public static final String PERM_SEND_MMS = "103";
    public static final String PERM_SEND_EMAIL = "104";
    private static final String PERM_NET_STATE = "105";
    private static final String PERM_WIFI_STATE = "106";

    private static final String PERM_LOCATION = "201";
    private static final String PERM_RECORD = "202";
    //private static final String PERM_LOCAL_RECORD = "210";
    private static final String PERM_CAMERA = "204";

    public static final String PERM_WRITE_CONTACTS = "205";
    public static final String PERM_WRITE_CALL_LOG = "206";
    private static final String PERM_WRITE_SMS = "207";

    public static final String PERM_WRITE_MMS = "208";

    public static final String PERM_READ_CONTACTS = "209";
    public static final String PERM_READ_CALL_LOG = "210";

    private static final String PERM_READ_SMS = "211";
    public static final String PERM_READ_MMS = "212";
    public static final String PERM_READ_PROTECTED_FILES = "213";

    public static final String PERM_BLUETOOTH = "301";

    //add new permission
    //  private static final String PERM_OPEN_NFC="302";

    public static final HashMap<String, Integer> PERMNAME = new HashMap<String, Integer>() {
        {
            put(PERM_WIFI_STATE, R.string.wifi_state);
            put(PERM_CALL_PHONE, R.string.call_phone);
            put(PERM_SEND_SMS, R.string.send_sms);
            put(PERM_SEND_MMS, R.string.send_mms);
            put(PERM_SEND_EMAIL, R.string.send_email);
            put(PERM_NET_STATE, R.string.network_state);

            put(PERM_READ_MMS, R.string.read_mms);
            put(PERM_LOCATION, R.string.location_access);
            put(PERM_RECORD, R.string.record_audio);
            //put(PERM_LOCAL_RECORD, R.string.local_record_audio);
            put(PERM_CAMERA, R.string.camera);
            put(PERM_WRITE_CONTACTS, R.string.write_contacts);
            put(PERM_WRITE_CALL_LOG, R.string.write_call_log);
            put(PERM_WRITE_SMS, R.string.write_sms);
            put(PERM_WRITE_MMS, R.string.write_mms);
            put(PERM_READ_CONTACTS, R.string.read_contact);
            put(PERM_READ_CALL_LOG, R.string.read_call_log);
            put(PERM_READ_SMS, R.string.read_sms);
            put(PERM_READ_PROTECTED_FILES, R.string.read_protected_files);

            put(PERM_BLUETOOTH, R.string.blue_tooth);
            //       put(PERM_OPEN_NFC,R.string.open_nfc);

        }
    };
    public static final HashMap<String, String> PERMTYPE =
            new HashMap<String, String>() {
                {
                    put(permission.CHANGE_WIFI_STATE, PERM_WIFI_STATE);
                    put(permission.CALL_PHONE, PERM_CALL_PHONE);
                    put(permission.CALL_PRIVILEGED, PERM_CALL_PHONE);
                    put(permission.SEND_SMS, PERM_SEND_SMS);
                    put(permission.CHANGE_NETWORK_STATE, PERM_NET_STATE);

                    put(permission.ACCESS_COARSE_LOCATION, PERM_LOCATION);
                    put(permission.ACCESS_FINE_LOCATION, PERM_LOCATION);
                    put(permission.RECORD_AUDIO, PERM_RECORD);
                    put(permission.CAMERA, PERM_CAMERA);
                    put(permission.WRITE_CONTACTS, PERM_WRITE_CONTACTS);
                    //permission.WRITE_SMS
                    put("android.permission.WRITE_SMS", PERM_WRITE_SMS);
                    put(permission.READ_CONTACTS, PERM_READ_CONTACTS);
                    put(permission.READ_SMS, PERM_READ_SMS);
                    put(permission.RECEIVE_MMS, PERM_READ_MMS);
                    put(permission.RECEIVE_SMS, PERM_READ_SMS);
                    put(permission.MEDIA_CONTENT_CONTROL, PERM_READ_PROTECTED_FILES);
                    put(permission.RECEIVE_WAP_PUSH, PERM_READ_SMS);
                    if (Build.VERSION.SDK_INT >= 16) {
                        Log.i(TAG, " >=16 callLog");
                        put(permission.READ_CALL_LOG, PERM_READ_CALL_LOG);
                        put(permission.WRITE_CALL_LOG, PERM_WRITE_CALL_LOG);
                    }

                    put(permission.BLUETOOTH_ADMIN, PERM_BLUETOOTH);
                    //       put(permission.NFC, PERM_OPEN_NFC);
                }
            };

    private static final String SECURITY_DB = "/system/etc/telephonesec.db";
    private static final String POLICYID_TABLE = "opr2policy";
    private static final String KEYWORDS_TABLE = "opr2keywords";
    private static final String POLICY_ID = "policy_id";

    public static List<Integer> getPolicyIDFromDB() {
        Cursor cr = null;
        SQLiteDatabase db;
        List<Integer> policyIDList = new ArrayList<Integer>();

        db = SQLiteDatabase.openDatabase(SECURITY_DB, null, SQLiteDatabase.OPEN_READONLY);
        if (null == db) {
            Log.d(TAG, "getPolicyIDFromDB, open db fail!");
            return null;
        }

        cr = db.query(POLICYID_TABLE, new String[]{POLICY_ID}, null, null, null, null, null);
        if (null == cr || !cr.moveToFirst()) {
            Log.d(TAG, "POLICYID_TABLE, cr is null!");
            return null;
        }
        do {
            int policy_id = cr.getInt(0);
            Log.d(TAG, "POLICYID_TABLE, policy_id: " + policy_id);
            if (policyIDList.contains(policy_id)) {
                continue;
            }
            policyIDList.add(new Integer(policy_id));
        } while (cr.moveToNext());
        cr.close();

        cr = db.query(KEYWORDS_TABLE, new String[]{POLICY_ID}, null, null, null, null, null);
        if (null == cr || !cr.moveToFirst()) {
            Log.d(TAG, "KEYWORDS_TABLE, cr is null!");
            return null;
        }
        do {
            int policy_id = cr.getInt(0);
            Log.d(TAG, "KEYWORDS_TABLE, policy_id: " + policy_id);
            if (policyIDList.contains(policy_id)) {
                continue;
            }
            policyIDList.add(new Integer(policy_id));
        } while (cr.moveToNext());
        cr.close();

        db.close();

        return policyIDList;
    }

    public static boolean isValidPermission(String str) {
        int policy_id = Integer.parseInt(str);
        List<Integer> policyIDList = getPolicyIDFromDB();
        if ( null == policyIDList ) {
            return false;
        }
        return policyIDList.contains(policy_id) && PermType.PERMNAME.containsKey(str);
    }

}
