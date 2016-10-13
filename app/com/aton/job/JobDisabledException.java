
package com.aton.job;

public class JobDisabledException extends RuntimeException {

    public JobDisabledException() {
		super();
    }
    
	public JobDisabledException(Throwable cause) {
		super(cause);
	}
	
    public JobDisabledException(String message, Throwable cause) {
		super(message, cause);
    }

    public JobDisabledException(String message) {
		super(message);
    }

    @Override
    public String toString() {
        return "JobDisabledException [message=" + getMessage() +  "]";
    }


}
