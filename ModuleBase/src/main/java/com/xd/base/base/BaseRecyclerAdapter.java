package com.pw.base.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.base.utils.NStringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseRecyclerAdapter<T,T2 extends BaseViewHolder> extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int itemTypeITEM = 0;
    public static final int itemTypeHEAD = 1;
    public static final int itemTypeFOOT = 2;

    public static final String UPDATE_ITEM_HEAD = "updateItemHead";
    public static final String UPDATE_ITEM_FOOT = "updateItemFoot";
    public static final String UPDATE_ITEM_ITEM = "updateItemItem";


    public List<T> mDataList;

    public ItemGenerate mItemGenerate;
    public HeadGenerate mHeadGenerate;
    public FootGenerate mFootGenerate;

    public abstract ItemGenerate GenerateItem();

    public HeadGenerate GenerateHead(){
        return null;
    }
    public FootGenerate GenerateFoot(){
        return null;
    }

    public void updateFootView() {
        if (getFootCount()>0) {
            notifyItemRangeChanged(getHeadCount() + getRealItemCount(), getFootCount(),UPDATE_ITEM_FOOT);
        }
    }

    public void updateHeadView() {
        if (getHeadCount() > 0) {
            notifyItemRangeChanged(0, getHeadCount(),UPDATE_ITEM_HEAD);
        }
    }


    public T getData(int pos) {
        return mDataList.get(pos);
    }

    public void updateItem(int itemPos,String dataString) {
        notifyItemChanged(itemPos, dataString);
    }

    public BaseRecyclerAdapter() {
        mDataList = new ArrayList<>();
        mItemGenerate = GenerateItem();
        mHeadGenerate = GenerateHead();
        mFootGenerate = GenerateFoot();
    }

    public void move(int fromIndex, int tarIndex) {
//        Collections.swap(mDataList, fromIndex, tarIndex);
        notifyItemMoved(fromIndex,tarIndex);
    }

    public void dismiss(int index) {
        notifyItemRemoved(index);
    }

    public void setData(List<T> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public interface ItemGenerate<T,T2 extends BaseViewHolder>{
        void BindItemView(T data, T2 holder,int postion);

        T2 creatItemHolder(@NonNull ViewGroup parent, int viewType);

        boolean updateItemView(T2 holder, int position,List<Object> payloads);
    }

    public interface HeadGenerate<T extends BaseViewHolder>{
        void BindHeadView(T holder,int postion);

        T creatHeadHolder(@NonNull ViewGroup parent, int viewType);

        int HeadCount();

        boolean updateHeadView(T holder, int position);
    }

    public interface FootGenerate<T extends BaseViewHolder>{
        void BindFootView(T holder,int postion);

        T creatFootHolder(@NonNull ViewGroup parent, int viewType);

        int FootCount();

        boolean updateFootView(T holder, int position);
    }



    @Override
    public int getItemViewType(int position) {
        if (position < getHeadCount()) {
            return itemTypeHEAD;
        }
        if (position >= getHeadCount() + getRealItemCount() && position < getItemCount()) {
            return itemTypeFOOT;
        }else {
            return itemTypeITEM;
        }
    }

    public int getRealItemCount() {

        int itemCount = 0;
        if (mDataList == null) {
            itemCount = 0;
        }else {
            itemCount = mDataList.size();
        }
        return itemCount;
    }

    @Override
    public int getItemCount() {
        return getRealItemCount() + getHeadCount() + getFootCount();
    }

    public int getHeadCount(){
        if (mHeadGenerate != null) {
            return mHeadGenerate.HeadCount();
        }
        return 0;
    }

    public int getFootCount(){
        if (mFootGenerate != null) {
            return mFootGenerate.FootCount();
        }
        return 0;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder viewHolderTotal = null;
        if (viewType == itemTypeHEAD) {
            if (mHeadGenerate != null) {
                viewHolderTotal = mHeadGenerate.creatHeadHolder(parent, viewType);
            }
        } else if (viewType == itemTypeFOOT) {
            if (mFootGenerate != null) {
                viewHolderTotal = mFootGenerate.creatFootHolder(parent, viewType);
            }
        } else {
            viewHolderTotal = mItemGenerate.creatItemHolder(parent, viewType);
        }
        return viewHolderTotal;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        if (getItemViewType(position) == itemTypeHEAD) {
            if (mHeadGenerate != null) {
                mHeadGenerate.BindHeadView(holder,position);
            }
        } else if (getItemViewType(position) == itemTypeFOOT) {
            if (mFootGenerate != null) {
                int footindex = position-getHeadCount()-getRealItemCount();
                mFootGenerate.BindFootView(holder, footindex);
            }
        }else {
            int dataIndex = position - getHeadCount();
            T data = mDataList.get(dataIndex);
            mItemGenerate.BindItemView(data, (T2) holder, dataIndex);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        }else {
            boolean needReBindView = true;
            String payload = payloads.get(0).toString();
            if (NStringUtils.isNotBlank(payload)) {
                if (UPDATE_ITEM_HEAD.equals(payload)) {
                    if (mHeadGenerate != null) {
                        needReBindView = mHeadGenerate.updateHeadView(holder, position);
                    }
                } else if (UPDATE_ITEM_FOOT.equals(payload)) {
                    if (mFootGenerate != null) {
                        needReBindView = mFootGenerate.updateFootView(holder, position);
                    }
                } else {
                    if (mItemGenerate != null) {
                        try {
                            needReBindView = mItemGenerate.updateItemView(holder, position, payloads);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (needReBindView) {
                onBindViewHolder(holder, position);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setViewClick(View view, View.OnClickListener clickListener) {
        if (view == null) {
            return;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isItemClickable()) {
                    if (clickListener != null) {
                        clickListener.onClick(v);
                        lastClickTime = System.currentTimeMillis();
                    }
                }
            }
        });
    }

    private Long lastClickTime;

    private boolean isItemClickable() {
        if (lastClickTime != null) {
            long curTime = System.currentTimeMillis();
            if (curTime - lastClickTime < 500) {
                return false;
            }
        }
        return true;
    }

}

