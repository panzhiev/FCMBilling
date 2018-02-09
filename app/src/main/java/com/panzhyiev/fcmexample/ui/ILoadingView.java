package com.panzhyiev.fcmexample.ui;


import java.util.ArrayList;

public interface ILoadingView {

    void showProgressIndicator();

    void hideProgressIndicator();

    void setList(ArrayList list);

    void reloadList(ArrayList list);

    void showError();
}
