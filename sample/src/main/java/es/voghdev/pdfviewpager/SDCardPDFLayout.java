package es.voghdev.pdfviewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.io.File;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

/**
 * @Author: yuqingfan
 * @Description:
 * @date: 2021/1/12
 */
public class SDCardPDFLayout extends FrameLayout{
    private static final String TAG = "RemoteAndLocalPDFLayout";
    private Context mContext;
    private View contentView;
    private RelativeLayout relativeLayout;
    private FrameLayout frameLayout;
    private TextView showPageTv; // 显示到第几页view
    private ImageView previousPage; // 上页view
    private ImageView nextPage; // 下页view
    private String myFileUrl;//文件URL

    private int jumpNum = 0;//这里只有跳转时才用

    private PDFViewPager pdfViewPager;
    private PDFPagerAdapter adapter;

    public SDCardPDFLayout(Context context) {
        this(context, null);
    }

    public SDCardPDFLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SDCardPDFLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        mContext = context;
        contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pdflayout, this);
        frameLayout = contentView.findViewById(R.id.frameLayout);
        relativeLayout = contentView.findViewById(R.id.relativeLayout);
        previousPage = contentView.findViewById(R.id.previousPage);
        nextPage = contentView.findViewById(R.id.nextPage);
        showPageTv = contentView.findViewById(R.id.showPageTv);
        jumpNum = 0;

        previousPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (pdfViewPager != null) {
                    int currPageNum = pdfViewPager.getCurrentItem() - 1;
                    Log.d(TAG, "onClick将一页: " + currPageNum);
                    if (currPageNum >= 0) {
                        pdfViewPager.setCurrentItem(currPageNum, false);
                    }
                }


            }
        });

        nextPage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (pdfViewPager != null) {
                    int currPageNum = pdfViewPager.getCurrentItem() + 1;
                    if (adapter != null) {
                        if (currPageNum <= adapter.getCount()) {
                            pdfViewPager.setCurrentItem(currPageNum, false);
                        }
                    }
                }

            }
        });
    }


    public void loadPDF(String fileUrl, int pageNum) {
        /**
         * @description 加载PDF
         * @param [fileRemoteUrl, pageNum] 地址    要跳转到的页数
         * @return void
         * @author yuqingfan
         * @time 2020/12/25 16:19
         */

        jumpNum = pageNum - 1;

        final Context ctx = mContext;
        pdfViewPager = new PDFViewPager(ctx, fileUrl);

        myFileUrl = fileUrl;
        adapter = new PDFPagerAdapter(mContext, fileUrl);

        pdfViewPager.setAdapter(adapter);
        if (jumpNum > adapter.getCount()) {
            jumpNum = adapter.getCount();
        }
        if (jumpNum < 0) {
            jumpNum = 0;
        }
        pdfViewPager.setCurrentItem(jumpNum);
        if (pdfViewPager != null && adapter != null) {
            showPageTv.setText(pdfViewPager.getCurrentItem() + 1 + "/" + adapter.getCount());
            Log.d(TAG, "onSuccess:页数为： " + pdfViewPager.getCurrentItem() + "总页数" + adapter.getCount());
        }
        updateLayout();

        //sdCardPDFLayout.setId(R.id.pdfViewPager);
        pdfViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int position) {
                if (pdfViewPager != null && adapter != null) {
                    showPageTv.setText(pdfViewPager.getCurrentItem() + 1 + "/" + adapter.getCount());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    // 跳转到指定页数
    public void jumpNumPage(int num) {
        jumpNum = num - 1;
        if (jumpNum < 0) {
            jumpNum = 0;
        }
        if (pdfViewPager != null) {
            pdfViewPager.setCurrentItem(jumpNum, false);
        }

    }

    //获取当前页
    public int getCurrentNumPage() {
        if (pdfViewPager != null) {
            return pdfViewPager.getCurrentItem() + 1;
        }else {
            return 0;
        }

    }

    //获取总页数
    public int getSumNumPage() {
        return adapter == null ? 0 : adapter.getCount();
    }


    public void refreshCurrentViewPager() {
        /**
         * @description 刷新当前页===用作View大小改变时调用
         * @param
         * @return
         * @author yuqingfan
         * @time 2021/1/4 17:40
         */
        if (myFileUrl != null) {
            int jumpPage = getCurrentNumPage();
            adapter = new PDFPagerAdapter(mContext, myFileUrl);
            pdfViewPager.setAdapter(adapter);
            pdfViewPager.setCurrentItem(jumpPage - 1);
            if (pdfViewPager != null && adapter != null) {
                showPageTv.setText(pdfViewPager.getCurrentItem() + 1 + "/" + adapter.getCount());
                Log.d(TAG, "onSuccess:页数为： " + pdfViewPager.getCurrentItem() + "总页数" + adapter.getCount());
            }
        }
    }

    
    private void updateLayout() {
        frameLayout.addView(pdfViewPager,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    // 释放pdf控件
    public void release() {
        if (adapter != null) {
            adapter.close();
        }
        jumpNum = 0;
        removeAllViews();
    }
}
