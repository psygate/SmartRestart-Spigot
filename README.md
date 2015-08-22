SmartRestart-Spigot
===================

What does it do?
----------------
Allows to set limits for a server restart. Currently based on memory usage, tick time (tick below 12 for instance) and scheduled (every 24 hours).

### Base Configuration

```
# Valid time suffixes are:
# - d: day
# - h: hour
# - m: minute
# - s: seconds
# - ms: milli seconds
# You can use 3d4h23m10s19ms as a time value in all time configuration values.
>
#Prevents the server from restarting in this time period. Scheduled is not affected by this.
Restart-Timeout: 30m
>
# shorthand notations:
# k - 1000
# c - 100
# m - 1000000
Max-Log-Size: 10k
# Enables restart on memory limit.
Memory-Limit-Enabled: true
# Set the memory limit for a restart.
Memory-Limit: 90%
# Enables restart on time.
Scheduled-Restart-Enabled: true
#This forces a restart every X hours.
Restart-Force-Hours: 24h
# Enables restart on tick below a certain level.
Tick-Restart-Enabled: true
# Restart if tick falls below:
Restart-On-Tick-Below: 12
```

- Restart-Timeout affects constraints. If this period hasn't expired since the last restart, the server will not attempt another restart. This is primarily here to prevent the "up-down" syndrom. If the server starts with a bad tick or bad memory vales, this will prevent it from shutting down instantly.
- Time values can be provided in a 3d4h23m10s19ms format. Overflowing values as in "500m" are allowed.
- Max-Log-Size is for the telemetry command, that shows some verbose output about server state.
- Memory-Limit-Enabled is for the memory limit constraint. Once the memory limit provided by Memory-Limit has been reached, the server will restart. If this is set to true, the memory limit constraint is enabled.
- Scheduled-Restart-Enabled is for the scheduled restarter. Once the time period provided by Restart-Force-Hours has expired, the server will restart. This is not affected by the Restart-Timeout value and will always happen.
- Tick-Restart-Enabled is for the tick limit constraint. Once the tick is below Restart-On-Tick-Below, the server will restart.
