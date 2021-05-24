package com.flyn.mydb.Service;

public class Check {
    public static void hadUseDatabase() {
        if (DBMS.dataDictionary == null) {
            throw new RuntimeException("please select a database");
        }
    }
}
