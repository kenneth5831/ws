package com.example.ws.util;

import com.example.ws.entity.Sequence;
import com.example.ws.mapper.SequenceMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class OrderUtil {

    private final SequenceMapper sequenceMapper;

    public OrderUtil(SequenceMapper sequenceMapper) {
        this.sequenceMapper = sequenceMapper;
    }

    /**
     * 生成訂單號：ORD + yyyyMMdd + 4位流水號
     */
    @Transactional
    public String generateOrderNumber() {
        Sequence seq = sequenceMapper.selectForUpdate("order");
        if (seq == null) {
            throw new IllegalStateException("找不到 order 對應的序列資料");
        }

        Long nextValue = seq.getCurrentValue() + 1;
        seq.setCurrentValue(nextValue);
        sequenceMapper.updateValue(seq);

        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String seqPart = String.format("%04d", nextValue);
        return "ORD" + datePart + seqPart;
    }
}