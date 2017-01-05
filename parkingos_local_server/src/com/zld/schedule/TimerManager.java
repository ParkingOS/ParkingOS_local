package com.zld.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.schedule.DeleteSchedule;

/**
 * 每个月的第一天的2：00执行分红计算
 * @author Administrator
 *
 */
public class TimerManager {
	private static final long PERIOD_DAY = 24*60*60 * 1000;

	public TimerManager(DataBaseService dataBaseService) {
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.HOUR_OF_DAY, 2);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		Date date = calendar.getTime();
//		// 第一次执行定时任务的时间
//		// 如果第一次执行定时任务的时间 小于 当前的时间
//		// 此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
//		if (date.before(new Date())) {
//			date = this.addDay(date, 1);
//			//System.out.println("加一天，一天后的2：00执行");
//		}
//		Timer timer = new Timer();
//		ParkSchedule task = new ParkSchedule(dataBaseService);
		// 安排指定的任务在指定的时间开始进行重复的固定延迟执行。
//		timer.schedule(task, new Date(), PERIOD_DAY);
		System.out.println(" ////////////////start//////////////////////");
		 ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);
		 DeleteSchedule task1 = new DeleteSchedule();//定时删除图片

		    executor.scheduleAtFixedRate(
		      task1,
		      60,
		      60*60*12,
		      TimeUnit.SECONDS);

		 SyncFromLineSchedule task2 = new SyncFromLineSchedule(dataBaseService);//定时同步云端的修改

		    executor.scheduleAtFixedRate(
		      task2,
		      45000L,
		      Long.valueOf(CustomDefind.SYNCFROM),
		      TimeUnit.MILLISECONDS);
		    SyncToLineSchedule task3 = new SyncToLineSchedule(dataBaseService);//定时上传数据给云端

		    executor.scheduleAtFixedRate(
		      task3,
		      40000L,
		      Long.valueOf(CustomDefind.SYNCTO),
		      TimeUnit.MILLISECONDS);
		    AutoUpdateSchedule task4 = new AutoUpdateSchedule(dataBaseService);//定时获取服务器更新包

		    executor.scheduleAtFixedRate(
		      task4,
		      36000L,
		      Long.valueOf(CustomDefind.AUTO),
		      TimeUnit.MILLISECONDS);

//		timer.schedule(task, date, PERIOD_DAY);
	}

	// 增加或减少天数

	public Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}
}
