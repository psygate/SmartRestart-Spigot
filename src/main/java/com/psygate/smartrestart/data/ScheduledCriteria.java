/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartrestart.data;

import com.psygate.smartrestart.SmartRestart;
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
        return System.currentTimeMillis() - SmartRestart.getInstance().getLastRestart() > SmartRestart.getInstance().getConf().getForceHours();
    }

    @Override
    public void cancelledByTimeout() {
        SmartRestart.getInstance().getLogger().log(Level.WARNING, "Cancelled timeout should NOT affect scheduled restarter.");
    }

    @Override
    public String getName() {
        return "scheduled";
    }

}
