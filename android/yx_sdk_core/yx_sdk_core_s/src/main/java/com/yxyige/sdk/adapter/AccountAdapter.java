package com.yxyige.sdk.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yxyige.sdk.bean.UserInfo;
import com.yxyige.sdk.core.YXSDKListener;
import com.yxyige.sdk.utils.AccountTools;
import com.yxyige.sdk.utils.Util;

import java.util.List;

public class AccountAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mFactory;
    private List<UserInfo> accountList;
    private YXSDKListener callBack;
    private PopupWindow window;

    public AccountAdapter(Context context, PopupWindow window, LayoutInflater mFactory, List<UserInfo> accountList, YXSDKListener callBack) {
        this.context = context;
        this.window = window;
        this.mFactory = mFactory;
        this.accountList = accountList;
        this.callBack = callBack;
    }

    public void setList(List<UserInfo> accountList) {
        this.accountList = accountList;
    }

    @Override
    public int getCount() {
        if (accountList != null)
            return accountList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (accountList != null)
            return accountList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = mFactory.inflate(
                    Util.getIdByName("yx_account_item", "layout", context.getPackageName(), context), null);
            vh = new ViewHolder();
            vh.fg_name = (TextView) convertView.findViewById(Util.getIdByName("fg_name", "id", context.getPackageName(), context));
            vh.fg_delete = convertView.findViewById(Util.getIdByName("fg_delete", "id", context.getPackageName(), context));
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.fg_name.setFocusable(false);
        vh.fg_name.setText(accountList.get(position).getUname());
        vh.fg_name.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (callBack != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("uname", accountList.get(position).getUname());
                    bundle.putString("upwd", accountList.get(position).getPassword());
                    callBack.onSuccess(bundle);

                    if (window != null) {
                        window.dismiss();
                    }
                }
            }
        });
        vh.fg_delete.setFocusable(false);
        vh.fg_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AccountTools.delAccountFromFile(context, accountList.get(position).getUname());
                accountList.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public class ViewHolder {
        public TextView fg_name;
        private View fg_delete;
    }

}
