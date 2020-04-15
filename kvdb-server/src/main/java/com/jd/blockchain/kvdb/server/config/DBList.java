package com.jd.blockchain.kvdb.server.config;

import com.jd.blockchain.kvdb.protocol.exception.KVDBException;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 数据库配置，已开启服务的数据库会在kvdb-server启动后自动创建或加载
 */
public class DBList {

    private static final String PROPERTITY_PREFIX = "db";
    private static final String PROPERTITY_SEPARATOR = ".";
    private static final String PROPERTITY_ENABLE = "enable";
    private static final String PROPERTITY_ROOTDIR = "rootdir";
    private static final String PROPERTITY_PARTITIONS = "partitions";

    private String configFile;

    // 数据库名做主键
    private Map<String, DBInfo> dbs = new HashMap<>();

    /**
     * 解析并保存dblist中配置项
     * <p>
     * 校验：
     * 1. 不能存在同名数据库
     *
     * @param configFile
     * @param kvdbConfig
     * @throws IOException
     */
    public DBList(String configFile, KVDBConfig kvdbConfig) throws IOException {
        this.configFile = configFile;
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));
        Set<String> dbNames = new HashSet<>();
        for (Object key : properties.keySet()) {
            String[] ps = ((String) key).split("\\.");
            if (ps[2].equals(PROPERTITY_ENABLE)) {
                String dbName = ps[1];
                if (dbNames.contains(dbName)) {
                    throw new KVDBException("duplicate database name : " + dbName);
                }
                dbNames.add(dbName);
            }
        }
        for (String dbName : dbNames) {
            DBInfo config = new DBInfo();
            config.setName(dbName);
            config.setEnable(Boolean.parseBoolean(properties.getProperty(PROPERTITY_PREFIX + PROPERTITY_SEPARATOR + dbName + PROPERTITY_SEPARATOR + PROPERTITY_ENABLE)));
            if (!config.isEnable()) {
                continue;
            }
            config.setDbRootdir(properties.getProperty(PROPERTITY_PREFIX + PROPERTITY_SEPARATOR + dbName + PROPERTITY_SEPARATOR + PROPERTITY_ROOTDIR, kvdbConfig.getDbsRootdir()));
            config.setPartitions(Integer.parseInt(properties.getProperty(PROPERTITY_PREFIX + PROPERTITY_SEPARATOR + dbName + PROPERTITY_SEPARATOR + PROPERTITY_PARTITIONS, String.valueOf(kvdbConfig.getDbsPartitions()))));
            dbs.put(dbName, config);
        }
    }

    /**
     * 获取当前服务器所有开启服务的数据库实例列表
     *
     * @return
     */
    public DBInfo[] getDatabaseArray() {
        return dbs.values().toArray(new DBInfo[dbs.size()]);
    }

    /**
     * 获取当前服务器所有开启服务的数据库实例名称集
     *
     * @return
     */
    public Set<String> getDatabaseNameSet() {
        return dbs.keySet();
    }

    /**
     * 保存新创建数据库信息
     *
     * @param dbInfo
     * @throws IOException
     */
    public void createDatabase(DBInfo dbInfo) throws IOException {
        FileWriter fw = null;
        try {
            fw = new FileWriter(configFile, true);
            fw.write("\n" + String.format(PROPERTITY_PREFIX + PROPERTITY_SEPARATOR + dbInfo.getName() + PROPERTITY_SEPARATOR + PROPERTITY_ENABLE + "=true"));
            fw.write("\n" + String.format(PROPERTITY_PREFIX + PROPERTITY_SEPARATOR + dbInfo.getName() + PROPERTITY_SEPARATOR + PROPERTITY_ROOTDIR + "=" + dbInfo.getDbRootdir()));
            fw.write("\n" + String.format(PROPERTITY_PREFIX + PROPERTITY_SEPARATOR + dbInfo.getName() + PROPERTITY_SEPARATOR + PROPERTITY_PARTITIONS + "=" + dbInfo.getPartitions()));
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (null != fw) {
                    fw.close();
                }
            } catch (IOException e) {
            }
        }
    }

}
