package com.dpsg.cyberx;

import java.util.List;

// Stub for AppOpsManager.PackageOps
public class HiddenAppOpsStubs {
    public String getPackageName() { return null; }
    public List<OpEntryStub> getOps() { return null; }
}

// Stub for AppOpsManager.OpEntry
class OpEntryStub {
    public String getOpStr() { return null; }
    public List<HistoricalOpStub> getHistoricalOps() { return null; }
}

// Stub for AppOpsManager.HistoricalOp
class HistoricalOpStub {
    public long getDuration() { return 0; }
    public long getTime() { return 0; }
}
