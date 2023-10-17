package com.example.navershoppingproductprice.Utility;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;


import java.util.ArrayList;
import java.util.List;

public class BatchListWriter<T> implements ItemWriter<List<T>> {
    private final JdbcBatchItemWriter<T> itemWriter;
    public BatchListWriter(JdbcBatchItemWriter<T> _itemWriter){
        this.itemWriter = _itemWriter;
    }
    @Override
    public void write(Chunk<? extends List<T>> items) {
        List<T> currentList = new ArrayList<>();
        for(List<T> subList : items){
            currentList.addAll(subList);
        }
        try {
            itemWriter.write(new Chunk<>(currentList));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
