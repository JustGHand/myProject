package com.pw.codeset.abilities.schedule;

import android.view.View;
import android.widget.CheckBox;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.baseutils.others.recycle.BaseRecyclerAdapter;
import com.pw.baseutils.utils.ArrayUtils;
import com.pw.baseutils.utils.NStringUtils;
import com.pw.codeset.R;
import com.pw.codeset.abilities.notes.NotesAdapter;
import com.pw.codeset.application.MyApp;
import com.pw.codeset.base.BaseFragment;
import com.pw.codeset.databean.ExpandableItem;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.databean.ScheduleBean;
import com.pw.codeset.manager.NotesManager;
import com.pw.codeset.manager.ScheduleManager;
import com.pw.codeset.utils.CalendarUtilKt;
import com.pw.codeset.utils.CommenUseViewUtils;
import com.pw.codeset.utils.IntentUtils;
import com.pw.codeset.weidgt.SelectDialog;
import com.pw.codeset.weidgt.WarpLinearLayout;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends BaseFragment {
    @Override
    protected int getContentId() {
        return R.layout.activity_schedule_list;
    }

    RecyclerView mRecyclerView;
    List<ScheduleBean> mDataList;

    ScheduleAdapter mAdapter;


    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.schedule_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ScheduleAdapter(new ScheduleAdapter.OnItemClickListener<ScheduleBean>() {
            @Override
            public void onClick(@Nullable ScheduleBean data, int pos) {

            }

            @Override
            public boolean onLongClick(@Nullable ScheduleBean data, int pos) {
                return false;
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void dealWithData() {
    }

    @Override
    protected void finishData() {
        super.finishData();
        refreshList();
    }

    @Override
    protected void onNormalResume() {
        super.onNormalResume();
        refreshList();
    }

    @Override
    protected void onMenuClick(View view) {
        toScheduleDetail(null);
    }

    @Override
    protected void onBackClick(View view) {
        super.onBackClick(view);
        CalendarUtilKt.startCalendar(MyApp.getInstance(),System.currentTimeMillis());
    }

    private void refreshList() {
        mDataList = ScheduleManager.getInstance().getScheduleList();
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        List<ExpandableItem.Group<ScheduleBean>> data = new ArrayList<>();
        ExpandableItem.Group<ScheduleBean> earlierGroup = new ExpandableItem.Group<ScheduleBean>("更早",ScheduleManager.getInstance().getEarlierScheduleList(), false);
        data.add(earlierGroup);
        ExpandableItem.Group<ScheduleBean> todayGroup = new ExpandableItem.Group<ScheduleBean>("今天",ScheduleManager.getInstance().getTodayScheduleList(), false);
        data.add(todayGroup);
        ExpandableItem.Group<ScheduleBean> weekGroup = new ExpandableItem.Group<ScheduleBean>("本周",ScheduleManager.getInstance().getWeekScheduleList(), false);
        data.add(weekGroup);
        ExpandableItem.Group<ScheduleBean> monthGroup = new ExpandableItem.Group<ScheduleBean>("本月",ScheduleManager.getInstance().getMonthScheduleList(), false);
        data.add(monthGroup);
        ExpandableItem.Group<ScheduleBean> yearGroup = new ExpandableItem.Group<ScheduleBean>("今年",ScheduleManager.getInstance().getYearScheduleList(), false);
        data.add(yearGroup);
        mAdapter.setGroups(data);
    }

    private void toScheduleDetail(ScheduleBean scheduleBean) {
        IntentUtils.INSTANCE.toScheduleEdit(requireActivity());
    }

}
