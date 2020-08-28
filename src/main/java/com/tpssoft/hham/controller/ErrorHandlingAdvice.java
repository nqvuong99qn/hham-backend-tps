package com.tpssoft.hham.controller;

import com.tpssoft.hham.exception.AuthenticationFailureException;
import com.tpssoft.hham.exception.DuplicatedEmailException;
import com.tpssoft.hham.exception.DuplicatedNameException;
import com.tpssoft.hham.exception.NegativeFundException;
import com.tpssoft.hham.exception.NoPrivilegeException;
import com.tpssoft.hham.exception.PasswordRequirementsNotMeetException;
import com.tpssoft.hham.exception.ResourceNotFoundException;
import com.tpssoft.hham.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.mail.SendFailedException;

@ControllerAdvice
@ResponseBody
public class ErrorHandlingAdvice {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse wrongDataFormat() {
        // This can happen when client sent an array or something else instead of a JSON object
        return new ErrorResponse(1, "Wrong data format");
    }

    @ExceptionHandler(AuthenticationFailureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse wrongCredential() {
        return new ErrorResponse(2, "Incorrect username or password");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse resourceNotFound(ResourceNotFoundException exception) {
        return new ErrorResponse(3, exception.getMessage());
    }

    @ExceptionHandler(NoPrivilegeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse missingOrInvalidActivationToken(NoPrivilegeException exception) {
        return new ErrorResponse(4, exception.getMessage());
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse duplicatedEmail(DuplicatedEmailException exception) {
        return new ErrorResponse(5, exception.getMessage());
    }

    @ExceptionHandler(DuplicatedNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse duplicatedName(DuplicatedNameException exception) {
        return new ErrorResponse(6, exception.getMessage());
    }

    @ExceptionHandler(PasswordRequirementsNotMeetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse passwordRequirementsNotMeet(PasswordRequirementsNotMeetException exception) {
        return new ErrorResponse(7, exception.getMessage());
    }

    @ExceptionHandler(SendFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse emailFailedToSend() {
        return new ErrorResponse(8, "Failed to send email");
    }

    @ExceptionHandler(NegativeFundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse negativeFund() {
        return new ErrorResponse(9,
                "Operation canceled. Fund will become negative after the operation");
    }
}
