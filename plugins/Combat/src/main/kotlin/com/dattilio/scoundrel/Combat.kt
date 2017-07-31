package com.dattilio.scoundrel

import com.dattilio.klient.api.LinePreprocessor
import org.slf4j.LoggerFactory
import ro.fortsoft.pf4j.Extension
import ro.fortsoft.pf4j.Plugin
import ro.fortsoft.pf4j.PluginWrapper

class Combat(wrapper: PluginWrapper) : Plugin(wrapper) {
    private val logger = LoggerFactory.getLogger(Combat::class.java)

    override fun start() {
        logger.info("Combat.start()")
    }

    override fun stop() {
        logger.info("Combat.stop()")
    }

    @Extension
    class CombatLinePreprocessor : LinePreprocessor {
        private val logger = LoggerFactory.getLogger(Combat::class.java)
        override fun preProcessLine(line: String): String {
            logger.info("[C] " + line)
            return line
        }

    }

}