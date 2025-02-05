package com.nm.order.management.common.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DateTimeMapper {

    public Timestamp convertDateToTimestamp(Date date) {
        long seconds = date.getTime() / 1_000;
        int nanos = (int) ((date.getTime() % 1_000) * 1_000_000);
        return Timestamp.newBuilder()
                .setSeconds(seconds)
                .setNanos(nanos)
                .build();
    }

    public Date convertTimestampToDate(Timestamp timestamp) {
        long millis = timestamp.getSeconds() * 1_000 + timestamp.getNanos() / 1_000_000;
        return new Date(millis);
    }

    public Timestamp convertLongToTimestamp(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        long seconds = timestamp / 1_000;
        int nanos = (int) ((timestamp % 1_000) * 1_000_000);
        return Timestamp.newBuilder()
                .setSeconds(seconds)
                .setNanos(nanos)
                .build();
    }
}
