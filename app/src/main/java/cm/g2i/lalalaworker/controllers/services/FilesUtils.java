package cm.g2i.lalalaworker.controllers.services;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import cm.g2i.lalalaworker.models.Worker;
import cm.g2i.lalalaworker.others.Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Sim'S on 14/08/2017.
 */

public class FilesUtils {
    private static final String LaLaLaWProfilesImgs = File.separator+"LaLaLaWData"+File.separator+"ProfilesImgs";
    private static final String LaLaLaWWorkerProfileFolder = "Me";

    public static Uri getOutputImageFileURI(int workerID){
        return Uri.fromFile(getOutputImageFile(workerID));
    }

    public static File getOutputImageFile(int workerID){
        // External sdcard location
        final String LaLaLaWMyProfileImg = LaLaLaWProfilesImgs+File.separator+LaLaLaWWorkerProfileFolder;
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.getRootDirectory().getAbsolutePath()),
                LaLaLaWMyProfileImg);
        //File mediaStorageDir = new File(LaLaLaWProfilesImgs, LaLaLaWWorkerProfileFolder);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MKDIR ERR", "Oops! Failed create "
                        + LaLaLaWProfilesImgs+"/"+LaLaLaWWorkerProfileFolder + " directory");
                return null;
            }
        }
        // Create a media file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new java.util.Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + String.valueOf(workerID)+".jpg");
        if (mediaFile.exists()) mediaFile.delete();
        return mediaFile;
    }

    public static String copyToLaLaLaWImagesDirectory(String imageFilePath, int workerID) throws FileNotFoundException, IOException{
        File file = new File(imageFilePath);

        final String LaLaLaWMyProfileImg = LaLaLaWProfilesImgs+File.separator+LaLaLaWWorkerProfileFolder;
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.getRootDirectory().getAbsolutePath()),
                LaLaLaWMyProfileImg);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MKDIR ERR", "Oops! Failed create "
                        + LaLaLaWProfilesImgs + " directory");
                return null;
            }
        }

        File copy = new File(mediaStorageDir.getPath()+File.separator+workerID+".jpg");
        if (copy.exists()) copy.delete();
        if (!copy.exists()) copy.createNewFile();

        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(copy);
        byte[] bytes = new byte[1024];
        int bytesRead = 0;
        try{
            while ((bytesRead = fis.read(bytes)) > 0){
                fos.write(bytes, 0, bytesRead);
            }
        } finally {
            fis.close();
            fos.close();
        }
        return copy.getPath();
    }

    public static String UriToPath(Uri imageFileUri, Context context){
        Cursor cursor = context.getContentResolver().query(imageFileUri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = context.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    public static File destinationPathForProfiles(Worker worker) throws IOException{
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.getRootDirectory().getAbsolutePath()),
                LaLaLaWProfilesImgs);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MKDIR ERR", "Oops! Failed create "
                        + LaLaLaWProfilesImgs + " directory");
                return null;
            }
        }
        String extension = ".jpg"; String[] fileNameExtension = Tools.split(worker.getPhoto(), ".");
        if (fileNameExtension!=null && fileNameExtension.length>1) extension = "."+fileNameExtension[1];
        File destinationImg = new File(mediaStorageDir.getPath()+File.separator+worker.getID()+ extension);

        if (destinationImg.exists()) destinationImg.delete();
        if (!destinationImg.exists()) destinationImg.createNewFile();

        return destinationImg;
    }
}
