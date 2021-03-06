package com.capgemini.tlta.exception;
import java.util.Date;

/**
 * The Class ErrorDetails.
 */
public class ErrorDetails {
	private Date timestamp;
	private String message;
	private String details;
	
	/**
	 * Instantiates a new error details.
	 *
	 * @param timestamp the timestamp
	 * @param message the message
	 * @param details the details
	 */
	public ErrorDetails(Date timestamp, String message, String details) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
}