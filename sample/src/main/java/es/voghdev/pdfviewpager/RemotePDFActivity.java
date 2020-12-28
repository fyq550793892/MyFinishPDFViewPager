/*
 * Copyright (C) 2016 Olmo Gallegos Hernández.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.voghdev.pdfviewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import java.io.File;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.asset.CopyAsset;
import es.voghdev.pdfviewpager.library.asset.CopyAssetThreadImpl;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class RemotePDFActivity extends BaseSampleActivity implements DownloadFile.Listener {
    private static final String TAG = "RemotePDFActivity";
    LinearLayout root;
    RemotePDFViewPager remotePDFViewPager;
    EditText etPdfUrl;
    Button btnDownload;
    PDFPagerAdapter adapter;

    EditText etPageNum;
    Button btJumpToPage;

    Button btLeftPage;
    Button btRightPage;
    TextView tvPageShow;
    Button btChange;
    int pageNum;
    private File pdfFolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.remote_pdf_example);
        setContentView(R.layout.activity_remote_pdf);
        pageNum = 0;

        root = findViewById(R.id.remote_pdf_root);
        etPdfUrl = findViewById(R.id.et_pdfUrl);
        btnDownload = findViewById(R.id.btn_download);
        etPageNum = findViewById(R.id.etPageNum);
        btJumpToPage = findViewById(R.id.btJumpToPage);
        btLeftPage = findViewById(R.id.btLeftPage);
        btRightPage = findViewById(R.id.btRightPage);
        tvPageShow = findViewById(R.id.tvPageShow);
        btChange = findViewById(R.id.btChange);
        //remotePDFViewPager = findViewById(R.id.remotePDFViewPager);
        btJumpToPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remotePDFViewPager != null) {
                    remotePDFViewPager.setCurrentItem(Integer.parseInt(etPageNum.getText().toString()) - 1);
                }
            }
        });

        btLeftPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNum--;
                if (pageNum >= 0) {
                    if (remotePDFViewPager != null) {
                        remotePDFViewPager.setCurrentItem(pageNum, false);
                    }
                } else {
                    pageNum = 0;
                }

            }
        });

        btRightPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNum++;
                if (adapter != null) {
                    if (pageNum <= adapter.getCount()) {
                        if (remotePDFViewPager != null) {
                            remotePDFViewPager.setCurrentItem(pageNum, false);
                        }
                    } else {
                        pageNum = adapter.getCount();
                    }
                }

            }
        });

        btChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLayout(300, 400);
            }
        });

        setDownloadButtonListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (adapter != null) {
            adapter.close();
        }
    }

    protected void setDownloadButtonListener() {
        final Context ctx = this;
        final DownloadFile.Listener listener = this;
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remotePDFViewPager = new RemotePDFViewPager(ctx, getUrlFromEditText(), listener);
                remotePDFViewPager.setId(R.id.pdfViewPager);
                remotePDFViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPageSelected(int position) {
                        if (remotePDFViewPager != null && adapter != null) {
                            tvPageShow.setText(remotePDFViewPager.getCurrentItem() + 1 + "/" + adapter.getCount());
                            Log.d(TAG, "onSuccess:页数为： " + remotePDFViewPager.getCurrentItem() + "这是什么" + adapter.getCount());
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                hideDownloadButton();
            }
        });
    }

    protected String getUrlFromEditText() {
        return etPdfUrl.getText().toString().trim();
    }

    public void showDownloadButton() {
        btnDownload.setVisibility(View.VISIBLE);
    }

    public void hideDownloadButton() {
        btnDownload.setVisibility(View.INVISIBLE);
    }

    public void updateLayout(int x, int y) {
        root.removeView(remotePDFViewPager);
        root.addView(remotePDFViewPager, x, y);
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(adapter);
        //remotePDFViewPager.onInterceptTouchEvent()
        if (remotePDFViewPager != null && adapter != null) {
            tvPageShow.setText(remotePDFViewPager.getCurrentItem() + 1 + "/" + adapter.getCount());
            Log.d(TAG, "onSuccess:页数为： " + remotePDFViewPager.getCurrentItem() + "这是什么" + adapter.getCount());
        }

        root.addView(remotePDFViewPager, -2, -2);
        showDownloadButton();
    }


    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();
        showDownloadButton();
    }

    @Override
    public void onProgressUpdate(int progress, int total) {

    }

}

