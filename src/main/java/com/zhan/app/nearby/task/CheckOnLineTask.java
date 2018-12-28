package com.zhan.app.nearby.task;

public class CheckOnLineTask {
	private boolean isDoing = false;
	public void start() {
		if (isDoing) {
			return;
		}
		isDoing = true;
	}
}
