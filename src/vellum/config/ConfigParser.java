/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.config;

import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import java.io.*;
import vellum.util.Systems;

/**
 *
 * @author evan.summers
 */
public class ConfigParser {

    Logr logger = LogrFactory.getLogger(ConfigParser.class);
    InputStream inputStream;
    BufferedReader reader;
    ConfigSection configEntry;
    ConfigDocument configMap = new ConfigDocument();
    String line;
    String type;
    String name;
    boolean blockStarted = false;
    int lineCount = 0;

    public ConfigParser() {
    }

    public void init(InputStream inputStream) throws Exception {
        this.inputStream = inputStream;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            line = reader.readLine();
            if (line == null) {
                if (blockStarted) {
                    endBlock();
                }
                break;
            }
            lineCount++;
            line = line.trim();
            if (line.startsWith("#")) {
                continue;
            } else if (line.startsWith("//")) {
                continue;
            } else if (line.length() == 0) {
                continue;
            } else if (line.equals(".")) {
                break;
            } else if (!blockStarted) {
                startBlock();
            } else {
                parseBlock();
            }
        }
    }

    private void startBlock() {
        String[] tokens = line.split(" ");
        logger.trace("start", tokens.length, tokens);
        ConfigHeaderParser headerParser = new ConfigHeaderParser();
        if (headerParser.parse(line)) {
            type = headerParser.getType();
            name = headerParser.getName();
            configEntry = new ConfigSection(type, name);
            if (configMap.containsKey(configEntry.getKey())) {
                throw new ConfigParserException(ConfigExceptionType.DUPLICATE, lineCount, type, name, line);
            }
            blockStarted = true;
        } else {
            throw new ConfigParserException(lineCount, type, name, line);
        }
    }

    private void parseBlock() {
        if (Character.isLetter(line.charAt(0))) {
            parseLine();
        } else if (line.equals("}")) {
            endBlock();
        } else {
            throw new ConfigParserException(lineCount, type, name, line);
        }
    }

    private void parseLine() {
        logger.trace("parseLine", line);
        ConfigLineParser lineParser = new ConfigLineParser();
        if (lineParser.parse(line)) {
            configEntry.getProperties().put(lineParser.getKey(), lineParser.getValue());
            if (false) {
                System.out.printf("insert into config (group_, name_, value) values ('%s', %s', '%s');\n", 
                        name, lineParser.getKey(), lineParser.getValue());
            }
            logger.trace("parseLine lineParser", lineParser);
        } else {
            throw new ConfigParserException(lineCount, type, name, line);
        }
    }

    private void endBlock() {
        logger.trace("put", configEntry.getKey());
        configMap.put(configEntry);
        blockStarted = false;
    }

    public ConfigDocument getConfigMap() {
        return configMap;
    }

    public static ConfigDocument parseConfFile(String path) throws Exception {
        String confFileName = Systems.getPath(path);
        File confFile = new File(confFileName);
        return parse(new FileInputStream(confFile));
    }
    
    public static ConfigDocument parse(InputStream stream) throws Exception {
        ConfigParser parser = new ConfigParser();
        parser.init(stream);
        return parser.getConfigMap();
    }
}
