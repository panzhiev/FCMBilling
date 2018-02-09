package com.panzhyiev.fcmexample.ui;

public interface IBaseFragment extends ILoadingView{

    void onBackPressed();
    String getCurrentTag();
}
