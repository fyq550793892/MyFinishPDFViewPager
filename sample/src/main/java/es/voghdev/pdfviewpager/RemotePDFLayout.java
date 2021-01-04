package es.voghdev.pdfviewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

/**
 * @Author: yuqingfan
 * @Description:
 * @date: 2020/12/25
 */
public class RemotePDFLayout extends FrameLayout implements DownloadFile.Listener{
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

    private RemotePDFViewPager remotePDFViewPager;
    private PDFPagerAdapter adapter;

    public RemotePDFLayout(Context context) {
        this(context, null);
    }

    public RemotePDFLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RemotePDFLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                if (remotePDFViewPager != null) {
                    int currPageNum = remotePDFViewPager.getCurrentItem() - 1;
                    Log.d(TAG, "onClick将一页: " + currPageNum);
                    if (currPageNum >= 0) {
                        remotePDFViewPager.setCurrentItem(currPageNum, false);
                    }
                }


            }
        });

        nextPage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (remotePDFViewPager != null) {
                    int currPageNum = remotePDFViewPager.getCurrentItem() + 1;
                    if (adapter != null) {
                        if (currPageNum <= adapter.getCount()) {
                            remotePDFViewPager.setCurrentItem(currPageNum, false);
                        }
                    }
                }

            }
        });
    }


    public void loadPDF(String fileRemoteUrl, int pageNum) {
        /**
         * @description 加载PDF
         * @param [fileRemoteUrl, pageNum] 这里传的为远程地址    要跳转到的页数
         * @return void
         * @author yuqingfan
         * @time 2020/12/25 16:19
         */

        jumpNum = pageNum - 1;

        final Context ctx = mContext;
        final DownloadFile.Listener listener = this;
        remotePDFViewPager = new RemotePDFViewPager(ctx, fileRemoteUrl, listener);

        remotePDFViewPager.setId(R.id.pdfViewPager);
        remotePDFViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int position) {
                if (remotePDFViewPager != null && adapter != null) {
                    showPageTv.setText(remotePDFViewPager.getCurrentItem() + 1 + "/" + adapter.getCount());
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
        if (remotePDFViewPager != null) {
            remotePDFViewPager.setCurrentItem(jumpNum, false);
        }

    }

    //获取当前页
    public int getCurrentNumPage() {
        if (remotePDFViewPager != null) {
            return remotePDFViewPager.getCurrentItem() + 1;
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
            adapter = new PDFPagerAdapter(mContext, FileUtil.extractFileNameFromURL(myFileUrl));
            remotePDFViewPager.setAdapter(adapter);
            remotePDFViewPager.setCurrentItem(jumpPage - 1);
            if (remotePDFViewPager != null && adapter != null) {
                showPageTv.setText(remotePDFViewPager.getCurrentItem() + 1 + "/" + adapter.getCount());
                Log.d(TAG, "onSuccess:页数为： " + remotePDFViewPager.getCurrentItem() + "总页数" + adapter.getCount());
            }
        }
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        myFileUrl = url;
        adapter = new PDFPagerAdapter(mContext, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(adapter);
        if (jumpNum > adapter.getCount()) {
            jumpNum = adapter.getCount();
        }
        if (jumpNum < 0) {
            jumpNum = 0;
        }
        remotePDFViewPager.setCurrentItem(jumpNum);
        if (remotePDFViewPager != null && adapter != null) {
            showPageTv.setText(remotePDFViewPager.getCurrentItem() + 1 + "/" + adapter.getCount());
            Log.d(TAG, "onSuccess:页数为： " + remotePDFViewPager.getCurrentItem() + "总页数" + adapter.getCount());
        }
        updateLayout();
    }


    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onProgressUpdate(int progress, int total) {

    }

    private void updateLayout() {
        frameLayout.addView(remotePDFViewPager,
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
