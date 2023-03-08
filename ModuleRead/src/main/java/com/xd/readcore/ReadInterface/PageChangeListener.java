package com.pw.readcore.ReadInterface;

import com.pw.readcore.bean.TxtChapter;

import java.util.List;

public interface PageChangeListener{

        /**
         * 作用：章节切换的时候进行回调
         *
         * @param pos:切换章节的序号
         */
        void onChapterChange(int pos,int wordcount);

        /**
         * 作用：阅读到下一章的时候进行回调
         *
         * @param pos:上一章章节的序号
         */
        void onChapterChangeByRead(int pos,int wordcount,String chapterId,boolean isNext);

        /**
         * 作用：请求加载章节内容
         *
         * @param requestChapters:需要下载的章节列表
         */
        void requestChapters(List<? extends TxtChapter> requestChapters);

        /**
         * 作用：章节目录加载完成时候回调
         *
         * @param chapters：返回章节目录
         */
        void onCategoryFinish(List<? extends TxtChapter> chapters);

        /**
         * 作用：章节页码数量改变之后的回调。==> 字体大小的调整，或者是否关闭虚拟按钮功能都会改变页面的数量。
         *
         * @param count:页面的数量
         */
        void onPageCountChange(int count);

        /**
         * 作用：当页面改变的时候回调
         *
         * @param pos:当前的页面的序号
         */
        void onPageChange(int pos,int wordcount,int chapterPos,int pageWordCount);

        void onPageChangeFinish(int pos,boolean success);

        void onReadParaChanged(int paraIndex);

        void onReadFinish();
}
