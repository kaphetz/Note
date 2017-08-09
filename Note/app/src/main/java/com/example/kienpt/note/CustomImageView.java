package com.example.kienpt.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CustomImageView extends FrameLayout {

    private View mRoot;
    private ImageView mImgPhoto;
    private ImageButton mBtnClose;


    private Bitmap mBitmap;
    private Context mContext;

    public CustomImageView(final Context context) {
        this(context, null);
    }

    public CustomImageView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        if (isInEditMode())
            return;

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View customView = null;
        if (inflater != null)
            customView = inflater.inflate(R.layout.custom_image_view, this);
        if (customView == null)
            return;
        mRoot = customView.findViewById(R.id.root);
        mImgPhoto = (ImageView) customView.findViewById(R.id.img_photo);
        mBtnClose = (ImageButton) customView.findViewById(R.id.btn_close);
        mImgPhoto.setImageBitmap(mBitmap);
        mBtnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoot.setVisibility(GONE);
            }
        });
    }

    public void setRoot(View root) {
        mRoot = mRoot;
    }

    public View getRoot() {
        return mRoot;
    }

    public void setImgPhoto(ImageView imgPhoto) {
        mImgPhoto = imgPhoto;
    }

    public ImageView getImgPhoto() {
        return mImgPhoto;
    }

    public void setBtnClose(ImageButton btnClose) {
        mBtnClose = btnClose;
    }

    public ImageButton getBtnClose() {
        return mBtnClose;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}