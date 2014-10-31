package com.partmanager;

import java.util.TimerTask;

import com.partmanager.action.power.DataCopyAction;

public class TaskBuckup extends TimerTask {

		  public void run() {
			  //
			  DataCopyAction dd = new DataCopyAction();
			  dd.backup();
		  }

}