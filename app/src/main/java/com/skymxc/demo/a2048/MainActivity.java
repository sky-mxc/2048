package com.skymxc.demo.a2048;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //最大数
    private TextView tvMax = null;

    //步数
    private TextView tvStep = null;

    //重新开始
    private Button  btResetart = null;

    //列布局
    private Spinner spColunm = null;

    //网格布局
    private GridLayout glBrood = null;

    //默认的网格列数
    private  int colume = 4;

    //添加块的二维数组
    private Block[][] blocks  =null;

    //存储空白格
    private List<int[]> emptyBlocks;

    //最大值
    private int max ;

    //步数
    private int step ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initGame();


        btResetart.setOnClickListener(chlickLis);

        //布局改变事件
        spColunm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        colume=4;
                        break;
                    case 1:
                        colume=5;
                        break;
                    case 2:
                        colume=6;
                        break;
                }

                initGame();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //重新开始点击事件
   private  View.OnClickListener chlickLis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                max=0;
                step=0;

            recycle(new Recycler() {
                @Override
                public boolean onRecycler(int i, int j) {
                    blocks[i][j].setNum(0);
                    return false;
                }
            },false);

            addNum(2);
            updateShow();
        }

    };

    /**
     * 初始化游戏
     */
    private void initGame(){

        //移除所有子视图
        glBrood.removeAllViews();

        //设置列数  gridView
        glBrood.setColumnCount(colume);

        //初始化空白格集合
        emptyBlocks= new LinkedList<>();

        //屏幕宽度  DisplayMetrics 屏幕的物理信息 包含屏幕宽度的像素值
//        int w = getResources().getDisplayMetrics().widthPixels-
//                 2*getResources().getDisplayMetrics().density*10;

        // getDimensionPixelSize  获取像素尺寸 因为我们用的是dp 需要换算  将dp换算为px
        final int w = getResources().getDisplayMetrics().widthPixels-4*getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);


        blocks = new Block[colume][colume];

        recycle(new Recycler() {
            @Override
            public boolean onRecycler(int i, int j) {
                blocks[i][j] = new Block.Builder(MainActivity.this)
                        .setNum(0)
                        .setPosition(i,j)
                        .setSize(w /colume)
                        .build();
                //添加子控件
                glBrood.addView(blocks[i][j].getTv());
                return false;
            }

        },false);

        addNum(2);
        updateShow();
    }

    /**
     * 添加数字
     * @param count 几个
     */
    private void addNum(int count) {
        //检查空白格
        checkEmptyBlocks();

        //按照需求的个数添加
        for (int i=0;i<count;i++){
            creatNum();
        }
    }

    private void creatNum() {
        if (emptyBlocks!=null&&emptyBlocks.size()>0){
          int index= (int) (Math.random()*emptyBlocks.size());
            //得到随机块
            int[] position = emptyBlocks.get(index);
            //随机2或者4
             int num = Math.random()>0.1?2:4;

            if (max< num){
                max=num;
            }
            //设置数字
            blocks[position[0]][position[1]].setNum(num);

            //移除被添加的项
            emptyBlocks.remove(index);
        }
    }

    /**
     * 检查空白格
     */
    private void checkEmptyBlocks() {
        emptyBlocks.clear();
        recycle(new Recycler() {
            @Override
            public boolean onRecycler(int i, int j) {
                //如果 num为0 则为空白
                if (blocks[i][j].getNum()==0){
                    emptyBlocks.add(blocks[i][j].getPosition());
                }
                return false;
            }
        },false);
    }


    /**
     * 初始化控件
     */
    public void initView(){
        tvMax = (TextView) findViewById(R.id.max);
        tvStep = (TextView) findViewById(R.id.step);
        btResetart = (Button) findViewById(R.id.buttonResetStart);
        spColunm = (Spinner) findViewById(R.id.column);
        glBrood  = (GridLayout) findViewById(R.id.grid);


    }

    private boolean recycle(Recycler recycler,boolean needReturn) {
        for (int i= 0;i<colume;i++){
            for (int j=0;j<colume;j++){
                boolean f =  recycler.onRecycler(i,j);
                if (needReturn){
                    return f;
                }

            }
        }
        return false;
    }

    interface  Recycler{
       boolean onRecycler(int i,int j);
//       void onRecycler2(int i,int j);
    }

    //坐标位置
    float downX;
    float downY;

    /**
     * 触屏事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {



        //获取本次事件的坐标
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            //按下
            case MotionEvent.ACTION_DOWN:
                //存储按下时的坐标位置
                downX=x;
                downY=y;
                Log.e("tag","================按下时X坐标："+downX);
                Log.e("tag","================按下时Y坐标："+downY);
                break;
            //抬起
            case MotionEvent.ACTION_UP:

                //判断游戏是否结束
                if (!isGameOver()){
                    Toast.makeText(MainActivity.this,"Game Over ,Reset Please",Toast.LENGTH_LONG).show();
                }else {

                    //获取距离差
                    Log.e("tag", "================抬起时X坐标：" + x);
                    Log.e("tag", "================抬起时Y坐标：" + y);

                    float dx = x - downX;
                    float dy = y - downY;
                    Log.e("tag", "================抬起时X距离差：" + dx);
                    Log.e("tag", "================抬起时Y距离差：" + dy);

                    if (Math.abs(dx)<50 && Math.abs(dy)<50){
                        return false;
                    }
                    //根据方向进行移动  移动成功添加一个新的块
                    if (move(getOrientation(dx,dy))){
                        step++;
                        addNum(1);
                        updateShow();
                    }

                }
                break;
        }



        return super.onTouchEvent(event);
    }

    /**
     * 更新显示
     */
    private void updateShow(){
        tvStep.setText(String.format("%d步",step));
        tvMax.setText(max+"");
    }


    /**
     * 移动 方法
     * @param orientation 移动的方向
     * @return 是否成功
     */
    private boolean move(int orientation) {
        Log.e("tag","移动方向："+(char)orientation);
        switch (orientation){
            case 'l':
               return moveToLeft();
            case 't':
                return moveToTop();
            case 'r':
               return moveToRight();
            case 'b':
                return moveToBottom();
        }

        return false;
    }

    /**
     * 左移  行从0开始  列从0 开始
     * @return 是否成功
     */
    private boolean moveToLeft(){
        boolean flag = false;   //是否移动成功
        List<Integer> nums = new ArrayList<Integer>();      //存放合并后的值
      for (int i=0;i<colume;i++){       //循环行
          //临时存储上一个数
          int lastNum =-1;

          int indexZ = -1 ;  //零的位置
          nums.clear();
          for(int j=0;j<colume;j++){        //循环列
              //拿到值
              int num = blocks[i][j].getNum();

              //判断是否为0  0就是空白的
              if (num!=0){
                  if (indexZ!=-1&&j>indexZ)flag =true;
                  //判断前一个 是否是有值的
                  if (lastNum==-1){
                      //没有值     直接 赋值
                      lastNum = num;
                  }else{//有值
                      //上一个数是否和这一个相等
                      if (lastNum==num){

                          if (max<2*num){  //最大值判断
                              max=2*num;
                          }

                        nums.add(num*2);   //两个相等的相加
                          lastNum=-1;       //相加过后不用比较了
                          flag=true;
                      }else{
                          nums.add(lastNum);  //将上一个数字放
                          lastNum= num;        //当前村委上一个数字 跟下一个比较
                      }

                  }
              }else{    //Num 的数值为 0
                  if (indexZ==-1){   //记录这一行 0 的位置
                      indexZ=j;
                  }
              }
          }

          if (lastNum !=-1){                 //将最后一个未添加到集合中的数字填入
              nums.add(lastNum);
          }

          //已经比较完  循环赋值  将nums 的数据赋值到 新行中
          for (int j=0;j<colume ;j++){

              if(j<nums.size()){
                  blocks[i][j].setNum(nums.get(j));
              }else{
                  blocks[i][j].setNum(0);
              }
          }
      }
        return flag;
    }

    /**
     * 向上滑动
     * @return
     */
    private boolean moveToTop(){
        boolean flag = false;   //是否移动成功
        List<Integer> nums = new ArrayList<Integer>();      //存放合并后的值
        for (int i=0;i<colume;i++){       //循环行
            //临时存储上一个数
            int lastNum =-1;

            int indexZ = -1 ;  //零的位置
            nums.clear();
            for(int j=0;j<colume;j++){        //循环列
                //拿到值
                int num = blocks[j][i].getNum();

                //判断是否为0  0就是空白的
                if (num!=0){
                    if (indexZ!=-1&&j>indexZ)flag =true;
                    //判断前一个 是否是有值的
                    if (lastNum==-1){
                        //没有值     直接 赋值
                        lastNum = num;
                    }else{//有值
                        //上一个数是否和这一个相等
                        if (lastNum==num){
                            if (max<2*num){  //最大值判断
                                max=2*num;
                            }
                            nums.add(num*2);   //两个相等的相加
                            lastNum=-1;       //相加过后不用比较了
                            flag=true;
                        }else{
                            nums.add(lastNum);  //将上一个数字放
                            lastNum= num;        //当前村委上一个数字 跟下一个比较
                        }

                    }
                }else{    //Num 的数值为 0
                    if (indexZ==-1){   //记录这一行 0 的位置
                        indexZ=j;
                    }
                }
            }

            if (lastNum !=-1){                 //将最后一个未添加到集合中的数字填入
                nums.add(lastNum);
            }

            //已经比较完  循环赋值  将nums 的数据赋值到 新行中
            for (int j=0;j<colume ;j++){

                if(j<nums.size()){
                    blocks[j][i].setNum(nums.get(j));
                }else{
                    blocks[j][i].setNum(0);
                }
            }
        }
        return flag;
    }

    private boolean moveToRight(){
        boolean flag = false;   //是否移动成功
        List<Integer> nums = new ArrayList<Integer>();      //存放合并后的值
        for (int i=0;i<colume;i++){       //循环行
            //临时存储上一个数
            int lastNum =-1;

            int indexZ = -1 ;  //零的位置
            nums.clear();
            for(int j=colume-1;j>=0;j--){        //循环列
                //拿到值
                int num = blocks[i][j].getNum();

                //判断是否为0  0就是空白的
                if (num!=0){
                    if (indexZ!=-1&&j<indexZ)flag =true;
                    //判断前一个 是否是有值的
                    if (lastNum==-1){
                        //没有值     直接 赋值
                        lastNum = num;
                    }else{//有值
                        //上一个数是否和这一个相等
                        if (lastNum==num){
                            if (max<2*num){  //最大值判断
                                max=2*num;
                            }
                            nums.add(num*2);   //两个相等的相加
                            lastNum=-1;       //相加过后不用比较了
                            flag=true;
                        }else{
                            nums.add(lastNum);  //将上一个数字放
                            lastNum= num;        //当前村委上一个数字 跟下一个比较
                        }

                    }
                }else{    //Num 的数值为 0
                    if (indexZ==-1){   //记录这一行 0 的位置
                        indexZ=j;
                    }
                }
            }

            if (lastNum !=-1){                 //将最后一个未添加到集合中的数字填入
                nums.add(lastNum);
            }

            //已经比较完  循环赋值  将nums 的数据赋值到 新行中
            for (int j=colume-1;j >=0 ;j--){
                int index =colume -1-j;  //将 J 转换为第一个  倒过来
                if(index<nums.size()){
                    blocks[i][j].setNum(nums.get(index));
                }else{
                    blocks[i][j].setNum(0);
                }
            }
        }
        return flag;
    }
    private boolean moveToBottom(){
        boolean flag = false;   //是否移动成功
        List<Integer> nums = new ArrayList<Integer>();      //存放合并后的值
        for (int i=0;i<colume;i++){       //循环行
            //临时存储上一个数
            int lastNum =-1;

            int indexZ = -1 ;  //零的位置
            nums.clear();
            for(int j=colume-1;j>=0;j--){        //循环列
                //拿到值
                int num = blocks[j][i].getNum();

                //判断是否为0  0就是空白的
                if (num!=0){
                    if (indexZ!=-1&&j<indexZ)flag =true;
                    //判断前一个 是否是有值的
                    if (lastNum==-1){
                        //没有值     直接 赋值
                        lastNum = num;
                    }else{//有值
                        //上一个数是否和这一个相等
                        if (lastNum==num){
                            if (max<2*num){  //最大值判断
                                max=2*num;
                            }
                            nums.add(num*2);   //两个相等的相加
                            lastNum=-1;       //相加过后不用比较了
                            flag=true;
                        }else{
                            nums.add(lastNum);  //将上一个数字放
                            lastNum= num;        //当前村委上一个数字 跟下一个比较
                        }

                    }
                }else{    //Num 的数值为 0
                    if (indexZ==-1){   //记录这一行 0 的位置
                        indexZ=j;
                    }
                }
            }

            if (lastNum !=-1){                 //将最后一个未添加到集合中的数字填入
                nums.add(lastNum);
            }

            //已经比较完  循环赋值  将nums 的数据赋值到 新行中
            for (int j=colume-1;j >=0 ;j--){
                int index =colume -1-j;  //将 J 转换为第一个  倒过来
                if(index<nums.size()){
                    blocks[j][i].setNum(nums.get(index));
                }else{
                    blocks[j][i].setNum(0);
                }
            }
        }
        return flag;
    }

    /**
     * 获取滑动的方向
     *  根据距离差判断 是x还是y方向
     *  如果是x方向 查看距离差是正数还是负数
     *  如果是负数 是 左划  正数是右划 。
     * @param dx x轴距离差
     * @param dy y轴距离差
     * @return
     */
    private int getOrientation(float dx,float dy){

        //绝对值大小
        if (Math.abs(dx)>Math.abs(dy)){
            return dx>0?'r':'l';
        }else{
            return dy>0?'b':'t';
        }
    }

    /**
     * 判断游戏是否结束
     * @return
     */
    private boolean isGameOver(){

        for (int i= 0;i<colume;i++){
            for (int j=0;j<colume;j++){
                int num = blocks[i][j].getNum();
                //如果数字是0就没有结束
                if(num==0)return true;
                //相邻的两个块有相同的值  还可以滑动
                if(j+1<colume&&blocks[i][j+1].getNum()==num) return true;// y轴右边紧邻
                if(i+1<colume&&blocks[i+1][j].getNum()==num) return true;// x轴下边紧邻

            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN||
                keyCode==KeyEvent.KEYCODE_DPAD_UP
                ||keyCode==KeyEvent.KEYCODE_DPAD_LEFT
                ||keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){

            if (!isGameOver()){

            }else{
                boolean f = false;
                switch (keyCode){
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        f= moveToBottom();
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        f= moveToTop();
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        f= moveToLeft();
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        f= moveToRight();
                        break;
                }

                if (f){
                    step++;
                    addNum(1);
                    updateShow();
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
