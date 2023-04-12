package com.dori.Dori90v.config;

import com.dori.Dori90v.Server;
import com.dori.Dori90v.client.character.MapleAccount;
import com.dori.Dori90v.client.character.MapleChar;
import com.dori.Dori90v.connection.crypto.MapleCrypto;
import com.dori.Dori90v.connection.netty.ChannelHandler;
import com.dori.Dori90v.connection.netty.ChatAcceptor;
import com.dori.Dori90v.connection.netty.LoginAcceptor;
import com.dori.Dori90v.enums.CharacterGender;
import com.dori.Dori90v.enums.ServiceType;
import com.dori.Dori90v.logger.Logger;
import com.dori.Dori90v.services.*;
import com.dori.Dori90v.wzHandlers.ItemDataHandler;
import com.dori.Dori90v.wzHandlers.MapDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {
    // Logger -
    private final Logger logger = new Logger(ServerConfig.class);

    // Services -
    @Autowired
    private MapleAccountService accountService;
    @Autowired
    private MapleCharService charService;

    @Bean
    CommandLineRunner cmdLineRunner(){
        return args -> {
            logger.serverNotice("Start Loading Server...");
            // Initiate maple crypto -
            MapleCrypto.initialize();
            // Register all the Packet handlers -
            ChannelHandler.initHandlers(false);

            // Start Login acceptor -
            new Thread(new LoginAcceptor()).start();
            // Start Chat acceptor -
            new Thread(new ChatAcceptor()).start();

            // Init Maple Worlds -
            Server.initMapleWorlds();

            // Load WZ data -
            MapDataHandler.loadMapData();
            MapDataHandler.loadWorldMapData();
            ItemDataHandler.loadItemData();
            ItemDataHandler.loadEquipData();
            //TODO: next is NPC & MOBS and then QUESTS

            // Register Services to MainService -
            ServiceManager.registerNewService(ServiceType.Equip, EquipService.getInstance());
            ServiceManager.registerNewService(ServiceType.Inventory, InventoryService.getInstance());
            ServiceManager.registerNewService(ServiceType.Item, ItemService.getInstance());
            ServiceManager.registerNewService(ServiceType.Account, MapleAccountService.getInstance());
            ServiceManager.registerNewService(ServiceType.Character, MapleCharService.getInstance());

            // Adding admin account -
            MapleAccount adminAccount = new MapleAccount("admin","admin",true);
            accountService.addNewEntity(adminAccount);
            logger.notice("Added admin account :D");
            MapleChar adminChar = new MapleChar(adminAccount.getId(),"Dori", CharacterGender.Boy.getValue());
            charService.addNewEntity(adminChar);
            logger.serverNotice("~ Server is Ready ~");
        };
    }
}
