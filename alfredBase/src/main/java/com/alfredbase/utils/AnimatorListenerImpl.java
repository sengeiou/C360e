package com.alfredbase.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;

/**
 * AnimatorListener的一个简单实现，添加了isRunning变量，用于判断是否有动画正在运行，用于一些动画冲突的场景
 * 
 * @author 冯小卫 2014-4-1
 * 
 */
public class AnimatorListenerImpl implements AnimatorListener {

	public static boolean isRunning = false;

	@Override
	public void onAnimationStart(Animator animation) {
		isRunning = true;

	}

	@Override
	public void onAnimationEnd(Animator animation) {
		isRunning = false;
	}

	@Override
	public void onAnimationCancel(Animator animation) {
		isRunning = false;
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		isRunning = true;
	}

}
