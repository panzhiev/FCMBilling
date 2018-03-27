package com.matthewmitchell.emercoinj.params;

import com.matthewmitchell.emercoinj.core.NetworkParameters;

public class EmercoinNetParams extends NetworkParameters {

    private static EmercoinNetParams instance;

    EmercoinNetParams() {
        addressHeader = 0x21;
        p2shHeader = 5;
        dumpedPrivateKeyHeader = 0x80;
        packetMagic = 0xe6e8e9e5;
        acceptableAddressCodes = new int[]{addressHeader, p2shHeader};
    }

    public static synchronized NetworkParameters get() {
        if (instance == null) {
            instance = new EmercoinNetParams();
        }
        Networks.register(instance);
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return "emcnet";
    }

    @Override
    public int getDumpedPrivateKeyHeader() {
        return dumpedPrivateKeyHeader;
    }
}
