package com.pw.codeset.abilities.read.bookshelf;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.codeset.R;
import com.pw.codeset.manager.BookManager;
import com.pw.codeset.abilities.read.leadbook.LeadBookActivity;
import com.pw.codeset.abilities.read.read.ReadActivity;
import com.pw.codeset.base.BaseFragment;
import com.pw.codeset.databean.BookBean;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.DialogUtils;
import com.pw.baseutils.others.recycle.BaseRecyclerAdapter;
import com.pw.baseutils.utils.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class BookShelfFragment extends BaseFragment {
    @Override
    protected int getContentId() {
        return R.layout.activity_bookshelf;
    }

    RecyclerView mRecyclerView;
    BookShelfAdapter mAdapter;
    private PopupWindow mPopWindow;

    List<BookBean> mBooks;

    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.bookshelf_list);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new BookShelfAdapter(getContext());
        mAdapter.setItemCLickListener(new BaseRecyclerAdapter.onItemClickListener<BookBean>() {
            @Override
            public void onClick(BookBean data, int pos) {
                if (data!=null) {
                    Intent intent = new Intent(getContext(), ReadActivity.class);
                    intent.putExtra(Constant.BOOK_ID, data.getBookId());
                    startActivity(intent);
//                    LogToastUtils.showLargeToast(data.getBookName());
                }
            }

            @Override
            public boolean onLongClick(BookBean data, int pos) {
                return false;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onMenuClick(View view) {
        showMorePopup(view);
    }

    @Override
    protected void dealWithData() {
    }

    @Override
    protected void onNormalResume() {
        super.onNormalResume();
        mBooks = BookManager.getInstance().getBookList();
        mAdapter.setData(mBooks);
    }

    private void toggleEditMode(boolean editMode) {
        mAdapter.startEdit();
    }

    private void addBook() {
        startActivity(new Intent(getContext(), LeadBookActivity.class));
    }


    private void showMorePopup(View view) {

//        if (true) {
//            List<DialogUtils.DialogMenu> dialogMenus = new ArrayList<>();
//            DialogUtils.DialogMenu manager = new DialogUtils.DialogMenu("书籍管理", R.mipmap.icon_bookshelf_manager, "manager");
//            DialogUtils.DialogMenu leadBook = new DialogUtils.DialogMenu("导入书籍", R.mipmap.icon_booklead, "leadBook");
//            dialogMenus.add(manager);
//            dialogMenus.add(leadBook);
//            DialogUtils.showPopUpView(getActivity(), view, dialogMenus, new DialogUtils.MenuClickListener() {
//                @Override
//                public void onItemClick(String itemTag) {
//                    if (NStringUtils.isNotBlank(itemTag)) {
//                        if (itemTag.equals("manager")) {
//                            toggleEditMode(true);
//                            if (mPopWindow != null) {
//                                mPopWindow.dismiss();
//                            }
//                        } else if (itemTag.equals("leadBook")) {
//                            addBook();
//                        }
//                    }
//                }
//            });
//            return;
//        }


        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_bookshelf_popup, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);

        TextView managerButton = contentView.findViewById(R.id.bookshelf_manager);
        TextView historyButton = contentView.findViewById(R.id.bookshelf_history);
        TextView leadBookButton = contentView.findViewById(R.id.bookshelf_leadbook);
        ImageView topTrangle = contentView.findViewById(R.id.bookshelf_pop_top);
        managerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditMode(true);
                mPopWindow.dismiss();
            }
        });

        leadBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }
        });

        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        //显示PopupWindow
        mPopWindow.showAsDropDown(view);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.7f;
        getActivity().getWindow().setAttributes(lp);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1;
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }
}
