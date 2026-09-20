package com.test.txin;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpandableMessageBoxLeft extends LinearLayout {
	
	private TextView tvSenderName; // عنصر اسم المرسل
	private TextView tvMessage;
	private TextView btnToggle;
	private boolean isExpanded = false;
	private final int MAX_COLLAPSED_LINES = 8; // عدد الأسطر عند الطي
	
	public ExpandableMessageBoxLeft(Context context) {
		super(context);
		init(context);
	}
	
	public ExpandableMessageBoxLeft(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public ExpandableMessageBoxLeft(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	private void init(Context context) {
		// 1. إعداد الحاوية الرئيسية (أن تكون على اليسار)
		setOrientation(LinearLayout.VERTICAL);
		
		// المحاذاة لليسار
		LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
		LayoutParams.WRAP_CONTENT,
		LayoutParams.WRAP_CONTENT
		);
		containerParams.gravity = Gravity.START; // محاذاة لليسار في الشاشة
		containerParams.setMargins(16, 12, 16, 12);
		setLayoutParams(containerParams);
		
		// 2. إنشاء خلفية رمادية احترافية للرسالة المستقبلة
		GradientDrawable background = new GradientDrawable();
		background.setColor(Color.parseColor("#F0F2F5")); // لون رمادي ناعم وأنيق (Modern Chat Bubble)
		background.setCornerRadii(new float[]{
			32f, 32f, // أعلى اليسار
			32f, 32f, // أعلى اليمين
			32f,  32f,   // أسفل اليسار (حافة قصيرة لتعطي شكل ذيل الفقاعة المستقبلة)
			8f, 8f  // أسفل اليمين
		});
		setBackground(background);
		setPadding(28, 20, 28, 16);
		
		// 3. إنشاء TextView لاسم المرسل
		tvSenderName = new TextView(context);
		tvSenderName.setTextColor(Color.parseColor("#0084FF")); // لون أزرق مميز يناسب الخلفية الرمادية
		tvSenderName.setTextSize(13f);
		tvSenderName.setTypeface(null, Typeface.BOLD); // خط عريض
		tvSenderName.setPadding(0, 0, 0, 8); // مسافة سفلية قبل نص الرسالة
		tvSenderName.setVisibility(View.GONE); // مخفي افتراضياً حتى يتم إدخال اسم
		
		// 4. إنشاء TextView للرسالة
		tvMessage = new TextView(context);
		tvMessage.setTextColor(Color.parseColor("#1C1E21")); // نص داكن وواضح جداً
		tvMessage.setTextSize(15f);
		tvMessage.setLineSpacing(6f, 1.1f);
		
		// 5. إنشاء زر التكبير / التصغير
		btnToggle = new TextView(context);
		btnToggle.setTextColor(Color.parseColor("#0084FF")); // أزرق مميز وأنيق للزر
		btnToggle.setTextSize(13f);
		btnToggle.setPadding(0, 10, 0, 0);
		btnToggle.setGravity(Gravity.START);
		
		// إضافة العناصر للحاوية بالترتيب
		addView(tvSenderName);
		addView(tvMessage);
		addView(btnToggle);
		
		// 6. التفاعل عند الضغط على الزر أو مربع الرسالة
		OnClickListener toggleListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isExpanded) {
					collapse();
					} else {
					expand();
				}
			}
		};
		
		btnToggle.setOnClickListener(toggleListener);
		this.setOnClickListener(toggleListener);
		
		collapse();
	}
	
	// تعيين اسم المرسل
	public void setSenderName(String name) {
		if (!TextUtils.isEmpty(name)) {
			tvSenderName.setText(name);
			tvSenderName.setVisibility(View.VISIBLE);
			} else {
			tvSenderName.setVisibility(View.GONE);
		}
	}
	
	// تعيين نص الرسالة والتحكم بظهور زر التكبير
	public void setMessageText(String text) {
		tvMessage.setText(text);
		
		// التحقق بعد رسم النص إذا كان يتجاوز 8 أسطر لإظهار زر التكبير
		tvMessage.post(new Runnable() {
			@Override
			public void run() {
				if (tvMessage.getLineCount() > MAX_COLLAPSED_LINES) {
					btnToggle.setVisibility(View.VISIBLE);
					} else {
					btnToggle.setVisibility(View.GONE);
				}
			}
		});
	}
	
	// تعيين الاسم والرسالة معاً بأسلوب مباشر
	public void setMessage(String senderName, String text) {
		setSenderName(senderName);
		setMessageText(text);
	}
	
	// توسيع الرسالة
	private void expand() {
		tvMessage.setMaxLines(Integer.MAX_VALUE);
		tvMessage.setEllipsize(null);
		btnToggle.setText("▲ عرض أقل");
		isExpanded = true;
	}
	
	// طي الرسالة لـ 8 أسطر
	private void collapse() {
		tvMessage.setMaxLines(MAX_COLLAPSED_LINES);
		tvMessage.setEllipsize(TextUtils.TruncateAt.END);
		btnToggle.setText("▼ عرض المزيد");
		isExpanded = false;
	}
}