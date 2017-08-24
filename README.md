# GetFrameOfVideo
从API 8开始，新增了一个类：

android.media.ThumbnailUtils这个类提供了3个静态方法一个用来获取视频第一帧得到的Bitmap，2个对图片进行缩略处理。

public static Bitmap createVideoThumbnail (String filePath, int kind)
第一个参数是视频文件的路径，第二个参数是指定图片的大小，有两种选择Thumbnails.MINI_KIND与Thumbnails.MICRO_KIND。

第一种文档上说大小是512 x 384 ，我用一个MP4格式文件测试得到544 x 960，用一个wmv格式文件测试得到160 x 120。明显不靠谱。
第二种参数两种格式文件得到的大小都是 96 x 96，这个才是缩略图。
extractThumbnail(Bitmap source, int width, int height, int options)
extractThumbnail(Bitmap source, int width, int height)
这两种方法都是用来处理Bitmap的大小的，第一个参数是要处理的Bitmap，第二个参数是处理后宽度，第三个是高度，第四个参数options，如果options定义为OPTIONS_RECYCLE_INPUT，则回收资源。也就是说可以用第三种方法把截取到的视频第一帧的Bitmap转成任意想要的大小，第三个方法还可以获取内存卡内图片的缩略图。

Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path1, Thumbnails.MINI_KIND);  
bitmap = ThumbnailUtils.extractThumbnail(bitmap, 210, 210);
从API 10开始新增一类MediaMetadataRetriever可以用来获取媒体文件的信息

复制代码
MediaMetadataRetriever mmr = new MediaMetadataRetriever();  
mmr.setDataSource("/sdcard/33.mp4");  
Bitmap bitmap = mmr.getFrameAtTime();  
image.setImageBitmap(bitmap);  
System.out.println(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)+"");  
System.out.println(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)+"");  
mmr.release();
复制代码
MediaMetadataRetriever可以获取视频任何一帧的缩略图。

 

复制代码
public static Bitmap createVideoThumbnail(String filePath) {  
    // MediaMetadataRetriever is available on API Level 8  
    // but is hidden until API Level 10  
    Class<?> clazz = null;  
    Object instance = null;  
    try {  
        clazz = Class.forName("android.media.MediaMetadataRetriever");  
        instance = clazz.newInstance();  
  
        Method method = clazz.getMethod("setDataSource", String.class);  
        method.invoke(instance, filePath);  
  
        // The method name changes between API Level 9 and 10.  
        if (Build.VERSION.SDK_INT <= 9) {  
            return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);  
        } else {  
            byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);  
            if (data != null) {  
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  
                if (bitmap != null) return bitmap;  
            }  
            return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);  
        }  
    } catch (IllegalArgumentException ex) {  
        // Assume this is a corrupt video file  
    } catch (RuntimeException ex) {  
        // Assume this is a corrupt video file.  
    } catch (InstantiationException e) {  
        Log.e(TAG, "createVideoThumbnail", e);  
    } catch (InvocationTargetException e) {  
        Log.e(TAG, "createVideoThumbnail", e);  
    } catch (ClassNotFoundException e) {  
        Log.e(TAG, "createVideoThumbnail", e);  
    } catch (NoSuchMethodException e) {  
        Log.e(TAG, "createVideoThumbnail", e);  
    } catch (IllegalAccessException e) {  
        Log.e(TAG, "createVideoThumbnail", e);  
    } finally {  
        try {  
            if (instance != null) {  
                clazz.getMethod("release").invoke(instance);  
            }  
        } catch (Exception ignored) {  
        }  
    }  
    return null;  
}
