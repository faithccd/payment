package com.faithccd.payment.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", " Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "C002", " Invalid Input Value"),
    ENTITY_NOT_FOUND(400, "C003", " Entity Not Found"),
    INTERNAL_SERVER_ERROR(500, "C004", "Server Error"),
    INVALID_TYPE_VALUE(400, "C005", " Invalid Type Value"),
    HANDLE_ACCESS_DENIED(403, "C006", "Access is Denied"),
    
    // RULE        
    INVAILD_VAT(400, "P001", " VAT can't be greater then payment amount !!"),
    INVAILD_CANCEL_AMT(400, "P002", " Cancel Amount can't be greater then balance amount !!"),
    INVAILD_CANCEL_VAT(400, "P003", " Cancel VAT can't be greater then balance VAT !!")
    ;
	
    private final String code;
    private final String message;
    private int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status =  status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
    
	public static final String EXECUTE_RULEQUERY_ERROR = "9903";
	
	public static final String MANDATORY_ERROR = "9904";
	
	public static final String FORMAT_ERROR = "9905";
	
	public static final String INVALID_CODE = "9906";
	
	public static final String SYSTEM_ERROR = "9907";    

}
