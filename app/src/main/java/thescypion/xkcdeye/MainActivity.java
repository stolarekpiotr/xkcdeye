package thescypion.xkcdeye;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import thescypion.xkcdeye.XkcdAPI.Comic;
import thescypion.xkcdeye.XkcdAPI.ComicReceivedListener;
import thescypion.xkcdeye.XkcdAPI.XkcdController;

public class MainActivity extends AppCompatActivity implements ComicReceivedListener {
    private final static String ID_VALUE_KEY = "idvaluekey";
    @BindView(R.id.tvComicId)
    TextView tvComicId;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.ivComicImage)
    PhotoView ivComicImage;
    @BindView(R.id.pbLoad)
    ProgressBar pbLoad;
    Dialog numberPickerDialog;
    NumberPicker numberPicker;
    private String alt = "";
    private XkcdController xkcdController;
    private CompositeDisposable disposable;
    private Integer id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setNumberPickerDialog();
        xkcdController = XkcdController.getInstance(this);
        disposable = new CompositeDisposable();
        if (savedInstanceState == null) {
            getComic(XkcdController.NEWEST);
        } else {
            id = savedInstanceState.getInt(ID_VALUE_KEY);
            numberPicker.setMaxValue(xkcdController.MAX_COMIC_ID);
            getComic(id);
        }
    }

    private void setNumberPickerDialog() {
        numberPickerDialog = new Dialog(this);
        numberPickerDialog.setContentView(R.layout.dialog);
        numberPicker = (NumberPicker) numberPickerDialog.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        Button btnGoTo = (Button) numberPickerDialog.findViewById(R.id.btnGoto);
        btnGoTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getComic(numberPicker.getValue());
                numberPickerDialog.dismiss();
            }
        });
    }

    private void updateUI(Comic comic) {
        pbLoad.setVisibility(View.GONE);
        tvTitle.setText(comic.getTitle());
        tvDate.setText(comic.getDateString());
        tvComicId.setText(comic.getNum().toString());
        GlideApp
                .with(getApplicationContext())
                .load(comic.getImg())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivComicImage);
    }

    private void getComic(Integer id) {
        pbLoad.setVisibility(View.VISIBLE);
        Disposable d = xkcdController.getComic(id);
        if (d != null) {
            disposable.add(d);
        } else {
            pbLoad.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ID_VALUE_KEY, id);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.btnFirst)
    public void goToFirstComic() {
        getComic(1);
    }

    @OnClick(R.id.btnPrevious)
    public void goToPreviousComic() {
        getComic(id - 1);
    }

    @OnClick(R.id.btnNext)
    public void goToNextComic() {
        getComic(id + 1);
    }

    @OnClick(R.id.btnLast)
    public void goToLastComic() {
        getComic(XkcdController.NEWEST);
    }

    @OnClick(R.id.ivComicImage)
    public void imageClick() {
        Toast.makeText(this, alt, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.tvComicId)
    public void onIdClick() {
        numberPicker.setValue(id);
        numberPickerDialog.show();
    }

    @Override
    public void onComicReceived(Comic comic, Boolean newest) {
        this.alt = comic.getAlt();
        id = comic.getNum();
        if (newest) {
            numberPicker.setMaxValue(xkcdController.MAX_COMIC_ID);
        }
        updateUI(comic);
    }
}
