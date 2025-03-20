/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pm.authservice.exception;

/**
 *
 * @author solon
 */
@SuppressWarnings("MissingSummary")
public class NotFoundException extends RuntimeException{
     public NotFoundException(String message) {
        super(message);
    }
}
