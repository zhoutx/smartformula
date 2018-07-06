package com.epri.dlsc.sbs.exception;

/**
 * 结算计算程序自定义异常
 * 
 * @author Zhong.Weijian
 *
 */
public class CalEngineException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CalEngineException() {
		super();
	}

	public CalEngineException(String s) {
		super(s);
	}
}
