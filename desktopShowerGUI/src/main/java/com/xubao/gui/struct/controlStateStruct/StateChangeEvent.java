package com.xubao.gui.struct.controlStateStruct;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/10
 */
public interface StateChangeEvent<T>
{
	boolean execute(T t) throws Exception;
}
