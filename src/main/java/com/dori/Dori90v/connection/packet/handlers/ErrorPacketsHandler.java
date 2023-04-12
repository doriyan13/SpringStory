package com.dori.Dori90v.connection.packet.handlers;

import com.dori.Dori90v.client.MapleClient;
import com.dori.Dori90v.connection.packet.Handler;
import com.dori.Dori90v.connection.packet.InPacket;
import com.dori.Dori90v.logger.Logger;

import java.util.Arrays;

import static com.dori.Dori90v.connection.packet.headers.InHeader.*;

public class ErrorPacketsHandler {
    // Logger -
    private static final Logger logger = new Logger(ErrorPacketsHandler.class);


    @Handler(op = ExceptionLog)
    public static void handleExceptionLog(MapleClient c, InPacket inPacket){
        String debug = inPacket.decodeString();

        logger.notice("Packet (client) error has occurred -");
        logger.notice(debug);
    }

    @Handler(op = ClientDumpLog)
    public static void handleSendBackupPacket(MapleClient c, InPacket inPacket){
        // Error info -
        short nCallType = inPacket.decodeShort();
        int dwErrorCode = inPacket.decodeInt();
        short backupBufferSize = inPacket.decodeShort();
        int unk = inPacket.decodeInt();
        short opcodeHeader = inPacket.decodeShort();
        // Error code 0 usually used for login bounce back, thus not desired to log -
        if(dwErrorCode != 0) {
            logger.error("Error occurred for the client - " + c.getIP() +
                    ", type: " + nCallType +
                    ", errorCode: " + dwErrorCode +
                    ", data length: " + backupBufferSize +
                    ", unk: " + unk +
                    ", opcodeHeader: " + opcodeHeader +
                    ", remaining of the packet: " + Arrays.toString(inPacket.decodeArr(inPacket.getUnreadAmount()))
            );
        }
    }
}
