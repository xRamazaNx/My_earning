package com.press.myearnings.adapter;

import android.animation.Animator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.press.myearnings.R;
import com.press.myearnings.ZarplataActivity;
import com.press.myearnings.database.SchemaDB;
import com.press.myearnings.database.ZarplataHelper;
import com.press.myearnings.model.Dannie;
import com.press.myearnings.model.DannieCard;
import com.press.myearnings.setting.ColumnName;
import com.press.myearnings.setting.SettingColorSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;
import static android.view.View.inflate;

/**
 * Created by PRESS on 09.10.2017.
 */

public class AdapterCard extends RecyclerView.Adapter<AdapterCard.HolderCard> {
    private final List<DannieCard> dannieCardList;
    private final Handler handler;
    private final boolean isArchive;

    public AdapterCard(List<DannieCard> list, Handler handler, boolean isArchive) {
        dannieCardList = list;
        this.handler = handler;
        this.isArchive = isArchive;
    }

    @NonNull
    @Override
    public HolderCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new HolderCard(inflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCard holder, int position) {
        holder.bindHolder(dannieCardList.get(position));
    }

    @Override
    public int getItemCount() {
        return dannieCardList.size();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    class HolderCard extends RecyclerView.ViewHolder {
        //        private ArrayList<Dannie> listForAdapter;

        private String sNameCard;
        private String sNameTable;

        private final TextView info;
        private final TextView nameCard;
        private final TextView dateCard;

        private final TextView textAllMoney;
        private final TextView textAllAvans;
        private final TextView textAllKVidache;

        private final TextView textAllMoneyTitle;
        private final TextView textAllAvansTitle;
        private final TextView textAllKVidacheTitle;
        private String sDateCreatedTable;
        private String dateModify;

        HolderCard(LayoutInflater inflater, ViewGroup item) {
            super(inflater.inflate(R.layout.card_view, item, false));

            info = itemView.findViewById(R.id.card_info_button);
            nameCard = itemView.findViewById(R.id.id_name_card);
            dateCard = itemView.findViewById(R.id.id_date_card);
            textAllMoney = itemView.findViewById(R.id.id_summ_all_money_card);
            textAllAvans = itemView.findViewById(R.id.id_summ_avans_card);
            textAllKVidache = itemView.findViewById(R.id.id_summ_money_k_vidache_card);

            textAllMoneyTitle = itemView.findViewById(R.id.card_title_vsego);
            textAllAvansTitle = itemView.findViewById(R.id.card_title_avans);
            textAllKVidacheTitle = itemView.findViewById(R.id.card_title_ostatok);
            onClickItem();

            itemView.setOnLongClickListener(longClickListener());
        }

        private View.OnClickListener infoViewClickListener(DannieCard dannieCard) {
            Dannie d = new Dannie();

            try {
                long dateC = Long.parseLong(dannieCard.getDateForCreated());
                d.setDateMl(dateC);
                sDateCreatedTable = d.getDate(-2);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                sDateCreatedTable = dannieCard.getDateForCreated();
            }

            try {
                long dateM = Long.parseLong(dannieCard.getDateForModify());
                d.setDateMl(dateM);
                dateModify = d.getDate(-2);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(itemView.getContext()).inflate(R.layout.card_info_window, null);
            TextView infoDateCreate = linearLayout.findViewById(R.id.info_date_created);
            TextView infoDateModify = linearLayout.findViewById(R.id.info_date_modify);

            infoDateCreate.setText(sDateCreatedTable);
            infoDateModify.setText(dateModify);

            return v -> {
                PopupWindow window = new PopupWindow(itemView.getContext());
                window.setContentView(linearLayout);
                window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                window.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new BitmapDrawable());
                window.setFocusable(true);
                window.setOutsideTouchable(true);
//                    int xOff = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
//                            itemView.getContext().getResources().getDisplayMetrics());

                int[] locat = new int[2];
                itemView.getLocationInWindow(locat);
                window.showAtLocation(info, Gravity.END | Gravity.TOP, locat[0], locat[1]);
            };

        }

        private void onClickItem() {
            itemView.setOnClickListener(view -> {
                getCardDannie();

                Intent intent = new Intent(itemView.getContext(), ZarplataActivity.class);
                intent.putExtra("namecard", sNameCard);
                intent.putExtra("nametable", sNameTable);
                intent.putExtra("namedatecreatedtable", sDateCreatedTable);
                intent.putExtra("namedatemodifydtable", dateModify);
                intent.putExtra("toolbar", isArchive);
                itemView.getContext().startActivity(intent);


            });
        }

        private View.OnLongClickListener longClickListener() {
            return view -> {
                final int delete = R.string.delete_the_entry;
                final int complete = R.string.want_to_set_arhive;
                final int returnCard = R.string.RETURN_ACCOUNTING_CARD;
                final Context context = new ContextThemeWrapper(itemView.getContext(), R.style.style_popupMenu);
                PopupMenu popup = new PopupMenu(context, view);
//                    PopupMenu popup = new PopupMenu()
                popup.inflate(R.menu.menu_item_card_view);
                if (isArchive) {
                    popup.getMenu().getItem(0).setTitle(returnCard);
                }
                popup.setOnMenuItemClickListener(item -> {
                    final RadioGroup chooseOneOrLast = (RadioGroup) inflate(itemView.getContext(), R.layout.choose_one_or_last, null);
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(itemView.getContext());
                    if (item.getItemId() == R.id.id_delete_item_cardView) {
                        dialog.setMessage(delete);
                    } else if (item.getItemId() == R.id.id_complete_card_menu_item) {
                        if (isArchive) {
                            dialog.setMessage(returnCard);
                            dialog.setView(chooseOneOrLast);
                        } else {
                            dialog.setMessage(complete);
                        }
                    }
                    dialog.setPositiveButton(R.string.YES, (dialogInterface, in) -> {
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
                        if (item.getItemId() == R.id.id_delete_item_cardView) {
                            deleteArchiveItem();
                        } else if (item.getItemId() == R.id.id_complete_card_menu_item) {
                            if (isArchive) {
                                onReturnArchive(chooseOneOrLast.getCheckedRadioButtonId());
                            } else {
                                onSetArchive();
                            }
                        }
//                                        }
//                                    }, 200);
                    })
                            .setNegativeButton(R.string.NO, (dialogInterface, i) -> dialogInterface.cancel())
                            .show();


                    return true;
                });
                popup.show();
                return true;
            };
        }

        void bindHolder(DannieCard dannieCard) {
            info.setOnClickListener(infoViewClickListener(dannieCard));

            nameCard.setText(dannieCard.getNameCard());
            if (isArchive) {
                dateCard.setText(dannieCard.getDateForArchive());
            } else {
                dateCard.setVisibility(GONE);
            }

            SharedPreferences pref = itemView.getContext().getSharedPreferences(
                    itemView.getContext().getPackageName() + "_preferences", Context.MODE_PRIVATE);
            String rub = itemView.getContext().getString(R.string.simvol_rub_string);

            if (pref.getBoolean(rub, false)) {
                String simvolKey = itemView.getContext().getString(R.string.chek_simvol);
                String simvol = pref.getString(simvolKey, "ք Рубль");

                textAllMoney.setText(String.format("%s " + simvol.charAt(0), dannieCard.getSumma()));
                textAllAvans.setText(String.format("%s " + simvol.charAt(0), dannieCard.getAvans()));
                textAllKVidache.setText(String.format("%s " + simvol.charAt(0), dannieCard.getOstatok()));
            } else {

                textAllMoney.setText(dannieCard.getSumma());
                textAllAvans.setText(dannieCard.getAvans());
                textAllKVidache.setText(dannieCard.getOstatok());

            }

            textAllMoney.setTextSize(setFontSize(pref, 0));
            textAllAvans.setTextSize(setFontSize(pref, 1));
            textAllKVidache.setTextSize(setFontSize(pref, 2));

            textAllMoney.setTypeface(null, setFontStyle(pref, 0));
            textAllAvans.setTypeface(null, setFontStyle(pref, 1));
            textAllKVidache.setTypeface(null, setFontStyle(pref, 2));

            ColumnName columnName = dannieCardList.get(getBindingAdapterPosition()).columnName;
            textAllMoneyTitle.setText(columnName.allSumma);
            textAllAvansTitle.setText(columnName.allAvans);
            textAllKVidacheTitle.setText(columnName.ostatok);

            SettingColorSet.getTextColor(pref, nameCard, SettingColorSet.ViewName.name);
            SettingColorSet.getTextColor(pref, dateCard, SettingColorSet.ViewName.dateCard);

            SettingColorSet.getTextColor(pref, textAllMoney, SettingColorSet.ViewName.summaCard);
            SettingColorSet.getTextColor(pref, textAllAvans, SettingColorSet.ViewName.avansCard);
            SettingColorSet.getTextColor(pref, textAllKVidache, SettingColorSet.ViewName.ostatok);

            SettingColorSet.getTextColor(pref, textAllMoneyTitle, SettingColorSet.ViewName.summaCardTitle);
            SettingColorSet.getTextColor(pref, textAllAvansTitle, SettingColorSet.ViewName.avansCardTitle);
            SettingColorSet.getTextColor(pref, textAllKVidacheTitle, SettingColorSet.ViewName.ostatokTitle);

        }

        private int setFontSize(SharedPreferences pref, int textView) {
            int size = 16;
            switch (textView) {
                case 0:
                    String keyZarplata = itemView.getContext().getString(R.string.zarplata);
                    size = Integer.parseInt(pref.getString(keyZarplata, "16"));
                    break;
                case 1:
                    String keyAvans = itemView.getContext().getString(R.string.avans_key_obshee);
                    size = Integer.parseInt(pref.getString(keyAvans, "16"));
                    break;
                case 2:
                    String keyOstatok = itemView.getContext().getString(R.string.Ostatok);
                    size = Integer.parseInt(pref.getString(keyOstatok, "16"));
                    break;
            }
            return size;
        }

        private int setFontStyle(SharedPreferences pref, int textview) {
            int style = 0;
            switch (textview) {
                case 0:
                    String keyZarplata = itemView.getContext().getString(R.string.zarplataKeyStyle);
                    style = Integer.parseInt(pref.getString(keyZarplata, "0"));
                    break;
                case 1:
                    String keyAvans = itemView.getContext().getString(R.string.avans_key_obsheeKeyStyle);
                    style = Integer.parseInt(pref.getString(keyAvans, "0"));
                    break;
                case 2:
                    String keyOstatok = itemView.getContext().getString(R.string.OstatokKeyStyle);
                    style = Integer.parseInt(pref.getString(keyOstatok, "0"));
                    break;
            }
            return style;
        }

        private void animationSelect(ViewGroup v) {
            int centerX = (v.getLeft() + v.getRight()) / 2;
            int centerY = (v.getTop() + v.getBottom()) / 2;
            int finalRadius = Math.max(v.getWidth(), v.getHeight());
            Animator animation = ViewAnimationUtils.createCircularReveal(itemView, centerX, centerY, 0, finalRadius);
            animation.setDuration(400);
            v.setVisibility(View.VISIBLE);
            animation.start();
        }

//        private void list() {
//            sNameTable = dannieCardList.get(getAdapterPosition()).getNameTable();
//
////            listForAdapter = new ArrayList<>();
//            SQLiteDatabase db = new ZarplataHelper(itemView.getContext()).getWritableDatabase();
//            Cursor cursorDetail;
//
//            cursorDetail = db.query(sNameTable, null, null, null, null, null, null);
//
//            if (cursorDetail.moveToFirst()) {
//                // заполнение массива из тек строк которые определены для архива
//                do {
//                    Dannie d = new Dannie();
//                    d.setSumma(cursorDetail.getString(cursorDetail.getColumnIndexOrThrow(SchemaDB.Table.Cols.RUL1)));
//                    d.setAvans(cursorDetail.getString(cursorDetail.getColumnIndexOrThrow(SchemaDB.Table.Cols.AVANS)));
//                    d.setDateInt(Integer.parseInt(cursorDetail.getString(cursorDetail.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE))));
//                    d.setComent(cursorDetail.getString(cursorDetail.getColumnIndexOrThrow(SchemaDB.Table.Cols.COMENT)));
////                    listForAdapter.add(d);
//
//                } while (cursorDetail.moveToNext());
//
//                db.close();
//                cursorDetail.close();
//            }
//        }

        private void onReturnArchive(int checkedRadioButtonId) {
            getCardDannie();
            dateModify = String.valueOf(Calendar.getInstance().getTimeInMillis());

            SQLiteDatabase database = new ZarplataHelper(itemView.getContext()).getWritableDatabase();

            // save names
            ColumnName columnName = new ColumnName();
            columnName.inflateFromDB(database, sNameTable);

            //пересчет данных карточек из активных, чтоб потом добавить обратно в таблицу(для переключателя в начало-в конец)
            ArrayList<DannieCard> arrayList = new ArrayList<>();
            Cursor cursor = database.query(SchemaDB.Table.NAME_CARD, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {

                    String tableName = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.NAME_TABLE_DATA));
                    if (tableName.equals(sNameTable))
                        continue;

                    DannieCard dA = new DannieCard();
                    dA.setNameTable(tableName);
                    dA.setNameCard(cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.NAME_FOR_TABLE_CARD)));
                    dA.setDateForCreated(cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE_CREATE_TABLE)));
                    dA.setDateForModify(cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE_MODIFY_TABLE)));
                    dA.columnName.inflateFromDB(database, tableName);
                    dA.isArchive = cursor.getInt(cursor.getColumnIndexOrThrow(SchemaDB.Table.IS_ARCHIVE)) == 1;
                    arrayList.add(dA);

                } while (cursor.moveToNext());
                //удаляем все из таблицы после пересчета
                database.delete(SchemaDB.Table.NAME_CARD, null, null);
            }
            cursor.close();

            ContentValues addedValue = new ContentValues();
            addedValue.put(SchemaDB.Table.Cols.NAME_FOR_TABLE_CARD, sNameCard);
            addedValue.put(SchemaDB.Table.NAME_TABLE_DATA, sNameTable);
            addedValue.put(SchemaDB.Table.Cols.DATE_CREATE_TABLE, sDateCreatedTable);
            addedValue.put(SchemaDB.Table.Cols.DATE_MODIFY_TABLE, dateModify);
            // we return this card
            addedValue.put(SchemaDB.Table.IS_ARCHIVE, 0);
            //если переключатель на "в начало"
            if (checkedRadioButtonId == R.id.move_one) {
                database.insert(SchemaDB.Table.NAME_CARD, null, addedValue);
            }

            for (int i = 0; i < arrayList.size(); i++) {
                DannieCard dannieCard = arrayList.get(i);

                ContentValues val = new ContentValues();
                val.put(SchemaDB.Table.Cols.NAME_FOR_TABLE_CARD, dannieCard.getNameCard());
                val.put(SchemaDB.Table.NAME_TABLE_DATA, dannieCard.getNameTable());
                val.put(SchemaDB.Table.Cols.DATE_CREATE_TABLE, dannieCard.getDateForCreated());
                val.put(SchemaDB.Table.Cols.DATE_MODIFY_TABLE, dannieCard.getDateForModify());
                val.put(SchemaDB.Table.IS_ARCHIVE, dannieCard.isArchive ? 1 : 0);

                database.insert(SchemaDB.Table.NAME_CARD, null, val);
                dannieCard.columnName.setNamesToDB(database, dannieCard.getNameTable());

            }
            //если переключатель на "в конец"
            if (checkedRadioButtonId == R.id.move_last) {
                database.insert(SchemaDB.Table.NAME_CARD, null, addedValue);
            }
            columnName.setNamesToDB(database, sNameTable);

            database.close();
            handler.sendEmptyMessage(11);

        }

        private void onSetArchive() {
            getCardDannie();
            dateModify = String.valueOf(Calendar.getInstance().getTimeInMillis());
            SQLiteDatabase database = new ZarplataHelper(itemView.getContext()).getWritableDatabase();
            //1
            ContentValues value = new ContentValues();
            value.put(SchemaDB.Table.IS_ARCHIVE, 1);
            value.put(SchemaDB.Table.Cols.DATE_MODIFY_TABLE, dateModify);
            database.update(SchemaDB.Table.NAME_CARD, value, SchemaDB.Table.NAME_TABLE_DATA + " = ?", new String[]{sNameTable});

            handler.sendEmptyMessage(11);

        }

        private void getCardDannie() {
            sNameCard = dannieCardList.get(getAdapterPosition()).getNameCard();
            sNameTable = dannieCardList.get(getAdapterPosition()).getNameTable();
            sDateCreatedTable = dannieCardList.get(getAdapterPosition()).getDateForCreated();
            dateModify = dannieCardList.get(getAdapterPosition()).getDateForModify();
        }

        private void deleteArchiveItem() {

            sNameTable = dannieCardList.get(getAdapterPosition()).getNameTable();

            SQLiteDatabase database = new ZarplataHelper(itemView.getContext()).getWritableDatabase();
            database.execSQL("drop table " + sNameTable);

            database.delete(SchemaDB.Table.NAME_CARD, SchemaDB.Table.NAME_TABLE_DATA + " = ?", new String[]{sNameTable});
            database.close();
            handler.sendEmptyMessage(11);


        }
    }


}
