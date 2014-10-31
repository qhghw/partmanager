package com.partmanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyListener implements ServletContextListener {
  
  private Timer timer = null;
  private Timer timer2 = null;
  public void contextInitialized(ServletContextEvent event) {
    timer = new Timer(true);
    //设置任务计划，启动和间隔时间
 //   timer.schedule(new MyTask(), 0, 86400000);
    timer.schedule(new MyTask(), getTargetDate(1,0,1,0));
    
    timer2 = new Timer(true);
    //设置任务计划，启动和间隔时间
 //   timer.schedule(new MyTask(), 0, 86400000);
    timer2.schedule(new TaskBuckup(), getTargetDate(0,23,30,0), 24*60*60*1000);
  }

  public void contextDestroyed(ServletContextEvent event) {
    timer.cancel();
    timer2.cancel();
  }
  /**
   * 获取第一次要执行的时间
   * 参数说明：
   * day:一个月中的哪一天，默认为当前天
   * hour:几点          minute:多少分 second:多少秒  默认为00:00:00
   * 可根据自己需要修改
   */
  public static Date getTargetDate(int day,int hour,int minute,int second){
   SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   Date date=new Date();//当前时间
   Calendar calendar=Calendar.getInstance();
   calendar.setTime(date);

   if(day!=0){
    calendar.set(Calendar.DAY_OF_MONTH,day);//设置哪一天
   }
   calendar.set(Calendar.HOUR_OF_DAY,hour);//设置几点
   calendar.set(Calendar.MINUTE, minute);//设置多少分
   calendar.set(Calendar.SECOND, second);//设置多少秒

   if(calendar.getTime().getTime()<date.getTime()){
    //如果设置day 则认为是每个月执行，否则为每天执行
    if(day!=0){
     //获取下个月的规定时间
     calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
    }else{
     //获取第二天的规定时间
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
    }
   }
   System.out.println("下次任务开始时间："+sf.format(calendar.getTime()));
   return calendar.getTime();
  }
 
  
}