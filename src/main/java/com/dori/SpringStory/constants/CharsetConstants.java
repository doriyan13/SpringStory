/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dori.SpringStory.constants;

/*
 * Thanks to GabrielSin (EllinMS) - gabrielsin@playellin.net
 * Ellin
 * MapleStory Server
 * CharsetConstants
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

public class CharsetConstants {
    private static final Logger log = LoggerFactory.getLogger(CharsetConstants.class);
    public static final Charset CHARSET = loadCharset();

    private enum Language {
        LANGUAGE_US("US-ASCII"),
        LANGUAGE_PT_BR("ISO-8859-1"),
        LANGUAGE_THAI("TIS620"),
        LANGUAGE_KOREAN("MS949");

        private final String charset;

        Language(String charset) {
            this.charset = charset;
        }

        public String getCharset() {
            return charset;
        }

        public static Language fromCharset(String charset) {
            Optional<Language> language = Arrays.stream(values())
                    .filter(l -> l.charset.equals(charset))
                    .findAny();
            if (language.isEmpty()) {
                log.warn("Charset {} was not found, defaulting to US-ASCII", charset);
                return LANGUAGE_US;
            }

            return language.get();
        }
    }

    private static Charset loadCharset() {
        // TODO: maybe recreate the config concept using YAML files?
        // ...

        return StandardCharsets.US_ASCII;
    }

    private static class StrippedYamlConfig {
        public StrippedServerConfig server;

        private static class StrippedServerConfig {
            public String CHARSET;
        }
    }
}