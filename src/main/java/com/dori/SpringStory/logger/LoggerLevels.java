package com.dori.SpringStory.logger;

public enum LoggerLevels {
    DEBUG(0),
    INFO(1),
    NOTICE (2),
    SERVER_NOTICE (3),
    WARNING(4),
    ERROR(5),
    ;

    private final Integer lvl;

    public Integer getLvl() {
        return lvl;
    }

    LoggerLevels(final Integer currLevel){
        this.lvl = currLevel;
    }

    @Override
    public String toString(){
        return this.lvl.toString();
    }
}
