/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart.data;

/**
 *
 * @author florian
 */
public interface RestartCriteria {

    public String getReason();

    public boolean LockOutAffected();

    public boolean isCriteriaViolated();

    public void cancelledByTimeout();

    public String getName();

}
