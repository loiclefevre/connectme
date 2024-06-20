package com.oracle.connect;

public abstract class LoginAttempt implements Runnable {
	public final long loginDecisionTime;

	public LoginAttempt(long loginDecisionTime) {
		this.loginDecisionTime = loginDecisionTime;
	}
}
