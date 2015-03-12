package pl.droidsonroids.gif;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * An {@link ImageButton} which tries treating background and src as {@link GifDrawable}
 *
 * @author koral--
 */
public class GifImageButton extends ImageButton {

    private boolean freezesAnimation;
    private boolean shouldSaveSource;
    private boolean shouldSaveBackground;

    /**
     * A corresponding superclass constructor wrapper.
     *
     * @param context
     * @see ImageButton#ImageButton(Context)
     */
    public GifImageButton(Context context) {
        super(context);
    }

    /**
     * Like equivalent from superclass but also try to interpret src and background
     * attributes as {@link GifDrawable}.
     *
     * @param context
     * @param attrs
     * @see ImageButton#ImageButton(Context, AttributeSet)
     */
    public GifImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInit(GifViewUtils.init(this, attrs, 0, 0));
    }

    /**
     * Like equivalent from superclass but also try to interpret src and background
     * attributes as GIFs.
     *
     * @param context
     * @param attrs
     * @param defStyle
     * @see ImageButton#ImageButton(Context, AttributeSet, int)
     */
    public GifImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        postInit(GifViewUtils.init(this, attrs, defStyle, 0));
    }

    /**
     * Like equivalent from superclass but also try to interpret src and background
     * attributes as GIFs.
     *
     * @param context
     * @param attrs
     * @param defStyle
     * @param defStyleRes
     * @see ImageButton#ImageButton(Context, AttributeSet, int, int)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GifImageButton(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle, defStyleRes);
        postInit(GifViewUtils.init(this, attrs, defStyle, defStyleRes));
    }

    private void postInit(GifViewUtils.InitResult result) {
        shouldSaveSource = true;
        shouldSaveBackground = true;
        freezesAnimation = result.mFreezesAnimation;
        if (result.mSourceResId > 0) {
            super.setImageResource(result.mSourceResId);
        }
        if (result.mBackgroundResId > 0) {
            super.setBackgroundResource(result.mBackgroundResId);
        }
    }

    /**
     * Sets the content of this GifImageView to the specified Uri.
     * If uri destination is not a GIF then {@link android.widget.ImageView#setImageURI(android.net.Uri)}
     * is called as fallback.
     * For supported URI schemes see: {@link android.content.ContentResolver#openAssetFileDescriptor(android.net.Uri, String)}.
     *
     * @param uri The Uri of an image
     */
    @Override
    public void setImageURI(Uri uri) {
        if (!GifViewUtils.setGifImageUri(this, uri)) {
            super.setImageURI(uri);
        }
    }

    @Override
    public void setImageResource(int resId) {
        if (!GifViewUtils.setResource(this, true, resId)) {
            super.setImageResource(resId);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        shouldSaveSource = false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        shouldSaveBackground = false;
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        shouldSaveBackground = false;
    }

    @Override
    public void setBackgroundResource(int resId) {
        if (!GifViewUtils.setResource(this, false, resId)) {
            super.setBackgroundResource(resId);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Drawable source = freezesAnimation && shouldSaveSource ? getDrawable() : null;
        Drawable background = freezesAnimation && shouldSaveBackground ? getBackground() : null;
        return new GifViewSavedState(super.onSaveInstanceState(), source, background);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        GifViewSavedState ss = (GifViewSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        ss.setPosition(getDrawable(), 0);
        ss.setPosition(getBackground(), 1);
    }
}
