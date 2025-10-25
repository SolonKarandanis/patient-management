package com.pm.creaditcardservice.repository.utils;/*
 * StatementParameter.java
 *
 * Created on 16 ����������� 2007, 1:43 ��
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.io.Serial;
import java.io.Serializable;

@SuppressWarnings("MissingSummary")
public final class StatementParameter implements Serializable {

    @Serial
    private static final long serialVersionUID = 0000000001;

    /**
     * Creates a new instance of StatementParameter
     */
    public StatementParameter() {
    }

    /**
     * Creates a new instance of StatementParameter
     */
    public StatementParameter(String name, Object value, String operator, String column, Class type) {
        this.setName(name);
        this.setValue(value);
        this.setOperator(operator);
        this.setColumn(column);
        this.type = type;
    }
    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property name.
     *
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property name.
     *
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Holds value of property value.
     */
    private Object value;

    /**
     * Getter for property value.
     *
     * @return Value of property value.
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Setter for property value.
     *
     * @param value New value of property value.
     */
    public void setValue(Object value) {
        this.value = value;
    }
    /**
     * Holds value of property operator.
     */
    private String operator;

    /**
     * Getter for property operator.
     *
     * @return Value of property operator.
     */
    public String getOperator() {
        return this.operator;
    }

    /**
     * Setter for property operator.
     *
     * @param operator New value of property operator.
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }
    /**
     * Holds value of property column.
     */
    private String column;

    /**
     * Getter for property column.
     *
     * @return Value of property column.
     */
    public String getColumn() {
        return this.column;
    }

    /**
     * Setter for property column.
     *
     * @param column New value of property column.
     */
    public void setColumn(String column) {
        this.column = column;
    }

    private Class type;

    /**
     * @return the type
     */
    public Class getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Class type) {
        this.type = type;
    }

    @Override
    public String toString(){
        return "com.pm.creaditcardservice.repository.util.StatementParameter[name=" + name+", value=" + value + ", operator=" + operator + ", column="+column + "]";
    }
}
