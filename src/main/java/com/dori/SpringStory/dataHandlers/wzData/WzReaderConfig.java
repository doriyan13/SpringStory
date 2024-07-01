package com.dori.SpringStory.dataHandlers.wzData;

public record WzReaderConfig(byte[] iv,
                             int version) {
    public WzCrypto buildEncryptor() throws Exception {
        return WzCrypto.fromIv(iv());
    }
}
