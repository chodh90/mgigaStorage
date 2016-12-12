package com.kt.gigastorage.mobile.service;

import java.text.DecimalFormat;

/**
 * Created by araise on 2016-11-23.
 */

public class KbConverter {
    private static DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
    private static final double BYTE = 1024, KB = BYTE, MB = KB*BYTE, GB = MB*BYTE;
    public static String convertBytesToSuitableUnit(long bytes){
        String bytesToSuitableUnit= bytes + "B";

        if(bytes >= GB) {
            double tempBytes = bytes/GB;
            bytesToSuitableUnit = twoDecimalForm.format(tempBytes) + "GB";
            return bytesToSuitableUnit;
        }

        if(bytes >= MB) {
            double tempBytes = bytes/MB;
            bytesToSuitableUnit = twoDecimalForm.format(tempBytes) + "MB";
            return bytesToSuitableUnit;
        }

        if(bytes >= KB) {
            double tempBytes = bytes/KB;
            bytesToSuitableUnit = twoDecimalForm.format(tempBytes) + "KB";
            return bytesToSuitableUnit;
        }

        return bytesToSuitableUnit;
    }
}
