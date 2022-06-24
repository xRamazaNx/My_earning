package com.press.myearnings.adapter;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.press.myearnings.R;
import com.press.myearnings.ZarplataActivity;
import com.press.myearnings.database.SchemaDB;
import com.press.myearnings.database.ZarplataHelper;
import com.press.myearnings.model.CopyData;
import com.press.myearnings.model.Dannie;
import com.press.myearnings.setting.ColumnName;
import com.press.myearnings.setting.SettingColorSet;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import static com.press.myearnings.StaticUtil.convertDpToPixels;

public class AdapterStroke extends RecyclerView.Adapter<AdapterStroke.Holder> {
    private final DecimalFormat numberFormat;
    private final SharedPreferences pref;
    private final int heightItem;
    private List<Dannie> dannieList;
    private Handler handler;
    private String nameTable;
    private boolean isAdapterForArhive;
    private boolean isTitleSettinMode;
    private SharedPreferences sharedPreferences;
    private Animation animation;
    private LinearLayout.LayoutParams layoutParamFix;
    private LinearLayout.LayoutParams layoutParamsNotFix;
    private boolean isNoteSingleLine = false;
    private boolean isFixHeightItem = false;


    public AdapterStroke(Context context, final List<Dannie> dannies, Handler handler, String nameTable, boolean isAdapterForArhive) {
        this.handler = handler;
        dannieList = dannies;
        this.nameTable = nameTable;
        this.isAdapterForArhive = isAdapterForArhive;
        numberFormat = new DecimalFormat();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        animation = AnimationUtils.loadAnimation(context, R.anim.anim_add);
        sharedPreferences = context.getSharedPreferences(SchemaDB.Table.OLD_SIZE_ITEM, Context.MODE_PRIVATE);
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        isNoteSingleLine = pref.getBoolean("check_box_note_one_line", false);
        isFixHeightItem = pref.getBoolean("check_box_fix_height_item", false);
        int height = Integer.parseInt(pref.getString(context.getString(R.string.zapisi_size_key), "30"));
        heightItem = convertDpToPixels(height, context);
        layoutParamFix = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightItem);
        layoutParamsNotFix = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setTitleSettinMode(boolean titleSettinMode) {
        isTitleSettinMode = titleSettinMode;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int id = R.layout.schet_za_1_dey;
        if (viewType == 1)
            id = R.layout.schet_za_1_dey_sort_date;

        View view = inflater.inflate(id, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bindDannie(dannieList.get(position));

    }

    @Override
    public int getItemCount() {
        return dannieList.size();

    }

    @Override
    public int getItemViewType(int position) {
        return dannieList.get(position).type;
    }
    ///////////////////////////
///////////////////////////

    class Holder extends RecyclerView.ViewHolder {
        int varVisDate;
        float summaF = 0;
        float avansF = 0;
        private TextView mAllSumma;
        private TextView mAllAvans;
        private TextView mOstatok;
        private TextView mSumma;
        private TextView mAvans;
        private TextView mDate;
        private TextView mComent;
        private TextView mNumeraciya;
        private LinearLayout line;

        Holder(View item) {
            super(item);
            varVisDate = Integer.parseInt(pref.getString(itemView.getContext().getString(R.string.date_pref_for_zapisi), "1"));

            mSumma = itemView.findViewById(R.id.id_colichestvo_rulonov_1_kolona);
            mAvans = itemView.findViewById(R.id.id_avans);
            mComent = itemView.findViewById(R.id.id_comment_1_dey);
            mDate = itemView.findViewById(R.id.id_data);
            mNumeraciya = itemView.findViewById(R.id.id_numeraciya);
            line = itemView.findViewById(R.id.linear);

            mAllSumma = itemView.findViewById(R.id.id_summa_today);
            mAllAvans = itemView.findViewById(R.id.id_avans_today);
            mOstatok = itemView.findViewById(R.id.id_ostatok_today);
            if (!isTitleSettinMode)
                if (mSumma != null) {
                    if (!isAdapterForArhive) {
                        mSumma.setOnLongClickListener(onLongClickMetod(1));
                        mAvans.setOnLongClickListener(onLongClickMetod(2));
                        mDate.setOnLongClickListener(onLongClickMetod(3));
                        mComent.setOnLongClickListener(onLongClickMetod(4));
                    }
                    onClickMethod();

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemView.getLayoutParams();
                    params.setMargins(0, 0, 0, -1);
                    itemView.setLayoutParams(params);
                }


//                    mAllSumma.setOnLongClickListener(onLongClickMetod(1));
//                    mAllAvans.setOnLongClickListener(onLongClickMetod(2));
//                    mOstatok.setOnLongClickListener(onLongClickMetod(3));


            //анимация для удаленея всего
//            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences(SchemaDB.Table.OLD_SIZE_ITEM, Context.MODE_PRIVATE);
//            if (sharedPreferences.contains(SchemaDB.Table.BOOLEANFORDELETEALL)){
//                int val = sharedPreferences.getInt(SchemaDB.Table.BOOLEANFORDELETEALL, 0);
//                if (val == 1){
//                    int sizeMassiv = dannieList.size();
//                    for (int i = 0; i < sizeMassiv; i++) {
//                        deleteItem();
//                    }
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putInt(SchemaDB.Table.BOOLEANFORDELETEALL, 0);
//                    editor.apply();
//                }
//            }
        }

        private void onClickMethod() {
            View.OnClickListener onClickListener = view -> {
                LayoutInflater l = LayoutInflater.from(itemView.getContext());
                ViewGroup viewGroup = (ViewGroup) l.inflate(R.layout.activity_prosmotr, null, false);

                ColumnName columnName = ColumnName.getColumnName(itemView.getContext(), nameTable);

                TextView pSumma = viewGroup.findViewById(R.id.id_colichestvo_rulonov_1_kolona_prosmotr);
                TextView titleSumma = viewGroup.findViewById(R.id.title_summa_prosmotr);
                titleSumma.setText(columnName.summa);
                pSumma.setText(mSumma.getText().toString());
                pSumma.setTextSize(setFontSize(pref, 0));
                pSumma.setTypeface(null, setFontStyle(pref, 0));
                pSumma.setTextColor(mSumma.getCurrentTextColor());


                TextView pAvans = viewGroup.findViewById(R.id.id_avans_prosmotr);
                TextView titleAvans = viewGroup.findViewById(R.id.title_avans_prosmotr);
                titleAvans.setText(columnName.avans);
                pAvans.setText(mAvans.getText().toString());
                pAvans.setTextSize(setFontSize(pref, 1));
                pAvans.setTypeface(null, setFontStyle(pref, 1));
                pAvans.setTextColor(mAvans.getCurrentTextColor());

                TextView pDate = viewGroup.findViewById(R.id.id_data_prosmotr);
                TextView titleDate = viewGroup.findViewById(R.id.title_date_prosmotr);
                titleDate.setText(columnName.date);
                pDate.setText(mDate.getText().toString());
                pDate.setTextSize(setFontSize(pref, 3));
                pDate.setTypeface(null, setFontStyle(pref, 3));
                pDate.setTextColor(mDate.getCurrentTextColor());

                TextView pComent = viewGroup.findViewById(R.id.id_comment_prosmotr);
                TextView titleNote = viewGroup.findViewById(R.id.title_note_prosmotr);
                titleNote.setText(columnName.note);
                pComent.setText(mComent.getText().toString());
                pComent.setTextSize(setFontSize(pref, 2));
                pComent.setTypeface(null, setFontStyle(pref, 2));
                pComent.setTextColor(mComent.getCurrentTextColor());

                AlertDialog.Builder dialogProsmotr = new AlertDialog.Builder(itemView.getContext(), R.style.prosmotr);
                dialogProsmotr.setView(viewGroup);
                dialogProsmotr.show();
            };
            mSumma.setOnClickListener(onClickListener);
            mAvans.setOnClickListener(onClickListener);
            mDate.setOnClickListener(onClickListener);
            mComent.setOnClickListener(onClickListener);
        }

        private View.OnLongClickListener onLongClickMetod(final int index) {
            return view -> {
                final int yelow = itemView.getResources().getColor(R.color.color_long_click_item);
                line.setBackgroundColor(yelow);
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), view);
                popupMenu.inflate(R.menu.item_menu);
                popupMenu.getMenu().findItem(R.id.id_paste_item).setEnabled(CopyData.getCopy().copy != null);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Message message = new Message();
                        message.arg1 = dannieList.get(getAdapterPosition()).getID();
                        message.arg2 = index;
                        int itemId = item.getItemId();
                        if (itemId == R.id.id_edit_item) {
                            message.what = ZarplataActivity.MESSEG_TO_EDIT_ITEM;
                            handler.sendMessage(message);
//                                    StaticUtil.getIntentOfShowEditWin(context, dannieList.get(getAdapterPosition()), index);
                        } else if (itemId == R.id.id_delete_item) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(itemView.getContext());
                            dialog.setMessage(R.string.delete_the_entry)
                                    .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            deleteItem();
                                        }
                                    })
                                    .setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    })
                                    .show();
                        } else if (itemId == R.id.id_copy_item) {
                            CopyData.getCopy().copy = dannieList.get(getAdapterPosition());
                        } else if (itemId == R.id.id_paste_item) {//                                    SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences(SchemaDB.Table.OLD_SIZE_ITEM, Context.MODE_PRIVATE);
//                                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                            //добавление данных одной записи в б
                            message.what = ZarplataActivity.MESSEG_TO_PASTE_ITEM;
                            handler.sendMessage(message);
                        } else if (itemId == R.id.id_dublicate_item) {
                            message.what = ZarplataActivity.MESSEG_TO_DUBLICATE_ITEM;
                            handler.sendMessage(message);
//                                    StaticUtil.getIntentOfShowEditWin(itemView.getContext(), dannieList.get(getAdapterPosition()), 1);
//                                    startEditWin(itemView.getContext(), dannieList.get(getAdapterPosition()), ZarplataActivity.REQUEST_NEW_ZAPIS);
                        } //switch item
                        return true;
                    }
                });
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu popupMenu) {
                        int transperent = itemView.getResources().getColor(R.color.transparent);
                        line.setBackgroundColor(transperent);
                    }
                });
                popupMenu.show();
                return true;
            };
        }

        private void deleteItem() {
//            Cursor cursor = database.query(SchemaDB.Table.NAME_ZAPISI, null, null, null, null, null, null);
//            ContentValues values = new ContentValues();
//            // упорядычивание ид в таблице
//            if (cursor.moveToFirst()) {
//                int newIndex = 0;
//                do {
//                    String oldINdex = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.ID_MASSIV));
//                    values.put(SchemaDB.Table.Cols.ID_MASSIV, String.valueOf(newIndex));
//                    database.update(nameTable, values, SchemaDB.Table.Cols.ID_MASSIV + " = ?", new String[]{oldINdex});
//                    newIndex++;
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
            SQLiteDatabase database = new ZarplataHelper(itemView.getContext()).getWritableDatabase();
            database.delete(nameTable, SchemaDB.Table.Cols.ID + " = ?", new String[]{Integer.toString(dannieList.get(getAdapterPosition()).getID())});
            database.close();

            colorAnim(R.color.color_avans_card, 600);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.anim_delete);
                    itemView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            handler.sendEmptyMessage(ZarplataActivity.MESSEG_TO_UPDATE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }, 300);

        }

        private void colorAnim(int colorToRes, int duration) {

            int colorFrom = itemView.getResources().getColor(colorToRes);
            int colorTo = itemView.getResources().getColor(R.color.transparent);
            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

            animator.setDuration(duration);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    line.setBackgroundColor((int) valueAnimator.getAnimatedValue());
                }
            });

            animator.start();

        }

        void bindDannie(Dannie dannie) {

            if (getItemViewType() == 1) {

                itemView.setLayoutParams(layoutParamsNotFix);
                itemView.setMinimumHeight(heightItem);

                mAllSumma.setText(dannie.getSumma());
                mAllAvans.setText(dannie.getAvans());
                mOstatok.setText(dannie.getOstatok());

                mAllSumma.setTextSize(setFontSize(pref, 5));
                mAllAvans.setTextSize(setFontSize(pref, 5));
                mOstatok.setTextSize(setFontSize(pref, 5));

                mAllSumma.setTypeface(null, setFontStyle(pref, 5));
                mAllAvans.setTypeface(null, setFontStyle(pref, 6));
                mOstatok.setTypeface(null, setFontStyle(pref, 7));

                SettingColorSet.getTextColor(pref, mAllSumma, SettingColorSet.ViewName.summaCard);
                SettingColorSet.getTextColor(pref, mAllAvans, SettingColorSet.ViewName.avansCard);
                SettingColorSet.getTextColor(pref, mOstatok, SettingColorSet.ViewName.ostatok);

                return;
            }

            if (isFixHeightItem) {
                line.setLayoutParams(layoutParamFix);

            } else {
                line.setLayoutParams(layoutParamsNotFix);
                line.setMinimumHeight(heightItem);
            }

            if (isNoteSingleLine) {
                mComent.setSingleLine(true);
                mComent.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                mComent.setSingleLine(false);
            }

//            if (getAdapterPosition() < dannieList.size() - 1)
//            Log.d("mDate", String.valueOf(dannie.getDateMl()));

            if (sharedPreferences.contains(SchemaDB.Table.COUNTER)) {
                int size = sharedPreferences.getInt(SchemaDB.Table.COUNTER, 0);  // единица решающая какая из анимаций будет{                                                            //анимация если удаляю одну запись
                if (dannie.getID() == size) {
                    itemView.startAnimation(animation);
                    colorAnim(R.color.color_vsego_card, 1000);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(SchemaDB.Table.COUNTER, -2);
                    editor.apply();
                }
            }
            //padding
            int dpForOne = convertDpToPixels(1, itemView.getContext());
            int dpForEnd = convertDpToPixels(10, itemView.getContext());
            if (getAdapterPosition() == 0 && dannieList.size() == 1) {
                itemView.setPadding(0, dpForOne, 0, dpForEnd);
            } else {
                if (getAdapterPosition() == 0) {
                    itemView.setPadding(0, dpForOne, 0, 0);
                } else if (getAdapterPosition() == dannieList.size() - 1) {
                    itemView.setPadding(0, 0, 0, dpForEnd);
                } else {
                    itemView.setPadding(0, 0, 0, 0);
                }
            }
            //

            //
            if (!dannie.getSumma().equals("")) {
                summaF = Float.parseFloat(dannie.getSumma());
                mSumma.setText(numberFormat.format(summaF));
            } else mSumma.setText("");
            // /
            if (!dannie.getAvans().equals("")) {
                avansF = Float.parseFloat(dannie.getAvans());
                mAvans.setText(numberFormat.format(avansF));
            } else mAvans.setText("");

            DisplayMetrics metrics = itemView.getResources().getDisplayMetrics();
            if (metrics.widthPixels <= 500)
                mDate.setTextSize(10);

            mDate.setText(dannie.getDate(varVisDate));

            mComent.setText(dannie.getComent());
            mNumeraciya.setText(String.valueOf(dannie.position));

            //убрано название дня недели в детализации архива
//            if (handler == null) {
//                String sDate = mDate.getText().toString();
//                mDate.setText(sDate.substring(4));
//            }

            mSumma.setTextSize(setFontSize(pref, 0));
            mAvans.setTextSize(setFontSize(pref, 1));
            mComent.setTextSize(setFontSize(pref, 2));
            mDate.setTextSize(setFontSize(pref, 3));

            mNumeraciya.setTextSize(setFontSize(pref, 4));

            mSumma.setTypeface(null, setFontStyle(pref, 0));
            mAvans.setTypeface(null, setFontStyle(pref, 1));
            mComent.setTypeface(null, setFontStyle(pref, 2));
            mDate.setTypeface(null, setFontStyle(pref, 3));
            mNumeraciya.setTypeface(null, setFontStyle(pref, 4));

            SettingColorSet.getTextColor(pref, mNumeraciya, SettingColorSet.ViewName.id);
            SettingColorSet.getTextColor(pref, mSumma, SettingColorSet.ViewName.summa);
            SettingColorSet.getTextColor(pref, mAvans, SettingColorSet.ViewName.avans);
            SettingColorSet.getTextColor(pref, mDate, SettingColorSet.ViewName.date);
            SettingColorSet.getTextColor(pref, mComent, SettingColorSet.ViewName.note);

        }

        private int setFontSize(SharedPreferences pref, int textView) {
            int size = 0;
            String key;
            switch (textView) {
                case 0:
                    key = itemView.getContext().getString(R.string.summa);
                    size = Integer.parseInt(pref.getString(key, "14"));
                    break;
                case 1:
                    key = itemView.getContext().getString(R.string.avans);
                    size = Integer.parseInt(pref.getString(key, "14"));
                    break;
                case 2:
                    key = itemView.getContext().getString(R.string.note);
                    size = Integer.parseInt(pref.getString(key, "12"));
                    break;
                case 3:
                    key = itemView.getContext().getString(R.string.date);
                    size = Integer.parseInt(pref.getString(key, "12"));
                    break;
                case 4:
                    key = itemView.getContext().getString(R.string.number);
                    size = Integer.parseInt(pref.getString(key, "14"));
                    break;
                case 5:
                    key = itemView.getContext().getString(R.string.date_period_key);
                    size = Integer.parseInt(pref.getString(key, "14"));
                    break;
            }
            return size;
        }

        private int setFontStyle(SharedPreferences pref, int textView) {
            int style = 0;
            String key;
            switch (textView) {
                case 0:
                    key = itemView.getContext().getString(R.string.summaKeyStyle);
                    style = Integer.parseInt(pref.getString(key, "0"));
                    break;
                case 1:
                    key = itemView.getContext().getString(R.string.avansKeyStyle);
                    style = Integer.parseInt(pref.getString(key, "0"));
                    break;
                case 2:
                    key = itemView.getContext().getString(R.string.noteKeyStyle);
                    style = Integer.parseInt(pref.getString(key, "0"));
                    break;
                case 3:
                    key = itemView.getContext().getString(R.string.dateKeyStyle);
                    style = Integer.parseInt(pref.getString(key, "0"));
                    break;
                case 4:
                    key = itemView.getContext().getString(R.string.numberKeyStyle);
                    style = Integer.parseInt(pref.getString(key, "2"));
                    break;
                case 5:
                    key = itemView.getContext().getString(R.string.zarplataKeyStyle);
                    style = Integer.parseInt(pref.getString(key, "0"));
                    break;
                case 6:
                    key = itemView.getContext().getString(R.string.avans_key_obsheeKeyStyle);
                    style = Integer.parseInt(pref.getString(key, "0"));
                    break;
                case 7:
                    key = itemView.getContext().getString(R.string.OstatokKeyStyle);
                    style = Integer.parseInt(pref.getString(key, "0"));
                    break;
            }
            return style;
        }

//        private LinearLayout.LayoutParams params(int weight, int height) {
//            float density = itemView.getContext().getResources().getDisplayMetrics().density;
//
//            int w = (int) (weight * density);
//            if (weight == -1) {
//                w = ViewGroup.LayoutParams.MATCH_PARENT;
//            }
//
//            int h = (int) (height * density);
//            if (height == -1) {
//                h = ViewGroup.LayoutParams.MATCH_PARENT;
//            }
//            return new LinearLayout.LayoutParams(w, h);
//        }
    }
}
/*
 */