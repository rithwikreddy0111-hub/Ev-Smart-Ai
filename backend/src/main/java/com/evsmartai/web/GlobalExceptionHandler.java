package com.evsmartai.web;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
 private static final Logger log=LoggerFactory.getLogger(GlobalExceptionHandler.class);
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){Map<String,String> fields=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->fields.putIfAbsent(x.getField(),x.getDefaultMessage()));return ResponseEntity.badRequest().body(Map.of("message","Validation failed","fields",fields));}
 @ExceptionHandler(NoSuchElementException.class) ResponseEntity<?> missing(NoSuchElementException e){return ResponseEntity.status(404).body(Map.of("message",e.getMessage()==null?"Not found":e.getMessage()));}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<?> bad(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()==null?"Invalid request":e.getMessage()));}
 // These used to fall through to the catch-all below and come back as a misleading 500.
 @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class,MissingServletRequestParameterException.class,ConstraintViolationException.class}) ResponseEntity<?> malformed(Exception e){return ResponseEntity.badRequest().body(Map.of("message","Invalid or missing request data"));}
 @ExceptionHandler(HttpRequestMethodNotSupportedException.class) ResponseEntity<?> method(HttpRequestMethodNotSupportedException e){return ResponseEntity.status(405).body(Map.of("message","Method not allowed"));}
 @ExceptionHandler(NoResourceFoundException.class) ResponseEntity<?> notFound(NoResourceFoundException e){return ResponseEntity.status(404).body(Map.of("message","Not found"));}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> conflict(DataIntegrityViolationException e){log.warn("Data integrity violation: {}",e.getMostSpecificCause().getMessage());return ResponseEntity.status(409).body(Map.of("message","This data conflicts with an existing record"));}
 @ExceptionHandler(Exception.class) ResponseEntity<?> other(Exception e){log.error("Unhandled exception",e);return ResponseEntity.status(500).body(Map.of("message","Unexpected server error"));}
}
