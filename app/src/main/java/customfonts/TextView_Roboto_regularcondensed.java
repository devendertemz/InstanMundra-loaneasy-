package customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;


/**
 * Created by one on 3/12/15.
 */
public class TextView_Roboto_regularcondensed extends AppCompatTextView {

    public TextView_Roboto_regularcondensed(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextView_Roboto_regularcondensed(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextView_Roboto_regularcondensed(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoCondensed-Regular.ttf");
            setTypeface(tf);
        }
    }

}