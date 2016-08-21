package com.skymxc.demo.a2048;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

/**
 * Created by sky-mxc
 * Date : 2016/8/18
 */
public class Block {

    //所在行列
    private int[] position ;

    //显示的数字
    private int num ;

    //显示的控件
    private TextView tv ;

    private Block(){}

    public Block(TextView tv, int row, int column, int num) {
        this.position= new int []{row,column};
        this.tv=tv;
        setNum(num);


    }


    public int[] getPosition() {
        return position;
    }

    public TextView getTv() {
        return tv;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
        if (num!=0){
            tv.setText(String.valueOf(num));
        }else{
            tv.setText("");
        }
        int color ;
        //根据数字设置背景颜色
        switch (num){
            case 0:
                color= R.color.bg_0;
                break;

            case 2:
                color= R.color.bg_2;
                break;

            case 4:
                color= R.color.bg_4;
                break;

            case 8:
                color= R.color.bg_8;
                break;

            case 16:
                color= R.color.bg_16;
                break;

            case 32:
                color= R.color.bg_32;
                break;

            case 64:
                color= R.color.bg_64;
                break;

            case 128:
                color= R.color.bg_128;
                break;

            case 256:
                color= R.color.bg_256;
                break;

            case 512:
                color= R.color.bg_512;
                break;

            case 1024:
                color= R.color.bg_1024;
                break;

            case 2048:
                color= R.color.bg_2048;
                break;

            case 4096:
                color= R.color.bg_4096;
                break;

            case 8192:
                color= R.color.bg_8192;
                break;

            case 16384:
                color= R.color.bg_16384;
                break;

            default:
                color= R.color.bg_de9fault;
                break;
        }
        tv.setBackgroundResource(color);
    }


    public static class Builder {

        private Context context ;
        private int row;
        private int column;
        private int num;
        private int size=50;


        public Builder(Context context ){
            this.context=context;
        }


        /**
         * 设置所在的行和列
         * @param row  行
         * @param column 列
         * @return
         */
        public Builder setPosition(int row,int column){
            this.row=row;
            this.column=column;
            return this;
        }

        /**
         * 设置显示的数
         * @param num 要显示的数
         * @return
         */
        public Builder setNum (int num){
            this.num=num;
            return this;
        }

        /**
         * 设置尺寸
         * @param size 尺寸
         * @return 当前
         */
        public Builder setSize (int size){
            this.size=size;
            return this;
        }


        public Block build(){
            TextView tv = new TextView(context);

            tv.setText("");
            tv.setGravity(Gravity.CENTER);
          //  tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
            tv.setBackgroundColor(Color.WHITE );
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,size/4);
            //创建子控件和父容器显示关系的属性的集合  （布局参数对象）
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            //设置宽高
            lp.width = size-2;
            lp.height = size-2;
            //设置外边距
            lp.bottomMargin = 1;
            lp.leftMargin = 1;
            lp.rightMargin = 1;
            lp.topMargin = 1;

            //所在的行和列
            lp.rowSpec = GridLayout.spec(row);
            lp.columnSpec = GridLayout.spec(column);
            tv.setLayoutParams(lp);
            return new Block(tv,row,column,num);
        }
    }
}
