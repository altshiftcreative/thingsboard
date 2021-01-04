package com.asc.bluewaves.lwm2m.model;

import java.io.Serializable;
import java.util.List;

import org.eclipse.leshan.core.request.BindingMode;

import lombok.Data;

@Data
public class ServerDTO implements Serializable {

	private int shortServerId;
	private long lifetime;
//	    private Long defaultMinPeriod;
//	    private Long defaultMaxPeriod;
	private BindingMode binding;
	private boolean notifyWhenDisable;

}
