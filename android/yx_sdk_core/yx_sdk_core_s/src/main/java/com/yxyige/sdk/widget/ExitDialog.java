package com.yxyige.sdk.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.yxyige.sdk.core.IError;
import com.yxyige.sdk.core.YXSDKListener;
import com.yxyige.sdk.utils.AppUtils;
import com.yxyige.sdk.utils.AsyncImageLoader;
import com.yxyige.sdk.utils.AsyncImageLoader.ImageCallback;
import com.yxyige.sdk.utils.Util;

public class ExitDialog extends Dialog {

    private String mPackageName;
    private String mUrl;
    private String mImgPath;
    private Drawable mImg;
    private Context context;
    private YXSDKListener exitListener;
    private AsyncImageLoader imageLoader;


    public ExitDialog(Context context, YXSDKListener listener) {
        this(context, Util.getIdByName("Dialog", "style", context.getPackageName(), context), listener);
        this.context = context;
        this.exitListener = listener;
    }

    ExitDialog(Context context, int theme, YXSDKListener listener) {
        super(context, theme);
        imageLoader = new AsyncImageLoader(context);
        this.exitListener = listener;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(p);
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(Util.getIdByName("yx_dialog_exit", "layout", context.getPackageName(), context), null);


        ImageView exitResImg = (ImageView) view.findViewById(Util.getIdByName("yx_img_exit_res", "id", context.getPackageName(), context));
        Button exitBtn = (Button) view.findViewById(Util.getIdByName("yx_btn_exit_game", "id", context.getPackageName(), context));
        Button continueBtn = (Button) view.findViewById(Util.getIdByName("yx_btn_continue_game", "id", context.getPackageName(), context));

        if (mImg != null) {

            exitResImg.setImageDrawable(mImg);
        } else {

            Bitmap bitmap = imageLoader.loadDrawable(mImgPath, exitResImg, new ImageCallback() {

                @Override
                public void imageLoaded(Bitmap imageDrawable, ImageView imageView, String imageUrl) {

                    imageView.setImageBitmap(imageDrawable);
                }
            });

            if (bitmap != null) {

                exitResImg.setImageBitmap(bitmap);
            }

        }

        exitResImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //默认做显示用，点击不做处理
                if (mPackageName != null
                        && !"".equals(mPackageName)
                        && Util.checkAppInstalled(context, mPackageName)) {  //打开应用

                    AppUtils.startAppFromPackage(context, mPackageName);
                    ExitDialog.this.dismiss();

                } else if (mUrl != null && !"".equals(mUrl)) {   //打开外部链接

                    AppUtils.toUri(context, mUrl);
                    ExitDialog.this.dismiss();
                }
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ExitDialog.this.dismiss();
                exitListener.onSuccess(new Bundle());
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ExitDialog.this.dismiss();
                exitListener.onFailture(IError.ERROR_CLIENT, "继续游戏");
            }
        });

        setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }


    /**
     * 设置退弹框的图片资源点击url
     *
     * @param url
     */
    public void setUrl(String url) {
        mUrl = url;
    }


    /**
     * 设置包名
     *
     * @param pname
     */
    public void setPkn(String pname) {

        this.mPackageName = pname;
    }

    /**
     * 设置图片资源
     *
     * @param img
     */
    public void setImageDrawable(Drawable img) {

        this.mImg = img;
    }


    /**
     * 设置图片资源路径
     */
    public void setImagePath(String exitImgPath) {

        this.mImgPath = exitImgPath;
    }
}
