package com.example.jimenez.appmunitacna.objects;

import com.example.jimenez.appmunitacna.Clases.Usuario;
import com.google.firebase.auth.FirebaseUser;

public class Global {
    public static FirebaseUser globalDataUser;
    public static Usuario currentDataUser;
    public static String userKey;

    public Global() {
    }

    public static String getUserKey() {
        return userKey;
    }

    public static void setUserKey(String userKey) {
        Global.userKey = userKey;
    }


    public static Usuario getCurrentDataUser() {
        return currentDataUser;
    }

    public static void setCurrentDataUser(Usuario currentDataUser) {
        Global.currentDataUser = currentDataUser;
    }

    public static FirebaseUser getGlobalDataUser() {
        return globalDataUser;
    }

    public static void setGlobalDataUser(FirebaseUser globalDataUser) {
        Global.globalDataUser = globalDataUser;
    }


}
