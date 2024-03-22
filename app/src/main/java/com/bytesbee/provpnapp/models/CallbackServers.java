package com.bytesbee.provpnapp.models;

import java.util.ArrayList;
import java.util.List;

public class CallbackServers {
    public int status;
    public String message;
    public int count_total = -1;
    public List<VPNServers> posts = new ArrayList<>();
}