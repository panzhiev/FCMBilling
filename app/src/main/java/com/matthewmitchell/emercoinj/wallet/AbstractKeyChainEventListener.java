package com.matthewmitchell.emercoinj.wallet;

import com.matthewmitchell.emercoinj.core.ECKey;

import java.util.List;

public class AbstractKeyChainEventListener implements KeyChainEventListener {
    @Override
    public void onKeysAdded(List<ECKey> keys) {
    }
}

