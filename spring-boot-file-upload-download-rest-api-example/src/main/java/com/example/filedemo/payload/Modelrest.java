package com.example.filedemo.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Modelrest {

	private String fileName;
	
    @JsonInclude(JsonInclude.Include.NON_NULL) 
	private List<Modelrest> mock;
	
	private String base64;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<Modelrest> getMock() {
		return mock;
	}
	public void setMock(List<Modelrest> mock) {
		this.mock = mock;
	}
	public String getBase64() {
		return base64;
	}
	public void setBase64(String base64) {
		this.base64 = base64;
	}

	public Modelrest(String fileName, List<Modelrest> mock, String base64) {
		super();
		this.fileName = fileName;
		this.mock = mock;
		this.base64 = base64;
	}

	public Modelrest(String fileName, String base64) {
		super();
		this.fileName = fileName;
		this.base64 = base64;
	}



}
