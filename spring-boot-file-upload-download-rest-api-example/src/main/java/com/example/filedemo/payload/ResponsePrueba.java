package com.example.filedemo.payload;

public class ResponsePrueba {

	private String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public ResponsePrueba() {
		super();
	}

	
	public ResponsePrueba(String data) {
		super();
		this.data = data;
	}

	@Override
	public String toString() {
		return "ResponsePrueba [data=" + data + "]";
	}

	
	
}
