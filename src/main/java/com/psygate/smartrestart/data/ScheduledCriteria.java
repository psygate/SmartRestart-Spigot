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
package com.psygate.smartrestart.data;

import com.psygate.smartrestart.SmartRestart;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 * @author psygate (http://github.com/psygate)
 */
public class ScheduledCriteria implements RestartCriteria {

    @Override
    public String getReason() {
        return "Scheduled Restart";
    }

    @Override
    public boolean isLockOutAffected() {
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

    @Override
    public EventType getType() {
        return EventType.RESTART;
    }

    @Override
    public long restartAfterMillis() {
        return TimeUnit.MINUTES.toMillis(10);
    }

}
