# 🍁 SpringStory
> Java 21.0.1 SpringBoot Server simulator for a v95 Shroom game.

## 🧾 Requirements
- JDK 21+ (recommend JDK 21.0.1).
- MySQL Server (currently working with mySQL server 8).

## 📦 Setup (Windows)
1. Download the src (recommend downloading master -> LTS).
2. Download MySQL server and set the password to be `admin` (if you want to set different password you just need to update the `application.properties`).
3. Open your favorite mySQL workbench (I use intellJ ultimate to connect to the DB).
4. Add new Schema called `SpringStory` (in the DB).
5. Create in the src a new Dir called `wz` and dump all the wz files into `img` files (you can use [WzDumper](https://github.com/Xterminatorz/WZ-Dumper)).
5. Run InitServer and let the server start generate the json folder with all the jsons data files.
6. Download the v95 client files ([Download Link](https://archive.org/download/GMSSetup93-133/GMS0095/)).
7. Open the Hendi v95 localhost ([Download Link](https://mega.nz/file/dWIgyR4I#6cDN_ycLLiFtad07Eby3UfjdY3TqGI65g6X-xEqlmds)).
8. Login via Admin account -
    > Username: `admin`
    > 
    > Password: `admin`

## 🎯 Feature List
- [x] Auto DB tables creation and mapping by @Entity classes (No more sql scripts to execute before start running the server).
- [x] Service classes for each Table (No more old school queries / sql statements for CRUD operations!).
- [x] WZ -> JSON files conversion (easier files reading and management / readability).
- [x] Parallel data loading for faster server loading.  
- [x] Event Driven approach for the game loop utilizing java 21 Virtual Threads (`EventManager.java`).
- [x] Reflection base handling to packets (Swordie style for much cleaner code / better performances imo).
- [x] Reflection base Commands handling (method base rather than Swordie inner static classes).
- [x] Custom Auto set of key mapping (hated to manually set my keybinding each time so the default is my preferred key binds).
- [x] Generic handling for all the classes buffs skills (no more class for each class to duplicate bunch of code to handle tempStats!).
  - Hot swap / reload ability for the tempStat handling - after handling a skill the server don't need a restart!
- [x] Custom Logger (made my own for better logs clarity to me, it's not optimized what-so-ever, but it's easier to develop that way to me).
- [x] Hotswap - Java / intellJ can be awesome so enjoy its features xD
- [x] Java + reflection base NPC scripting! (no more weird written and complex scripts)
- [x] Auto Register system to create new accounts.
- [x] Map/Item/Skill/Mobs search by name query based for better performances and nice utility to have.
- [x] Graceful shutdown.
- [x] Map caching & unloading after x amount of time for better memory management.
- [x] More cool stuff that I'm probably just don't remember xD

## 💻 Personal Note
This project is for learning and fun, I'm planing to continue work on, so it will be continued to be updated but not consistently.
Part of my plan was to suggest another way to go about creating a server and give good base for anyone that was to build on it
with more updated and better performances.

## ⭐️ Acknowledgements
* [RustMS](https://github.com/jon-zu/shroom-data) - Joo is the goat ❤
* [MAO Channel Server](https://github.com/RubenD96/Maple-Art-Online-Channel) - Chronos the Kotlin god ❤
* [Edelstein](https://github.com/Kaioru/Edelstein) - Got a lot of references packets handling from here. (Kairou thanks!)
* [Rebirth](https://github.com/67-6f-64/Rebirth95.Server) - Got a lot of references packets & tempStats handling from here. (Minimum Delta & Rajan Grewal thanks for being a legacy)
* [Moe Script API](https://github.com/y785/script-api) - Got cool ideas for my NPC scripting 😊
* [Swordie](https://bitbucket.org/swordiemen/swordie/src/master/) - Swordie gave me a lot of ideas on the architecture I went with in this project 🔥
