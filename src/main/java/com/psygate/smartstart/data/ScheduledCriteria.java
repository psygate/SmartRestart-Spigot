/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart.data;

import com.psygate.smartstart.SmartStart;
import java.util.logging.Level;

/**
 *
 * @author florian
 */
public class ScheduledCriteria implements RestartCriteria {

    @Override
    public String getReason() {
        return "Scheduled Restart";
    }

    @Override
    public boolean LockOutAffected() {
        return false;
    }

    @Override
    public boolean isCriteriaViolated() {
        return System.currentTimeMillis() - SmartStart.getInstance().getLastRestart() > SmartStart.getInstance().getConf().getForceHours();
    }

    @Override
    public void cancelledByTimeout() {
        SmartStart.getInstance().getLogger().log(Level.WARNING, "Cancelled timeout should NOT affect scheduled restarter.");
    }

    @Override
    public String getName() {
        return "scheduled";
    }

}
