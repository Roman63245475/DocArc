package com.example.docarc.be;

import java.util.List;

public interface IDataSettable<T extends Data> {

    public void setData(List<T> data);
    public int getId();
}
