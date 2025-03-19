/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pm.authservice.model;

/**
 *
 * @author solon
 */
public enum AccountStatus {
    ACTIVE("account.active"),
    INACTIVE("account.inactive"),
    DELETED("account.deleted"),;
	
	private final String value;
	
	private AccountStatus(String value) {
        this.value = value;
    }
	
	public String getValue() {
        return value;
    }
}
