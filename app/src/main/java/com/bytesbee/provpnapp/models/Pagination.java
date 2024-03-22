package com.bytesbee.provpnapp.models;

import static com.bytesbee.provpnapp.constants.IConstants.LOAD_MORE;
import static com.bytesbee.provpnapp.constants.IConstants.ONE;

import java.io.Serializable;

public class Pagination extends APIKey implements Serializable {
    private int count = LOAD_MORE;//10
    private int page = ONE; //1

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
