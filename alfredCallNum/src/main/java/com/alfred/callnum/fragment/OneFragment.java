package com.alfred.callnum.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alfred.callnum.R;
import com.alfred.callnum.activity.MainActivity;
import com.alfred.callnum.adapter.CallBean;
import com.alfred.callnum.adapter.MycallAdapter;
import com.alfred.callnum.adapter.RvListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OneFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OneFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView  re_one,re_two,re_three,re_four;
    private List<CallBean> mDatas = new ArrayList<>();
    MycallAdapter mAdapter;
    public OneFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OneFragment newInstance(String param1, String param2) {
        OneFragment fragment = new OneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initData() {

        for (int i = 0; i <20 ; i++) {

            CallBean call=new CallBean();
            call.setId(i);
            call.setName("name "+i);
            mDatas.add(call);
        }

        Collections.reverse(mDatas);
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        re_one=(RecyclerView)getActivity().findViewById(R.id.review_one);
        re_two=(RecyclerView)getActivity().findViewById(R.id.review_two);
//        re_three=(RecyclerView)getActivity().findViewById(R.id.review_three);
//        re_four=(RecyclerView)getActivity().findViewById(R.id.review_four);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity() ); //设置布局管理器
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity() ); //设置布局管理器
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity() ); //设置布局管理器
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(getActivity() ); //设置布局管理器
         re_one.setLayoutManager(layoutManager); //设置为垂直布局，这也是默认的
         re_two.setLayoutManager(layoutManager2);
//         re_three.setLayoutManager(layoutManager3);
//         re_four.setLayoutManager(layoutManager4);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);

        mAdapter = new MycallAdapter(getActivity(), mDatas, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {
               // mAdapter.notifyItemChanged(position);
//                String content = "";
//                Intent intent=new Intent();
//                intent.setClass(getActivity(), MainActivity.class);
//                 startActivity(intent);
            }
        });

       re_one.setAdapter(mAdapter);
        re_two.setAdapter(mAdapter);
//        re_three.setAdapter(mAdapter);
//        re_four.setAdapter(mAdapter);

        initData();
   //



    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
