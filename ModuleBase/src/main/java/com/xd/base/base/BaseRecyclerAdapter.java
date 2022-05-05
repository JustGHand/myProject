package com.xd.base.base;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xd.base.utils.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T,T2 extends BaseViewHolder> extends RecyclerView.Adapter<BaseViewHolder> {

    final int itemTypeITEM = 0;
    final int itemTypeHEAD = 1;
    final int itemTypeFOOT = 2;

    final String UPDATE_ITEM_HEAD = "updateItemHead";
    final String UPDATE_ITEM_FOOT = "updateItemFoot";
    final String UPDATE_ITEM_ITEM = "updateItemItem";


    public List<T> mDataList;

    private ItemGenerate mItemGenerate;
    private HeadGenerate mHeadGenerate;
    private FootGenerate mFootGenerate;

    public abstract ItemGenerate GenerateItem();

    public HeadGenerate GenerateHead(){
        return null;
    }
    public FootGenerate GenerateFoot(){
        return null;
    }

    public void updateFootView() {
        if (getFootCount()>0) {
            notifyItemRangeChanged(getHeadCount() + getItemCount(), getFootCount(),UPDATE_ITEM_FOOT);
        }
    }

    public void updateHeadView() {
        if (getHeadCount() > 0) {
            notifyItemRangeChanged(0, getHeadCount(),UPDATE_ITEM_HEAD);
        }
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

    public void setData(List<T> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public interface ItemGenerate<T,T2 extends BaseViewHolder>{
        void BindItemView(T data, T2 holder,int postion);

        T2 creatItemHolder(@NonNull ViewGroup parent, int viewType);

        boolean updateItemView(T2 holder, int position,List<Object> payloads);
    }

    public interface HeadGenerate{
        void BindHeadView(BaseViewHolder holder,int postion);

        BaseViewHolder creatHeadHolder(@NonNull ViewGroup parent, int viewType);

        int HeadCount();

        boolean updateHeadView(BaseViewHolder holder, int position);
    }

    public interface FootGenerate{
        void BindFootView(BaseViewHolder holder,int postion);

        BaseViewHolder creatFootHolder(@NonNull ViewGroup parent, int viewType);

        int FootCount();

        boolean updateFootView(BaseViewHolder holder, int position);
    }



    @Override
    public int getItemViewType(int position) {
        if (position < getHeadCount()) {
            return itemTypeHEAD;
        }
        if (position >= getHeadCount() + mDataList.size() && position < getItemCount()) {
            return itemTypeFOOT;
        }else {
            return itemTypeITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size()+getHeadCount()+getFootCount();
    }

    private int getHeadCount(){
        if (mHeadGenerate != null) {
            return mHeadGenerate.HeadCount();
        }
        return 0;
    }

    private int getFootCount(){
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
                int footindex = position-getHeadCount()-mDataList.size();
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
            if (UPDATE_ITEM_HEAD.equals(payloads.get(0).toString())) {
                if (mHeadGenerate != null) {
                    needReBindView = mHeadGenerate.updateHeadView(holder, position);
                }
            }else if (UPDATE_ITEM_FOOT.equals(payloads.get(0).toString())) {
                if (mFootGenerate != null) {
                    needReBindView = mFootGenerate.updateFootView(holder, position);
                }
            } else if (NStringUtils.isNotBlank(payloads.get(0).toString())) {
                needReBindView = mItemGenerate.updateItemView((T2) holder, position,payloads);
            }
            if (needReBindView) {
                onBindViewHolder(holder, position);
            }
        }
    }
}

