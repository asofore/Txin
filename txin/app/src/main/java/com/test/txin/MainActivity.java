package com.test.txin;

//package com.example.chatApp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private EditText editTextMessage;
    private Button buttonSend;
    private LinearLayout messagesContainer;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        messagesContainer = findViewById(R.id.messagesContainer);
        scrollView = findViewById(R.id.scrollView);

        // التمرير لأسفل القائمة تلقائياً عند التركيز على حقل الإدخال وظهور الكيبورد
        editTextMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollToBottom();
                }
            }
        });

        // إضافة رسالة تجريبية عند الضغط على زر الإرسال
        buttonSend.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String text = editTextMessage.getText().toString().trim();
        if (!text.isEmpty()) {

            // 1. إنشاء وعرض رسالة المستخدم (المرسلة)
            ExpandableMessageBox messageBox = new ExpandableMessageBox(MainActivity.this);
            messageBox.setMessageText(text);
            // messageBox.setSenderName("Ali Mohamed");
            
            // إضافة رسالة المستخدم إلى الحاوية
            messagesContainer.addView(messageBox);
            
            // تفريغ حقل النص والنزول للأسفل
            editTextMessage.setText("");
            scrollToBottom();

            // 2. إنشاء صندوق رد البوت (Gemini) وإضافته بصورة مؤقتة (جاري التحميل...)
            ExpandableMessageBoxLeft messageBoxLeft = new ExpandableMessageBoxLeft(MainActivity.this);
            messageBoxLeft.setMessageText("جاري التفكير...");
            messageBoxLeft.setSenderName("Gemini");
            messagesContainer.addView(messageBoxLeft);
            scrollToBottom();

            // 3. إرسال النص إلى Gemini في الخلفية
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // ضع مفتاح الـ API الخاص بك هنا
                    String apiKey = "your api key"; 
                    GeminiClient client = new GeminiClient(apiKey);

                    // الحصول على النتيجة
                    String response = client.generateText(text);

                    // 4. تحديث واجهة المستخدم بالرد القادم من Gemini
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageBoxLeft.setMessageText(response);
                            scrollToBottom();
                        }
                    });
                }
            }).start();
        }
    }
});

    }

    private void scrollToBottom() {
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100);
    }
}
