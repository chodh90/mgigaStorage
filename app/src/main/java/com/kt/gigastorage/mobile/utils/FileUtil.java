package com.kt.gigastorage.mobile.utils;

import android.content.Context;
import android.os.Environment;

import com.kt.gigastorage.mobile.activity.MainActivity;
import com.kt.gigastorage.mobile.activity.R;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a-raise on 2016-09-29.
 */
public class FileUtil {

    private static Context context = MainActivity.context;

    private static String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private static String projectName = context.getString(R.string.rootFoldrNm) ;

    private static String apsolutePath = externalPath + "/" +projectName;

    private static List<Map<String,String>> foldrFileArray = new ArrayList<Map<String,String>>();

    private static List<Map<String,String>> foldrFile = new ArrayList<Map<String,String>>();

    public static String filePath() {

        File folder = new File(apsolutePath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        return apsolutePath;
    }

    public static List<Map<String,String>> matching() {
        foldrFileArray = new ArrayList<Map<String,String>>();
        filePath();

        File path = new File(apsolutePath);

        File[] fileList = path.listFiles();

        Map<String,String> foldrMap = new HashMap<>();
        String foldrWholePathNm = "/Mobile";
        Date foldrDate = new Date();
        DateFormat foldrFormat = new SimpleDateFormat("yyyy-MM-dd");
        foldrMap.put("foldrWholePathNm",foldrWholePathNm);
        foldrMap.put("foldrNm","Mobile");
        foldrMap.put("foldrSize","");
        foldrMap.put("foldrAmdDate",""+foldrFormat.format(foldrDate));
        foldrFileArray.add(foldrMap);

        for (int j = 0; j < fileList.length; j++) {
            foldrFile = new ArrayList<Map<String,String>>();

            if (fileList[j].isDirectory()) {
                List<Map<String,String>> foldrFile = subDirList(path+"/"+fileList[j].getName());
                for(int i=0; i<foldrFile.size(); i++){
                    foldrFileArray.add(foldrFile.get(i));
                }
            }else if(fileList[j].isFile()){

                Map<String,String> fileMap = new HashMap<>();
                String fileWholePathNm = fileList[j].getAbsolutePath().replace(externalPath, "");
                String fileFoldrWholePathNm = fileList[j].getParent().replace(externalPath, "");
                Date fileDate = new Date(fileList[j].lastModified());
                DateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd");
                fileMap.put("fileWholePathNm",fileWholePathNm);
                fileMap.put("foldrWholePathNm",fileFoldrWholePathNm);
                fileMap.put("fileNm",fileList[j].getName());
                fileMap.put("fileSize",""+fileList[j].length());
                fileMap.put("fileAmdDate",""+fileFormat.format(fileDate));
                foldrFileArray.add(fileMap);
            }
        }
        return foldrFileArray;
    }

    public static List<Map<String,String>> subDirList(String path){

        File dir = new File(path);
        File[] fileList = dir.listFiles();

        Map<String,String> foldrMap = new HashMap<>();

        String foldrWholePathNm = dir.getAbsolutePath().replace(externalPath, "");

        Date foldrDate = new Date(dir.lastModified());
        DateFormat foldrFormat = new SimpleDateFormat("yyyy-MM-dd");
        foldrMap.put("foldrWholePathNm",foldrWholePathNm);
        foldrMap.put("foldrNm",dir.getName());
        foldrMap.put("foldrSize",""+dir.length());
        foldrMap.put("foldrAmdDate",""+foldrFormat.format(foldrDate));

        foldrFile.add(foldrMap);

        try {
            if(fileList != null) {

                for (int i = 0; i < fileList.length; i++) {
                    File file = fileList[i];

                    Map<String,String> fileMap = new HashMap<>();

                    if (file.exists()) {
                        if (file.isFile()) {

                            String fileWholePathNm = file.getAbsolutePath().replace(externalPath, "");
                            String fileFoldrWholePathNm = file.getParent().replace(externalPath, "");
                            Date fileDate = new Date(file.lastModified());
                            DateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd");
                            fileMap.put("fileWholePathNm",fileWholePathNm);
                            fileMap.put("foldrWholePathNm",fileFoldrWholePathNm);
                            fileMap.put("fileNm",file.getName());
                            fileMap.put("fileSize",""+file.length());
                            fileMap.put("fileAmdDate",""+fileFormat.format(fileDate));

                            foldrFile.add(fileMap);
                        }
                        if (file.isDirectory()) {

                            subDirList(file.getCanonicalPath().toString());

                        }
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return foldrFile;
    }

    public static int getIconByEtsion(String etsionNm) {

        int icoInt = 0;

        if(etsionNm.equals("css")) {
            icoInt = R.drawable.ico_24dp_filetype_code;
        } else if(etsionNm.equals("doc") || etsionNm.equals("docx") ) {
            icoInt = R.drawable.ico_24dp_filetype_doc;
        }else if(etsionNm.equals("hwp")) {
            icoInt = R.drawable.ico_24dp_filetype_hwp;
        }else if(etsionNm.equals("exe")) {
            icoInt = R.drawable.ico_24dp_filetype_exe;
        }else if(etsionNm.equals("avi") || etsionNm.equals("mp4") || etsionNm.equals("mkv") || etsionNm.equals("mpg") || etsionNm.equals("mpeg") || etsionNm.equals("wmv") ){
            icoInt = R.drawable.ico_24dp_filetype_film;
        }else if(etsionNm.equals("jpg") || etsionNm.equals("png")) {
            icoInt = R.drawable.ico_24dp_filetype_img;
        } else if(etsionNm.equals("pdf")) {
            icoInt = R.drawable.ico_24dp_filetype_pdf;
        } else if(etsionNm.equals("ppt") || etsionNm.equals("pptx")) {
            icoInt = R.drawable.ico_24dp_filetype_ppt;
        } else if(etsionNm.equals("mp3") || etsionNm.equals("wav") || etsionNm.equals("ogg") || etsionNm.equals("wma")) {
            icoInt = R.drawable.ico_24dp_filetype_sound;
        } else if(etsionNm.equals("txt")) {
            icoInt = R.drawable.ico_24dp_filetype_txt;
        } else if(etsionNm.equals("htm") || etsionNm.equals("html")) {
            icoInt = R.drawable.ico_24dp_filetype_webcode;
        } else if(etsionNm.equals("xls") || etsionNm.equals("xlsx")) {
            icoInt = R.drawable.ico_24dp_filetype_xls;
        } else if(etsionNm.equals("zip")) {
            icoInt = R.drawable.ico_24dp_filetype_zip;
        } else {
            icoInt = R.drawable.ico_24dp_filetype_etc;
        }

        return icoInt;

    }

    public static void removeFile(String foldrPath,String fileName) {

        String removePath = externalPath + foldrPath + "/";
        File file = new File(removePath+fileName);
        if(file.isFile()){
            file.delete();
        }

    }

    public static void removeFoldr(String foldrPath) {
        String removePath = externalPath + foldrPath + "/";
        File dir = new File(removePath);

        String[] children = dir.list();
        if (children != null) {
            for (int i=0; i<children.length; i++) {
                String filename = children[i];
                File file = new File(removePath + filename);

                if (file.exists()) {
                    file.delete();

                }
            }
            dir.delete();
        }
        dir.delete();
    }

}