package com.yxyige.sdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.yxyige.sdk.core.IConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AsyncImageLoader {

    public static ExecutorService pool;

    static {
        pool = Executors.newFixedThreadPool(IConfig.POLL_MAX_LOADIMAGE_NUM);
    }

    private Context context;
    /**
     * 软引用对象，在响应内存需要时，由垃圾回收器决定是否清除此对象。软引用对象最常用于实现内存敏感的缓存。
     */
    private HashMap<String, SoftReference<Bitmap>> imageCache;

    public AsyncImageLoader(Context context) {
        this.context = context;
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }

    public Bitmap loadDrawable(final String imageUrl,
                               final ImageView imageView, final ImageCallback imagecallback) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap) msg.obj;
                imagecallback.imageLoaded(bitmap, imageView, imageUrl);
            }
        };

        if (imageCache.containsKey(imageUrl)) {
            // 从缓存中读取
            SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
            Bitmap bitmap = softReference.get();
            if (bitmap != null) {
                return bitmap;
            }

        } else if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

            /**
             * 加上一个对本地缓存的查找
             */

            if (imageUrl.lastIndexOf("/") == -1 || imageUrl.lastIndexOf(".") == -1) {
                return null;
            }
            String bitmapName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
            File cacheDir = new File(android.os.Environment.getExternalStorageDirectory().getPath() + IConfig.BITMAP_CACHE);

            // 创建文件夹
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            File[] cacheFiles = cacheDir.listFiles();

            int i = 0;
            for (; i < cacheFiles.length; i++) {
                if (bitmapName.equals(cacheFiles[i].getName())) {
                    break;
                }
            }

            if (i < cacheFiles.length) {
                Bitmap bitmap = BitmapFactory.decodeFile(android.os.Environment
                        .getExternalStorageDirectory().getPath()
                        + IConfig.BITMAP_CACHE
                        + bitmapName);

                return bitmap;
            }
        }

        pool.execute(new Thread() {
            public void run() {
                File bitmapFile = null;
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(8 * 1000); // 注意要设置超时，设置时间不要超过10秒，避免被android系统回收
                    if (conn.getResponseCode() != 200) {
                        Message msg = handler.obtainMessage(0, null);
                        handler.sendMessage(msg);
                        return;
                    }

                    InputStream inSream = conn.getInputStream();

                    Bitmap bitmap = null;


                    /**********检测sdcard的状态*********/
                    if (android.os.Environment.getExternalStorageState().equals(
                            android.os.Environment.MEDIA_MOUNTED)) {

                        /********Envinronment.getExternalStrorageDirectory()返回sdcard目录*******/
                        File dir = new File(android.os.Environment
                                .getExternalStorageDirectory().getPath() + IConfig.BITMAP_CACHE);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        String bitmapName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));

                        bitmapFile = new File(android.os.Environment
                                .getExternalStorageDirectory().getPath()
                                + IConfig.BITMAP_CACHE
                                + bitmapName);

                        if (!bitmapFile.exists()) {
                            bitmapFile.createNewFile();
                        }

                        FileOutputStream outStream = new FileOutputStream(bitmapFile);
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = inSream.read(buffer)) != -1) {
                            outStream.write(buffer, 0, len);
                        }
                        outStream.close();
                        inSream.close();

                        bitmap = BitmapFactory.decodeFile(android.os.Environment
                                .getExternalStorageDirectory().getPath()
                                + IConfig.BITMAP_CACHE
                                + bitmapName);
                    } else {
                        bitmap = BitmapFactory.decodeStream(inSream);
                    }

                    Message msg = handler.obtainMessage(0, bitmap);
                    handler.sendMessage(msg);

                    imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Message msg = handler.obtainMessage(0, null);
                    handler.sendMessage(msg);

                    if (bitmapFile != null && bitmapFile.exists()) {
                        bitmapFile.delete();
                    }
                }
            }
        });

        return null;

    }

    public byte[] getsdImage(String imageurl) {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

            /**
             * 加上一个对本地缓存的查找
             */
            String bitmapName = imageurl.substring(imageurl.lastIndexOf("/") + 1, imageurl.lastIndexOf("."));//.replace("http://", "").replace("/", "").replace(":", "");//.substring(imageUrl.lastIndexOf("/") + 1,imageUrl.lastIndexOf("."));
            File cacheDir = new File(android.os.Environment
                    .getExternalStorageDirectory().getPath() + IConfig.BITMAP_CACHE);

            // 创建文件夹
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            File[] cacheFiles = cacheDir.listFiles();

            int i = 0;
            for (; i < cacheFiles.length; i++) {
                if (bitmapName.equals(cacheFiles[i].getName())) {
                    break;
                }
            }

            if (i < cacheFiles.length) {
                Bitmap bitmap = BitmapFactory.decodeFile(android.os.Environment
                        .getExternalStorageDirectory().getPath()
                        + IConfig.BITMAP_CACHE
                        + bitmapName);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                return baos.toByteArray();
            }
        }
        return null;
    }

    public interface ImageCallback {
        public void imageLoaded(Bitmap imageDrawable, ImageView imageView,
                                String imageUrl);
    }

}
