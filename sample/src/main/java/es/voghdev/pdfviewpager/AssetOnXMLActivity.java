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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.BasePDFPagerAdapter;

public class AssetOnXMLActivity extends BaseSampleActivity {
    RemotePDFLayout remotePDFLayout;
    Button btChange;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.asset_on_xml);
        setContentView(R.layout.activity_asset_on_xml);
        btChange = findViewById(R.id.btChange);
        //pdfViewPager = findViewById(R.id.pdfViewPager);
        remotePDFLayout = findViewById(R.id.remotePDFLayout);
        remotePDFLayout.loadPDF("https://lec-t-bh.oss-cn-beijing.aliyuncs.com//2020/11/26/doc/db2f387c-1e06-4ed0-9676-62e0a7388d91_trans.pdf",3);

        btChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) remotePDFLayout.getLayoutParams();
                linearParams.height = 400;
                linearParams.width = 500;
                remotePDFLayout.setLayoutParams(linearParams);
                remotePDFLayout.jumpNumPage(remotePDFLayout.getCurrentNumPage() + 1);
                //remotePDFLayout.jumpNumPage(1);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remotePDFLayout.release();
        //((BasePDFPagerAdapter) pdfViewPager.getAdapter()).close();
    }
}