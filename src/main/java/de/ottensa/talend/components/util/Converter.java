package de.ottensa.talend.components.util;

import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.io.Serializable;
import java.util.*;

public class Converter implements Serializable {

    private final RecordBuilderFactory recordBuilderFactory;

    public Converter(RecordBuilderFactory recordBuilderFactory) {
        this.recordBuilderFactory = recordBuilderFactory;
    }

    public Map recordToMap(Record record) {
        if (record == null) {
            return null;
        }

        Map<String, Object> object = new HashMap<>();

        for (Schema.Entry entry : record.getSchema().getEntries()) {
            String name = entry.getName();
            Schema.Type type = entry.getType();

            switch (type) {
                case RECORD:
                    object.put(name, recordToMap(record.getRecord(name)));
                    break;
                case ARRAY:
                    object.put(name, record.getArray(Object.class, name));
                    break;
                case STRING:
                    object.put(name, record.getString(name));
                    break;
                case BYTES:
                    object.put(name, record.getBytes(name));
                    break;
                case INT:
                    object.put(name, record.getInt(name));
                    break;
                case LONG:
                    object.put(name, record.getLong(name));
                    break;
                case FLOAT:
                    object.put(name, record.getFloat(name));
                    break;
                case DOUBLE:
                    object.put(name, record.getDouble(name));
                    break;
                case BOOLEAN:
                    object.put(name, record.getBoolean(name));
                    break;
                case DATETIME:
                    object.put(name, record.getDateTime(name));
                    break;
            }

        }

        return object;
    }

    public Record mapToRecord(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Record.Builder builder = recordBuilderFactory.newRecordBuilder();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object val = entry.getValue();
            String key = entry.getKey();
            if (val instanceof Map) {
                builder.withRecord(key, mapToRecord((Map) val));
            } else if (val instanceof Collection) {
                List<Record> records = new ArrayList<>();
                for (Object o : (Collection) val) {
                    records.add(mapToRecord((Map<String, Object>) o));
                }

                Schema.Entry e = recordBuilderFactory.newEntryBuilder()
                        .withName(key)
                        .withType(Schema.Type.ARRAY)
                        .withElementSchema(records.get(0).getSchema())
                        .build();
                builder.withArray(e, records);
            } else if (val instanceof String) {
                builder.withString(key, (String) val);
            } else if (val instanceof byte[]) {
                builder.withBytes(key, (byte[]) val);
            } else if (val instanceof Date) {
                builder.withDateTime(key, (Date) val);
            } else if (val instanceof Integer) {
                builder.withInt(key, (int) val);
            } else if (val instanceof Long) {
                builder.withLong(key, (long) val);
            } else if (val instanceof Float) {
                builder.withFloat(key, (float) val);
            } else if (val instanceof Double) {
                builder.withDouble(key, (double) val);
            } else if (val instanceof Boolean) {
                builder.withBoolean(key, (boolean) val);
            }
        }

        return builder.build();
    }
}
