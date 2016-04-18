import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import com.typesafe.config.ConfigFactory
import net.logstash.logback.appender.LogstashTcpSocketAppender
import net.logstash.logback.encoder.LogstashTcpEncoder

def config = ConfigFactory.load()
def host

if(config.getBoolean('logstash.enabled')) {
    host = config.getString("logstash.host")
}

def appenders = ["CONSOLE"]

if(host) {
    appenders = ["LOGSTASH_ASYNC"]

    appender('LOGSTASH', LogstashTcpSocketAppender) {
        remoteHost = host
        port = 51515
        encoder(LogstashTcpEncoder)
    }

    appender('LOGSTASH_ASYNC', AsyncAppender) {
        appenderRef('LOGSTASH')
    }
}

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}

root(Level.INFO, appenders)