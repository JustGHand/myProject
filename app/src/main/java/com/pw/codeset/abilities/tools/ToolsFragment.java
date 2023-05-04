package com.pw.codeset.abilities.tools;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.pw.codeset.R;
import com.pw.codeset.abilities.fileTransfer.FileTransferActivity;
import com.pw.codeset.abilities.games.GamesActivity;
import com.pw.codeset.abilities.games.sudoku.SudokuActivity;
import com.pw.codeset.abilities.gdMap.GDMapActivity;
import com.pw.codeset.base.BaseFragment;
import com.pw.codeset.databean.ToolsBean;
import com.pw.codeset.myTest.MyTestActivity;
import com.pw.codeset.utils.ResourceUtils;
import com.pw.codeset.weidgt.AnimContainer;

import java.util.ArrayList;
import java.util.List;

public class ToolsFragment extends BaseFragment {
    private String GAMS;
    private String FILE_TRANSFER;
    private String DEVELOP_TEST;
    private String GD_MAP;
    private String SUDOKE;

    @Override
    protected int getContentId() {
        return R.layout.activity_tools;
    }

    AnimContainer mToolsContainer;

    List<ToolsBean> mToolList;

    boolean isAnimating = false;


    @Override
    protected void dealWithData() {
        GAMS = this.getString(R.string.tools_game);
        FILE_TRANSFER = ResourceUtils.getResString(R.string.tools_file_transfer);
        DEVELOP_TEST = ResourceUtils.getResString(R.string.tools_develop_test);
        GD_MAP = ResourceUtils.getResString(R.string.tools_develop_map);
        SUDOKE = ResourceUtils.getResString(R.string.tools_develop_sudoke);
        mToolList = new ArrayList<>();
        mToolList.add(new ToolsBean(GAMS,R.mipmap.game_block_image));
        mToolList.add(new ToolsBean(FILE_TRANSFER,R.mipmap.image_file_transfer));
        mToolList.add(new ToolsBean(DEVELOP_TEST,R.mipmap.icon_tool_test));
        mToolList.add(new ToolsBean(GD_MAP,R.mipmap.icon_tool_map));
        mToolList.add(new ToolsBean(SUDOKE,R.mipmap.icon_tool_sudoke));

    }

    @Override
    protected void finishData() {
        super.finishData();

        for (int i = 0; i < mToolList.size(); i++) {
            ToolsBean toolsBean = mToolList.get(i);
            ViewGroup itemView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.item_toollist, mToolsContainer, false);

            ImageView itemImageView = itemView.findViewById(R.id.tool_icon);
            Integer toolImg = toolsBean.getIconUrl();
            if (toolImg != null) {
                Glide.with(getContext()).load(toolImg).into(itemImageView);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (toolsBean.getToolName().equals(GAMS)) {
                        toGame();
                    } else if (toolsBean.getToolName().equals(FILE_TRANSFER)) {
                        toFileTransfer();
                    } else if (toolsBean.getToolName().equals(DEVELOP_TEST)) {
                        toDevelopTest();
                    } else if (toolsBean.getToolName().equals(GD_MAP)) {
                        toGDMap();
                    } else if (toolsBean.getToolName().equals(SUDOKE)) {
                        toSudoke();
                    }
                }
            });

            mToolsContainer.addView(itemView);

        }

    }

    @Override
    protected void initView(View view) {

        mToolsContainer = view.findViewById(R.id.tools_container);

        ((Switch)view.findViewById(R.id.tools_anim_switch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAnimating = isChecked;
                if (isChecked) {
                    startAnim();
                }else {
                    endAnim();
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        endAnim();
    }

    @Override
    protected void onNormalResume() {
        super.onNormalResume();
        startAnim();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            startAnim();
        }else {
            endAnim();
        }
    }

    private void startAnim() {
        if (isAnimating) {
            if (mToolsContainer != null) {
                mToolsContainer.startAnim();
            }
        }
    }

    private void endAnim() {
        if (mToolsContainer != null) {
            mToolsContainer.endAnim();
        }
    }

    private void toGame() {
        startActivity(new Intent(getContext(), GamesActivity.class));
    }

    private void toFileTransfer() {
        startActivity(new Intent(getContext(), FileTransferActivity.class));
    }

    private void toDevelopTest() {
        startActivity(new Intent(getContext(), MyTestActivity.class));
    }

    private void toGDMap() {
        Intent intent = new Intent(getContext(), GDMapActivity.class);
        startActivity(intent);
    }

    private void toSudoke() {
        Intent intent = new Intent(getContext(), SudokuActivity.class);
        startActivity(intent);
    }

}
