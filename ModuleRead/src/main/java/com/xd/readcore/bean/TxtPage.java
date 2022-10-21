package com.xd.readcore.bean;


import com.xd.base.ad.YYFrame;
import com.xd.base.utils.NStringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by newbiechen on 17-7-1.
 */

public class TxtPage implements Serializable {
    public int position;
    public String title;
    public int titleLines; //当前 lines 中为 title 的行数。
    public List<String> lines;
    public List<LineInfo> lineInfos;
    private List<LineInfo> firstLines;

    private Map<Integer, ParaInPageBean> paraList;

    private int speechingPara = -1;

    private int paraCount = -1;

    private LineInfo mAdLine = null;

    private int adHashCode = 0;

    public TxtPage() {
        lineInfos = new ArrayList<>();
        lines = new ArrayList<>();
    }

    //生成广告相关信息
    public LineInfo findAdLine() {
        if (mAdLine != null) {
            return mAdLine;
        }
        if (lineInfos != null && lineInfos.size() > 0) {
            for (LineInfo line : lineInfos) {
                if (line.getmLineType() == LineInfo.LineType.LineTypeAdView
                        || line.getmLineType() == LineInfo.LineType.LineTypeTailAd
                        || line.getmLineType() == LineInfo.LineType.LineTypeFullPageAd) {
                    mAdLine = line;
                    return line;
                }
            }
        }
        return null;
    }

    public boolean bHaveAd() {
        return findAdLine() != null;
    }

    public LineInfo.LineAdType getAdType() {
        LineInfo adLine = findAdLine();
        if (adLine != null) {
            return adLine.getLineAdType();
        }
        return LineInfo.LineAdType.LineAdTypeNone;
    }


    public YYFrame getAdFrame() {
        LineInfo adLine = findAdLine();
        if (adLine != null) {
            return adLine.getmAdView().getAdFrame();
        }
        return YYFrame.YYFrameZero();
    }

    public int pageCharCount() {
        int wordCount = 0;
        if (lineInfos == null) {
            return 0;
        }
        for (LineInfo line : lineInfos) {
            wordCount += line.getmCharCount();
        }
        return wordCount;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<LineInfo> getStringList() {
        return lineInfos;
    }

    public int getAdHashCode() {
        return adHashCode;
    }

    public void setAdHashCode(int adHashCode) {
        this.adHashCode = adHashCode;
    }

    public String getMarkDesc() {
        if (lineInfos.isEmpty()) {
            return "";
        }

        String desc = lineInfos.get(0).getmLineText();
        if (lineInfos.size() >= 2) {
            desc = desc + lineInfos.get(1).getmLineText();
        }
        return desc;
    }

    public int getTitleLines() {
        return titleLines;
    }

    public void setSpeechingPara(int paraIndex) {
        speechingPara = paraIndex;
    }

    public int getSpeechingPara() {
        return speechingPara;
    }

    public String getHighLightContent() {
        if (speechingPara == -1) {
            return null;
        }
        ParaInPageBean paraInPageBean = getPara(speechingPara);
        String highlightContent = paraInPageBean.getTextContent();
        if (NStringUtils.isBlank(highlightContent)) {
            List<LineInfo> paraLines = getParaLines(speechingPara);
            for (int i = 0; i < paraLines.size(); i++) {
                highlightContent = highlightContent + paraLines.get(i).getmLineText();
            }
        }
        return highlightContent;
    }

    public void setSpeechingParaToLastPara() {
        speechingPara = paraCount;
    }

    public void setSpeechingParaToFirstPara() {
        speechingPara = 0;
    }

    public int getPageParaCount() {
        if (paraList == null) {
            return 0;
        }
        return paraList.size();
    }

    public void addLineToLastPara(LineInfo lineInfo,boolean isFirstLineInPage) {

        if (paraList == null) {
            paraList = new HashMap<>();
            paraCount = -1;
        }


        if (lineInfo.getmLineType() == LineInfo.LineType.LineTypeFirstLine || isFirstLineInPage) {
            paraCount++;
            ParaInPageBean paraInPageBean = new ParaInPageBean();
            paraInPageBean.setParaIndex(paraCount);
            paraInPageBean.setLineCount(1);
            String stringContent = paraInPageBean.getTextContent();
            paraInPageBean.setTextContent(stringContent+lineInfo.getmLineText());
            paraInPageBean.setStartCharPos(lineInfo.getmStartPos());
            paraInPageBean.setEndCharPos(lineInfo.getmStartPos()+lineInfo.getmCharCount());
            paraList.put(paraCount, paraInPageBean);
        } else {
            if (paraList.get(paraCount)!=null) {
                String stringContent = paraList.get(paraCount).getTextContent();
                paraList.get(paraCount).setTextContent(stringContent + lineInfo.getmLineText());
                paraList.get(paraCount).addLineCount();
            }else {
                String stringContent = "";
                if (paraCount >= 0 && paraCount < paraList.size()) {
                    paraList.get(paraCount).setTextContent(stringContent + lineInfo.getmLineText());
                    paraList.get(paraCount).addLineCount();
                    paraList.get(paraCount).setStartCharPos(lineInfo.getmStartPos());
                    paraList.get(paraCount).setEndCharPos(lineInfo.getmStartPos()+lineInfo.getmCharCount());
                }
            }
        }
    }

    public ParaInPageBean getPara(int paraIndex) {
        if (paraList == null) {
            paraList = new HashMap<>();
        }
        if (paraList.size() <= paraIndex) {
            ParaInPageBean paraInPageBean = new ParaInPageBean();
            paraInPageBean.setParaIndex(paraIndex);
            paraList.put(paraIndex,paraInPageBean);
        }
        return paraList.get(paraIndex);
    }

    public List<LineInfo> getParaLines(int paraIndex) {
        List<LineInfo> paraLines = new ArrayList<>();
        if (lineInfos == null) {
            return paraLines;
        }
        for (int i = 0; i < lineInfos.size(); i++) {
            if (lineInfos.get(i).getmParaIndex() == paraIndex) {
                paraLines.add(lineInfos.get(i));
            }
        }
        return paraLines;
    }

    public float getCurrentParaStartY(int paraIndex) {
        float currentParaStartY = 0;
        if (speechingPara!=-1&&paraList.get(speechingPara)!=null) {
            currentParaStartY = paraList.get(speechingPara).getStartY();
        }
        return currentParaStartY;

    }

}
