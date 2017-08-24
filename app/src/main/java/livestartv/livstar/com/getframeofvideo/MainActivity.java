package livestartv.livstar.com.getframeofvideo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;

    String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(EasyPermissions.hasPermissions(this,perms)){

                test();
            }else {
                requestPermission();
            }
        }else{
            test();
        }




    }

    private void test() {
        String path = "/storage/emulated/0/outputVideo1503568182191.mp4";

        Bitmap bitmap =  ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 210, 210);

        iv = (ImageView) findViewById(R.id.iv);

        iv.setImageBitmap(bitmap);
    }

    private static final  int PERMISSION_CODES = 1001;
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        List<String> p = new ArrayList<>();
        for(String permission :perms){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                p.add(permission);
            }
        }
        if(p.size() > 0){
            requestPermissions(p.toArray(new String[p.size()]),PERMISSION_CODES);
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODES:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){

                }else {
                    test();
                }
                break;
        }

    }
}
