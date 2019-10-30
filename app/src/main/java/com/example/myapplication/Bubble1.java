package com.example.myapplication;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xujiaji.happybubble.BubbleDialog;

import org.w3c.dom.Text;


public class Bubble1 extends BubbleDialog implements View.OnClickListener {
    private ViewHolder mViewHolder;
    private OnClickCustomButtonListener mListener;


    public Bubble1(Context context) {
        super(context);
        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_bubble1, null);
        mViewHolder = new ViewHolder(rootView);
        addContentView(rootView);
        mViewHolder.btn8.setOnClickListener(this);
        mViewHolder.btn9.setOnClickListener(this);
        mViewHolder.btn10.setOnClickListener(this);

    }

    public void setNameText(String s) {
        TextView tv = findViewById(R.id.textView11);
        tv.setText(s);
    }

    public void setPhoneText(String s) {
        TextView tv = findViewById(R.id.textView12);
        tv.setText(s);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null)
        {
            mListener.onClick(((Button)v).getText().toString());
        }
    }

    private static class ViewHolder
    {
        Button btn8,btn9,btn10;
        public ViewHolder(View rootView)
        {
            btn8 = rootView.findViewById(R.id.button8);
            btn9 = rootView.findViewById(R.id.button9);
            btn10 = rootView.findViewById(R.id.button10);
        }
    }

    public void setClickListener(OnClickCustomButtonListener l)
    {
        this.mListener = l;
    }

    public interface OnClickCustomButtonListener
    {
        void onClick(String str);
    }


}
