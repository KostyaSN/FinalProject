package com.test.myapplicationgetoiil.interfaces;

import com.test.myapplicationgetoiil.model.OwnLocation;

import java.util.List;

public interface iCallBackServices {
    public void success(List<OwnLocation> places);
    public void incrementProgressPercentage(int percentage);
    public void fail();
}
