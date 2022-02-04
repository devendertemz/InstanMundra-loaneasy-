package customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;


public class TextView_Roboto_regular extends AppCompatTextView {

    public TextView_Roboto_regular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextView_Roboto_regular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextView_Roboto_regular(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            setTypeface(tf);
        }
    }

}