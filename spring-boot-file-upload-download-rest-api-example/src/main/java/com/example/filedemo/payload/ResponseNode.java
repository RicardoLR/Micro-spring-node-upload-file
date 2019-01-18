package com.example.filedemo.payload;

import java.io.IOException;

import org.springframework.boot.json.JsonParser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseNode {


	private String data;
	private  String data2;

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getData2() {
		return data2;
	}
	public void setData2(String data2) {
		this.data2 = data2;
	}

    //@JsonCreator(mode=JsonCreator.Mode.DELEGATING)
	public ResponseNode(String data, String data2) {
		super();
		this.data = data;
		this.data2 = data2;
	}
	
	public ResponseNode(String data) {
		super();
		this.data = data;	
	}    
	public ResponseNode() {
		super();
	}
    
	
	@Override
	public String toString() {
		return "ResponseNode [data=" + data + ", data2=" + data2 + "]";
	}



}
