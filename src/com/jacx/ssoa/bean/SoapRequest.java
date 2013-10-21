package com.jacx.ssoa.bean;

public class SoapRequest {
	
	private String requestMessage;
	private String endpoint;
	
	
	
	public SoapRequest(String requestMessage, String endpoint) {
		super();
		this.requestMessage = requestMessage;
		this.endpoint = endpoint;
	}
	
	public String getRequestMessage() {
		return requestMessage;
	}
	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	
	

}
