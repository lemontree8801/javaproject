/*
 * Copyright (C) 2010-2101 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.otter.shared.etl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.alibaba.otter.shared.common.model.config.channel.ChannelParameter.SyncConsistency;
import com.alibaba.otter.shared.common.model.config.channel.ChannelParameter.SyncMode;
import com.alibaba.otter.shared.common.utils.OtterToStringStyle;

/**
 * 每条变更数据.
 * 
 * @author xiaoqing.zhouxq 2011-8-9 下午03:42:20
 */
public class EventData implements ObjectData, Serializable {

    private static final long serialVersionUID = -7071677425383765372L;

    /**
     * otter内部维护的一套tableId，与manager中得到的table Id对应
     */
    private long              tableId          = -1;

    private String            tableName;

    private String            schemaName;

    /**
     * 逻辑库名，用于分库分表
     */
    private String            logicSchemaName;

    /**
     * 变更数据的业务类型(I/U/D/C/A/E),与canal中的EntryProtocol中定义的EventType一致.
     */
    private EventType         eventType;

    /**
     * redis command
     */
    private String            redisEventType;

    /**
     * 变更数据的业务时间.
     */
    private long              executeTime;

    /**
     * 变更前的主键值,如果是insert/delete变更前和变更后的主键值是一样的.
     */
    private List<EventColumn> oldKeys          = new ArrayList<>();

    /**
     * 变更后的主键值,如果是insert/delete变更前和变更后的主键值是一样的.
     */
    private List<EventColumn> keys             = new ArrayList<>();

    /**
     * 非主键的其他字段
     */
    private List<EventColumn> columns          = new ArrayList<>();

    // ====================== 运行过程中对数据的附加属性 =============================
    /**
     * 预计的size大小，基于binlog event的推算
     */
    private long              size             = 1024;

    /**
     * 同步映射关系的id
     */
    private long              pairId           = -1;

    /**
     * 当eventType =
     * CREATE/ALTER/ERASE时，就是对应的sql语句，其他情况为动态生成的INSERT/UPDATE/DELETE sql
     */
    private String            sql;

    /**
     * ddl/query的schemaName，会存在跨库ddl，需要保留执行ddl的当前schemaName
     */
    private String            ddlSchemaName;

    /**
     * 自定义的同步模式, 允许覆盖默认的pipeline parameter，比如针对补救数据同步
     */
    private SyncMode          syncMode;


    /**
     * 自定义的同步一致性，允许覆盖默认的pipeline parameter，比如针对字段组强制反查数据库
     */
    private SyncConsistency   syncConsistency;

    /**
     * 是否为remedy补救数据，比如回环补救自动产生的数据，或者是freedom产生的手工订正数据
     */
    private boolean           remedy           = false;

    /**
     * 生成对应的hint内容
     */
    private String            hint;

    /**
     * 生成sql是否忽略schema,比如针对tddl/drds,需要忽略schema
     */
    private boolean           withoutSchema    = false;

    private boolean           usingShard       = false;

    private SourcingType sourcingType;

    public SourcingType getSourcingType() {
        return sourcingType;
    }

    public void setSourcingType(SourcingType sourcingType) {
        this.sourcingType = sourcingType;
    }

    public String getRedisEventType() {
        return redisEventType;
    }

    public void setRedisEventType(String redisEventType) {
        this.redisEventType = redisEventType;
    }

    public static enum SourcingType {
        /** mysql DB */
        MYSQL,
        /** localBinLog */
        LOCALBINLOG,
        /** oracle DB */
        ORACLE,
        /** 多库合并模式 */
        GROUP,
        /** TiDB */
        TiDB,
        /** Redis */
        REDIS;

        public boolean isMysql() {
            return this.equals(SourcingType.MYSQL);
        }

        public boolean isLocalBinlog() {
            return this.equals(SourcingType.LOCALBINLOG);
        }

        public boolean isOracle() {
            return this.equals(SourcingType.ORACLE);
        }

        public boolean isGroup() {
            return this.equals(SourcingType.GROUP);
        }

        public boolean isTiDB() {
            return this.equals(SourcingType.TiDB);
        }

        public boolean isRedis() {
            return this.equals(SourcingType.REDIS);
        }
    }

    public HashMap<String, String> getProps() {
        return props;
    }

    public void setProps(HashMap<String, String> props) {
        this.props = props;
    }

    /**
     * 扩展属性.
     */
    private HashMap<String,String> props;

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public List<EventColumn> getKeys() {
        return keys;
    }

    public void setKeys(List<EventColumn> keys) {
        this.keys = keys;
    }

    public List<EventColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<EventColumn> columns) {
        this.columns = columns;
    }

    public long getPairId() {
        return pairId;
    }

    public void setPairId(long pairId) {
        this.pairId = pairId;
    }

    public List<EventColumn> getOldKeys() {
        return oldKeys;
    }

    public void setOldKeys(List<EventColumn> oldKeys) {
        this.oldKeys = oldKeys;
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }

    public SyncConsistency getSyncConsistency() {
        return syncConsistency;
    }

    public void setSyncConsistency(SyncConsistency syncConsistency) {
        this.syncConsistency = syncConsistency;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDdlSchemaName() {
        return ddlSchemaName;
    }

    public void setDdlSchemaName(String ddlSchemaName) {
        this.ddlSchemaName = ddlSchemaName;
    }

    public boolean isRemedy() {
        return remedy;
    }

    public void setRemedy(boolean remedy) {
        this.remedy = remedy;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isWithoutSchema() {
        return withoutSchema;
    }

    public void setWithoutSchema(boolean withoutSchema) {
        this.withoutSchema = withoutSchema;
    }

    public void setUsingShard(boolean usingShard) {
        this.usingShard = usingShard;
    }

    public boolean isUsingShard() {
        return usingShard;
    }

    public void setLogicSchemaName(String logicSchemaName) {
        this.logicSchemaName = logicSchemaName;
    }

    public String getLogicSchemaName() {
        return logicSchemaName;
    }

    // ======================== helper method =================

    /**
     * 返回所有待变更的字段
     */
    public List<EventColumn> getUpdatedColumns() {
        List<EventColumn> columns = new ArrayList<EventColumn>();
        for (EventColumn column : this.columns) {
            if (column.isUpdate()) {
                columns.add(column);
            }
        }

        return columns;
    }

    private List<EventColumn> cloneColumn(List<EventColumn> columns) {
        if (columns == null) {
            return null;
        }

        List<EventColumn> cloneColumns = new ArrayList<EventColumn>();
        for (EventColumn column : columns) {
            cloneColumns.add(column.clone());
        }

        return cloneColumns;
    }

    public EventData clone() {
        EventData data = new EventData();
        data.setTableId(tableId);
        data.setTableName(tableName);
        data.setSchemaName(schemaName);
        data.setDdlSchemaName(ddlSchemaName);
        data.setEventType(eventType);
        data.setExecuteTime(executeTime);
        data.setKeys(cloneColumn(keys));
        data.setColumns(cloneColumn(columns));
        data.setOldKeys(cloneColumn(oldKeys));
        data.setSize(size);
        data.setPairId(pairId);
        data.setSql(sql);
        data.setSyncMode(syncMode);
        data.setSyncConsistency(syncConsistency);
        data.setRemedy(remedy);
        data.setHint(hint);
        data.setWithoutSchema(withoutSchema);
        data.setProps((HashMap<String, String>) props.clone());
        data.setSourcingType(sourcingType);
        data.setRedisEventType(redisEventType);
        return data;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, OtterToStringStyle.DEFAULT_STYLE);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columns == null) ? 0 : columns.hashCode());
        result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
        result = prime * result + (int) (executeTime ^ (executeTime >>> 32));
        result = prime * result + ((keys == null) ? 0 : keys.hashCode());
        result = prime * result + ((oldKeys == null) ? 0 : oldKeys.hashCode());
        result = prime * result + (int) (pairId ^ (pairId >>> 32));
        result = prime * result + ((schemaName == null) ? 0 : schemaName.hashCode());
        result = prime * result + (int) (tableId ^ (tableId >>> 32));
        result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
        result = prime * result + ((props == null) ? 0 : props.hashCode());
        result = prime * result + ((sourcingType == null) ? 0 : sourcingType.hashCode());
        result = prime * result + ((redisEventType == null) ? 0 : redisEventType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        EventData other = (EventData) obj;
        if (columns == null) {
            if (other.columns != null) return false;
        } else if (!columns.equals(other.columns)) return false;
        if (eventType != other.eventType) return false;
        if (executeTime != other.executeTime) return false;
        if (keys == null) {
            if (other.keys != null) return false;
        } else if (!keys.equals(other.keys)) return false;
        if (oldKeys == null) {
            if (other.oldKeys != null) return false;
        } else if (!oldKeys.equals(other.oldKeys)) return false;
        if (pairId != other.pairId) return false;
        if (schemaName == null) {
            if (other.schemaName != null) return false;
        } else if (!schemaName.equals(other.schemaName)) return false;
        if (tableId != other.tableId) return false;
        if (tableName == null) {
            if (other.tableName != null) return false;
        } else if (!tableName.equals(other.tableName)) return false;
        if (props == null) {
            if (other.props != null) return false;
        } else if (!props.equals(other.props)) return false;
        if (sourcingType == null) {
            if (other.sourcingType != null) return false;
        } else if (!sourcingType.equals(other.sourcingType)) return false;
        if (redisEventType == null) {
            if (other.redisEventType != null) return false;
        } else if (!redisEventType.equals(other.redisEventType)) return false;
        return true;
    }
}
