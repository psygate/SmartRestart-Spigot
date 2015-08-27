/*


 The MIT License (MIT)

 Copyright (c) 2015 psygate (http://github.com/psygate)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package com.psygate.smartrestart.runnables;

import com.psygate.smartrestart.SmartRestart;
import com.psygate.smartrestart.data.EventType;
import com.psygate.smartrestart.data.RestartCriteria;
import com.psygate.smartrestart.data.TickingRestartCriteria;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author psygate (http://github.com/psygate)
 */
public class RestartHandler implements Runnable {

    private static final SortedMap<Long, String> limits = new TreeMap<>();
    private static final String msgTemplate = SmartRestart.PREFIX + ChatColor.YELLOW + "$REASON$ scheduled $TYPE$ in $TIME$.";

    ;

    static {
        for (int i = 60; i >= 10; i -= 10) {
            limits.put(TimeUnit.MINUTES.toMillis(i), i + " minutes");
        }

        for (int i = 5; i >= 1; i--) {
            limits.put(TimeUnit.MINUTES.toMillis(i), i + " minutes");
        }

        for (int i = 30; i >= 10; i -= 5) {
            limits.put(TimeUnit.SECONDS.toMillis(i), i + " seconds");
        }

        for (int i = 1; i < 10; i++) {
            limits.put(TimeUnit.SECONDS.toMillis(i), i + " seconds");
        }
    }

    private final List<RestartCriteria> criteria = new ArrayList<>(10);
    private final List<TickingRestartCriteria> tickcriteria = new ArrayList<>(10);
    private boolean isRestart = false;
    private SortedMap<Long, String> warns = new TreeMap<>();
    private long scheduled = -1;
    private String reason = "";
    private long restartAfter = TimeUnit.MINUTES.toMillis(5);
    private EventType type = EventType.RESTART;

    @Override
    public void run() {
        for (TickingRestartCriteria crit : tickcriteria) {
            crit.tick();
        }
        SmartRestart smart = SmartRestart.getInstance();
        Checker checker = smart.getChecker();

        if (checker.getTicks() < 0) {
            return;
        }
        if (!isRestart) {
            boolean timegated = System.currentTimeMillis() - smart.getLastRestart() < smart.getConf().getTimeout();
            for (RestartCriteria crit : criteria) {
                if (crit.isCriteriaViolated() && !crit.isLockOutAffected()) {
                    triggerRestart(crit);
                } else if (crit.isCriteriaViolated() && timegated) {
                    crit.cancelledByTimeout();
                } else if (crit.isCriteriaViolated()) {
                    triggerRestart(crit);
                }
            }
        } else {
            if (!warns.isEmpty() && restartAfter - warns.lastKey() <= (System.currentTimeMillis() - scheduled)) {
                String msg = msgTemplate.replace("$REASON$", reason)
                        .replace("$TYPE$", type.name())
                        .replace("$TIME$", warns.get(warns.lastKey()));
                Bukkit.broadcastMessage(msg);
                warns.remove(warns.lastKey());
            }

            if (restartAfter <= (System.currentTimeMillis() - scheduled)) {
                Bukkit.broadcastMessage(SmartRestart.PREFIX + ChatColor.RED + type.name());
                SmartRestart.getInstance().saveLastRestart();
                if (type == EventType.RESTART) {
                    Bukkit.spigot().restart();
                } else if (type == EventType.STOP) {
                    Bukkit.shutdown();
                }
            }
        }
    }

    public void addRestartCriteria(TickingRestartCriteria criteria) {
        this.criteria.add(criteria);
        this.tickcriteria.add(criteria);
    }

    public void addRestartCriteria(RestartCriteria criteria) {
        this.criteria.add(criteria);
    }

    public void clearCriteria() {
        this.criteria.clear();
        this.tickcriteria.clear();
    }

    private void triggerRestart(RestartCriteria crit) {
        if (!isRestart) {
            this.reason = crit.getReason();
            this.restartAfter = crit.restartAfterMillis();
            type = crit.getType();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(this.restartAfter);
            Bukkit.broadcastMessage(SmartRestart.PREFIX + ChatColor.YELLOW + reason + " Scheduled restart in " + minutes + " minutes.");
            warns = new TreeMap<>(limits);
            Iterator<Long> it = warns.keySet().iterator();

            while (it.hasNext()) {
                if (it.next() > this.restartAfter) {
                    it.remove();
                }
            }

            isRestart = true;
            scheduled = System.currentTimeMillis();
        }
    }
}
