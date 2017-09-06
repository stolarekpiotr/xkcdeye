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
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import thescypion.xkcdeye.XkcdAPI.Comic;
import thescypion.xkcdeye.XkcdAPI.XkcdController;

public class MainActivity extends AppCompatActivity {

    Comic comic;
    XkcdController xkcdController;
    CompositeDisposable disposable = new CompositeDisposable();

    Integer id = 1;
    Integer lastId = 999;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setNumberPickerDialog();

        xkcdController = new XkcdController();
        getNewestComic();
    }

    private void setNumberPickerDialog() {
        numberPickerDialog = new Dialog(this);
        numberPickerDialog.setTitle(getString(R.string.dialog_ui_title));
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
        this.comic = comic;
        pbLoad.setVisibility(View.GONE);
        tvTitle.setText(comic.getTitle());
        tvDate.setText(comic.getDateString());
        tvComicId.setText(comic.getNum().toString());
        GlideApp
                .with(this)
                .load(comic.getImg())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivComicImage);
    }

    private void getComic(Integer id) {
        this.id = (id < 1) ? 1 : id;
        pbLoad.setVisibility(View.VISIBLE);
        Single<Comic> call = xkcdController.getComic(this.id);
        Disposable getComic = call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                    @Override
                    public void accept(Comic comic) throws Exception {
                        updateUI(comic);
                    }
                });
        disposable.add(getComic);
    }

    private void getNewestComic() {
        pbLoad.setVisibility(View.VISIBLE);
        Single<Comic> call = xkcdController.getNewestComic();
        Disposable getComic = call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                    @Override
                    public void accept(Comic comic) throws Exception {
                        updateUI(comic);
                        lastId = comic.getNum();
                        id = lastId;
                        numberPicker.setMaxValue(lastId);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
        disposable.add(getComic);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
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
        getComic(lastId);
    }

    @OnClick(R.id.ivComicImage)
    public void imageClick() {
        Toast.makeText(this, comic.getAlt(), Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.tvComicId)
    public void onIdClick() {
        numberPicker.setValue(id);
        numberPickerDialog.show();
    }

}
