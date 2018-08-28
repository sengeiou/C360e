package com.alfredposclient.activity.kioskactivity.subpos;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.VerifyDialog;
import com.alfredbase.javabean.SubPosBean;
import com.alfredbase.store.sql.SubPosBeanSQL;
import com.alfredbase.utils.DialogFactory;
import com.alfredposclient.R;

import java.util.List;
import java.util.Map;

public class SubPosManagePage extends BaseActivity {
    private ListView lv_subpos;
    private TextView tv_title_name;
    private List<SubPosBean>  subPosBeans;
    private VerifyDialog dialog;
    private SubPosItemAdapter subPosItemAdapter;
    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.sub_pos_manage_page);
        lv_subpos = (ListView) findViewById(R.id.lv_subpos);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText("Sub Pos Manage");
        subPosBeans = SubPosBeanSQL.getAllSubPosBean();
        findViewById(R.id.ll_print).setVisibility(View.GONE);
        findViewById(R.id.btn_back).setOnClickListener(this);
        subPosItemAdapter = new SubPosItemAdapter();
        lv_subpos.setAdapter(subPosItemAdapter);
        dialog = new VerifyDialog(context, handler);
    }

    @Override
    protected void handlerClickEvent(View v) {
        super.handlerClickEvent(v);
        switch (v.getId()){
            case R.id.btn_back:
                this.finish();
                break;
        }
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case VerifyDialog.DIALOG_RESPONSE:
                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    final SubPosBean subPosBean = (SubPosBean) map.get("object");
                    if(subPosBean == null){
                        return;
                    }
                    DialogFactory.commonTwoBtnDialog(context, "Warning", "Sub Pos UNABLE to close if Unassigned",
                            context.getString(R.string.cancel), context.getString(R.string.ok),
                            null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SubPosBean s = subPosBean;
                                    s.setSubPosStatus(ParamConst.SUB_POS_STATUS_CLOSE);
                                    SubPosBeanSQL.updateSubPosBean(s);
                                    subPosItemAdapter.notifyDataSetChanged();
                                }
                            });
                    break;
            }
        }
    };

    class SubPosItemAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        SubPosItemAdapter(){
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return subPosBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return subPosBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.subpos_item, null);
                holder = new ViewHolder();
                holder.tv_pos_id = (TextView) convertView.findViewById(R.id.tv_pos_id);
                holder.tv_userName = (TextView) convertView.findViewById(R.id.tv_userName);
                holder.tv_device_id = (TextView) convertView.findViewById(R.id.tv_device_id);
                holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                holder.tv_tag = (TextView) convertView.findViewById(R.id.tv_tag);
                holder.btn_check = (Button) convertView.findViewById(R.id.btn_check);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            SubPosBean subPosBean = subPosBeans.get(position);
            holder.tv_pos_id.setText(subPosBean.getId()+"");
            holder.tv_userName.setText(subPosBean.getUserName());
            holder.tv_device_id.setText(subPosBean.getDeviceId());
            String status = "Close";
            boolean showCheck = false;
            if(subPosBean.getSubPosStatus() == ParamConst.SUB_POS_STATUS_OPEN){
                status = "Open";
                showCheck = true;
            }
            holder.tv_status.setText(status);
            holder.btn_check.setText("Unassign");
            holder.tv_tag.setText(subPosBean.getNumTag());
            if(showCheck){
                holder.btn_check.setVisibility(View.VISIBLE);
            }else{
                holder.btn_check.setVisibility(View.INVISIBLE);
            }
            holder.btn_check.setTag(subPosBean);
            holder.btn_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    dialog.show("verify",view.getTag());
                }
            });

            return convertView;
        }

        class ViewHolder {
            public TextView tv_pos_id;
            public TextView tv_userName;
            public TextView tv_device_id;
            public TextView tv_status;
            public TextView tv_tag;
            public Button btn_check;
        }
    }


}
